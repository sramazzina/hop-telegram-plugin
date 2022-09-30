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

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;
import org.apache.hop.core.Result;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.exception.HopMissingPluginsException;
import org.apache.hop.core.exception.HopTransformException;
import org.apache.hop.core.exception.HopXmlException;
import org.apache.hop.core.row.RowDataUtil;
import org.apache.hop.core.util.Utils;
import org.apache.hop.pipeline.Pipeline;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.SingleThreadedPipelineExecutor;
import org.apache.hop.pipeline.TransformWithMappingMeta;
import org.apache.hop.pipeline.engines.local.LocalPipelineEngine;
import org.apache.hop.pipeline.transform.BaseTransform;
import org.apache.hop.pipeline.transform.ITransformMeta;
import org.apache.hop.pipeline.transform.TransformMeta;
import org.apache.hop.pipeline.transforms.injector.InjectorMeta;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/** Transform That contains the basic skeleton needed to create your own plugin */
public class TelegramBot extends BaseTransform<TelegramBotMeta, TelegramBotData> {

  private static final Class<?> PKG = TelegramBot.class; // Needed by Translator
  private static final String ROW_SEPARATOR = "\n";
  private boolean endLoop = false;

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

    if (meta.isEnableNotifications() && r == null) { // no more input to be expected...
      setOutputDone();
      return false;
    }

    if (meta.isEnableNotifications()
        && first) { // use this block to do some processing that is only needed 1 time
      first = false;
    }

    if (meta.isEnableNotifications()) {

      data.outputRowMeta = getInputRowMeta().clone();
      String message = buildMessageToSend(r);

      int msgIdPos = data.outputRowMeta.indexOfValue("MessageId");
      SendMessage msg = new SendMessage(meta.getChatId(), message);
      //      if (msgIdPos > -1) {
      //        msg.replyToMessageId(((Integer) r[msgIdPos]).intValue());
      //      }

      SendResponse res = data.bot.execute(msg.parseMode(ParseMode.MarkdownV2));

      if (!res.isOk())
        throw new HopException(
            "Unable to write a message to a Telegram chat: " + res.description());
      putRow(data.outputRowMeta, r); // return your data

    } else {

      long delay = 2000L;

      while (!endLoop) {
        GetUpdates getUpdates = new GetUpdates().limit(10).offset(data.startingOffset).timeout(0);

        data.bot.execute(
            getUpdates,
            new Callback<GetUpdates, GetUpdatesResponse>() {

              @Override
              public void onResponse(GetUpdates request, GetUpdatesResponse response) {
                List<Update> updates = response.updates();
                if (updates != null && updates.size() > 0) {
                  try {
                    processUpdates(updates);
                  } catch (HopException e) {
                    logError("An error occurred while processing Telegram updates", e);
                  }
                }
              }

              @Override
              public void onFailure(GetUpdates request, IOException e) {}
            });

        try {
          Thread.sleep(delay);
        } catch (InterruptedException e) {
          endLoop = true;
          break;
        }
      }
    }

