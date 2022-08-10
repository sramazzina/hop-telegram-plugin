/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.project.hop.pipeline.transforms.telegrambot;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.exception.HopTransformException;
import org.apache.hop.core.util.Utils;
import org.apache.hop.pipeline.Pipeline;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.transform.BaseTransform;
import org.apache.hop.pipeline.transform.TransformMeta;

import java.util.List;

/** Transform That contains the basic skeleton needed to create your own plugin */
public class TelegramBot extends BaseTransform<TelegramBotMeta, TelegramBotData> {

  private static final Class<?> PKG = TelegramBot.class; // Needed by Translator
  private static final String ROW_SEPARATOR = "\n";

  public TelegramBot(
      TransformMeta transformMeta,
      TelegramBotMeta meta,
      TelegramBotData data,
      int copyNr,
      PipelineMeta pipelineMeta,
      Pipeline pipeline) {
    super(transformMeta, meta, data, copyNr, pipelineMeta, pipeline);
  }

  @Override
  public boolean processRow() throws HopException {

    Object[] r = getRow(); // Get row from input rowset & set row busy!

    if (r == null) { // no more input to be expected...
      setOutputDone();
      return false;
    }

    if (first) { // use this block to do some processing that is only needed 1 time
      first = false;
    }

    data.outputRowMeta = getInputRowMeta().clone();

    if (meta.isEnableNotifications()) {
      String message = buildMessageToSend(r);

      // Create your bot passing the token received from @BotFather
      com.pengrad.telegrambot.TelegramBot bot =
          new com.pengrad.telegrambot.TelegramBot(meta.getBotToken());

      SendResponse res = bot.execute((new SendMessage(meta.getChatId(), message)).parseMode(ParseMode.MarkdownV2));

      if (!res.isOk())
        throw new HopException(
            "Unable to write a message to a Telegram chat: " + res.description());
    } else {

    }

    putRow(data.outputRowMeta, r); // return your data
    return true;
  }

  private String buildMessageToSend(Object[] r) throws HopTransformException {
    List<TelegramBotFieldItem> items = meta.getFieldItems();
    StringBuffer messageToSend = new StringBuffer();

    if (!Utils.isEmpty(meta.getNotificationHeaderText())) {
      messageToSend
          .append(meta.getNotificationHeaderText())
          .append(ROW_SEPARATOR);
    }
    for (TelegramBotFieldItem item : items) {

      if (messageToSend.length() > 0) messageToSend.append(ROW_SEPARATOR);

      int itemValuePos = data.outputRowMeta.indexOfValue(item.getFieldName());
      messageToSend.append("*").append(item.getFieldName()).append(":* ").append(r[itemValuePos]);
    }

    if (!Utils.isEmpty(meta.getNotificationFooterText())) {
      messageToSend
          .append(ROW_SEPARATOR)
          .append(ROW_SEPARATOR)
          .append(meta.getNotificationFooterText());
    }

    return messageToSend.toString();
  }

  @Override
  public boolean init() {
    if (super.init()) {
      if (Utils.isEmpty(meta.getBotToken())) {
        logError("Missing BOT Token value");
        return false;
      }

      if (Utils.isEmpty(meta.getChatId())) {
        logError("Missing ChatId value");
        return false;
      }

      if (meta.isEnableCommands()) {
        // TODO perform pipelines initialization

      }
      return true;
    }
    return false;
  }
}