    return true;
  }

  private void processUpdates(List<Update> updates) throws HopException {
    int currentMessageId = 0;
    for (Update u : updates) {
      if (u.message() != null) {
        Message m = u.message();
        logBasic("Message received: " + u.message().text() + " Message id: " + m.messageId());
      } else if (u.channelPost() != null) {
        Message m = u.channelPost();
        if (m != null && m.text() != null) {
          logBasic("ChannelPost message received: " + m.text() + " Message id: " + m.messageId());
          List<TelegramBotCmdItem> cmdItems = meta.getCmdItems();

          for (TelegramBotCmdItem item : cmdItems) {
            boolean pipelineInitialized = false;
            // String cmd = item.getCommandString();
            String cmd = meta.extractCommandFromMessage(m);
            if (cmd.equals(item.getCommandString())) {
              if (data.eMap.get(cmd) == null) {
                initProcessingPipeline(item.getPipelineToStart(), cmd);
              }

              pipelineInitialized = true;
            }

            if (pipelineInitialized) {
              Object[] outputRow = processMessageAsRow(m);
              data.rowProducer.putRow(data.outputRowMeta, outputRow);
              incrementLinesInput();
              SingleThreadedPipelineExecutor executor = data.eMap.get(cmd);
              if (executor == null)
                throw new HopException("Error while retrieving executor for command " + cmd);

              executor.oneIteration();

              // Test code
              /*
                          SendMessage msg =
                              new SendMessage(meta.getChatId(), "Request processed\\!")
                                  .parseMode(ParseMode.MarkdownV2)
                                  .replyToMessageId(m.messageId())
                                  .replyMarkup(new ForceReply());
                          SendResponse res = data.bot.execute(msg);

                          if (!res.isOk())
                            throw new HopException(
                                "Unable to write a message to a Telegram chat: " + res.description());
              */
            }
          }
        }
      }
      currentMessageId = u.updateId();
    }
    // Set new starting offset for next interaction
    data.startingOffset = currentMessageId + 1;
    logBasic(
        "Increment starting offset - new offset to start gathering messages is "
            + data.startingOffset);
  }

  public Object[] processMessageAsRow(Message m) {

    Object[] rowData = RowDataUtil.allocateRowData(data.outputRowMeta.size());

    int index = 0;
    rowData[index++] = m.messageId();
    rowData[index++] = m.text();
    rowData[index++] = meta.extractCommandFromMessage(m);
    rowData[index++] = meta.extractCommandArgsFromMessage(m);
    rowData[index++] = m.chat().title();
    rowData[index] = new Date();

    return rowData;
  }

  private void initProcessingPipeline(String pipelineFilename, String cmd) {

    try {
      PipelineMeta subTransMeta =
          new PipelineMeta(variables.resolve(pipelineFilename), metadataProvider, this);
      subTransMeta.setMetadataProvider(metadataProvider);
      subTransMeta.setFilename(pipelineFilename);
      subTransMeta.setPipelineType(PipelineMeta.PipelineType.SingleThreaded);
      logDetailed("Loaded processing pipeline '" + pipelineFilename + "'");

      LocalPipelineEngine processingPipeline =
          new LocalPipelineEngine(subTransMeta, this, getPipeline());
      processingPipeline.prepareExecution();
      processingPipeline.setLogLevel(getPipeline().getLogLevel());
      processingPipeline.setPreviousResult(new Result());
      TransformWithMappingMeta.replaceVariableValues(processingPipeline, this);
      TransformWithMappingMeta.addMissingVariables(processingPipeline, this);
      processingPipeline.activateParameters(processingPipeline);

      logDetailed("Initialized sub-pipeline '" + pipelineFilename + "'");

      // Find the Injector step as entry point to the processing pipeline
      for (TransformMeta transformMeta : subTransMeta.getTransforms()) {
        ITransformMeta iTransform = transformMeta.getTransform();
        if (iTransform instanceof InjectorMeta) {
          if (data.rowProducer != null) {
            throw new HopException(
                "You can only have one copy of the injector transform '"
                    + transformMeta.getName()
                    + "' to accept the telegram messages");
          }
          // Attach an injector to this transform
          //
          data.rowProducer = processingPipeline.addRowProducer(transformMeta.getName(), 0);
        }
      }

      if (data.rowProducer == null) {
        throw new HopException(
            "Unable to find an Injector transform in the Kafka pipeline. Such a transform is needed to accept data from this Kafka Consumer transform.");
      }

      // See if we need to grab result records from the sub-pipeline...
      //
      //      if (StringUtils.isNotEmpty(meta.getSubTransform())) {
      //        ITransform transform = processingPipeline.findRunThread(meta.getSubTransform());
      //        if (transform == null) {
      //          throw new HopException(
      //                  "Unable to find transform '" + meta.getSubTransform() + "' to retrieve
      // rows from");
      //        }
      //        transform.addRowListener(
      //                new RowAdapter() {
      //
      //                  @Override
      //                  public void rowWrittenEvent(IRowMeta rowMeta, Object[] row)
      //                          throws HopTransformException {
      //                    // Write this row to the next transform(s)
      //                    //
      //                    KafkaConsumerInput.this.putRow(rowMeta, row);
      //                  }
      //                });
      //      }

      processingPipeline.setLogChannel(getLogChannel());
      processingPipeline.startThreads();

      SingleThreadedPipelineExecutor executor =
          new SingleThreadedPipelineExecutor(processingPipeline);
      boolean ok = executor.init();
      if (!ok) {
        throw new HopException("Initialization of sub-pipeline failed");
      }
      getPipeline().addActiveSubPipeline(getTransformName(), processingPipeline);

      data.eMap.put(cmd, executor);
    } catch (HopXmlException e) {
      e.printStackTrace();
    } catch (HopMissingPluginsException e) {
      e.printStackTrace();
    } catch (HopException e) {
      e.printStackTrace();
    }
  }

  private String buildMessageToSend(Object[] r) throws HopTransformException {
    List<TelegramBotFieldItem> items = meta.getFieldItems();
    StringBuffer messageToSend = new StringBuffer();

    if (!Utils.isEmpty(meta.getNotificationHeaderText())) {
      messageToSend.append(meta.getNotificationHeaderText()).append(ROW_SEPARATOR);
    }

    for (TelegramBotFieldItem item : items) {
      if (messageToSend.length() > 0) messageToSend.append(ROW_SEPARATOR);
      int itemValuePos = data.outputRowMeta.indexOfValue(item.getFieldName());
      if (meta.isOmitFieldName()) {
        messageToSend.append(r[itemValuePos]);
      } else {
        messageToSend.append("*").append(item.getFieldName()).append(":* ").append(r[itemValuePos]);
      }
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

      // Create your bot passing the token received from @BotFather
      data.bot = new com.pengrad.telegrambot.TelegramBot(meta.getBotToken());
      if (data.bot == null) {
        logError("Error while trying to initialize a TelegramBot for token: " + meta.getBotToken());
        return false;
      }

      try {
        data.outputRowMeta = meta.getRowMeta(getTransformName(), this);
      } catch (HopTransformException e) {
        log.logError("Error determining output row metadata", e);
      }

      data.eMap = new HashMap<>();
      data.startingOffset = 0;
      return true;
    }
    return false;
  }

  @Override
  public void stopRunning() throws HopException {
    endLoop = true;
    Set<String> keys = data.eMap.keySet();
    for (String key : keys) {

      SingleThreadedPipelineExecutor e = data.eMap.get(key);
      e.getPipeline().stopAll();
    }
    super.stopRunning();
  }
}
