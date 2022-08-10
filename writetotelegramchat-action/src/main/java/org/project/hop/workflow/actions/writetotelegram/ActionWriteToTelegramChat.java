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

package org.project.hop.workflow.actions.writetotelegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.apache.hop.core.ICheckResult;
import org.apache.hop.core.Result;
import org.apache.hop.core.annotations.Action;
import org.apache.hop.core.exception.HopXmlException;
import org.apache.hop.core.util.Utils;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.core.xml.XmlHandler;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.workflow.WorkflowMeta;
import org.apache.hop.workflow.action.ActionBase;
import org.apache.hop.workflow.action.IAction;
import org.apache.hop.workflow.action.validator.ActionValidatorUtils;
import org.apache.hop.workflow.action.validator.AndValidator;
import org.w3c.dom.Node;

import java.util.List;

@Action(
    id = "ACTIONSAMPLE",
    name = "i18n::WriteToTelgramChatAction.Name",
    description = "i18n::WriteToTelgramChatAction.Description",
    image = "telegram.svg",
    categoryDescription = "Telegram.Category",
    documentationUrl = "" /*url to your documentation */)
public class ActionWriteToTelegramChat extends ActionBase implements Cloneable, IAction {

  private static final Class<?> PKG = ActionWriteToTelegramChat.class; // Needed by Translator

  private String botToken;
  private String chatId;
  private String chatMessage;

  public ActionWriteToTelegramChat(String name) {
    super(name, "");

    botToken = null;
    chatId = null;
    chatMessage = null;
  }

  public ActionWriteToTelegramChat() {
    this("");
  }

  public Object clone() {
    ActionWriteToTelegramChat c = (ActionWriteToTelegramChat) super.clone();
    return c;
  }

  /**
   * Save values to XML
   *
   * @return
   */
  @Override
  public String getXml() {
    StringBuilder retval = new StringBuilder();

    retval.append(super.getXml());
    // example value to xml
    retval.append("      ").append(XmlHandler.addTagValue("botToken", botToken));
    retval.append("      ").append(XmlHandler.addTagValue("chatId", chatId));
    retval.append("      ").append(XmlHandler.addTagValue("chatMessage", chatMessage));

    return retval.toString();
  }

  /**
   * Read the XML and get the values needed for the acton
   *
   * @param entrynode
   * @param metadataProvider
   * @throws HopXmlException
   */
  @Override
  public void loadXml(Node entrynode, IHopMetadataProvider metadataProvider, IVariables variables)
      throws HopXmlException {
    try {
      super.loadXml(entrynode);

      botToken = XmlHandler.getTagValue(entrynode, "botToken");
      chatId = XmlHandler.getTagValue(entrynode, "chatId");
      chatMessage = XmlHandler.getTagValue(entrynode, "chatMessage");
    } catch (Exception e) {
      throw new HopXmlException(
          BaseMessages.getString(PKG, "WriteToTelgramChatAction.UnableToLoadFromXml.Label"), e);
    }
  }

  /**
   * Execute this action and return the result. In this case it means, just set the result boolean
   * in the Result class.
   *
   * @param result The result of the previous execution
   * @return The Result of the execution.
   */
  @Override
  public Result execute(Result result, int nr) {

    // Create your bot passing the token received from @BotFather
    TelegramBot bot = new TelegramBot(botToken);

    SendResponse res =
        bot.execute((new SendMessage(chatId, chatMessage)).parseMode(ParseMode.MarkdownV2));
    if (!res.isOk()) {
      logError("Unable to write a message to a Telegram chat: " + res.description());
      result.setNrErrors(1);
    }

    if (result.getNrErrors() == 0) {
      result.setResult(true);
    } else {
      result.setResult(false);
    }

    return result;
  }

  /**
   * Add checks to report warnings
   *
   * @param remarks
   * @param workflowMeta
   * @param variables
   * @param metadataProvider
   */
  @Override
  public void check(
      List<ICheckResult> remarks,
      WorkflowMeta workflowMeta,
      IVariables variables,
      IHopMetadataProvider metadataProvider) {

    ActionValidatorUtils.andValidator()
            .validate(
                    this,
                    "botToken",
                    remarks,
                    AndValidator.putValidators(ActionValidatorUtils.notBlankValidator()));

    ActionValidatorUtils.andValidator()
            .validate(
                    this,
                    "chatId",
                    remarks,
                    AndValidator.putValidators(ActionValidatorUtils.notBlankValidator()));

  }

  public String getChatId() {
    return chatId;
  }

  public void setChatId(String chatId) {
    this.chatId = chatId;
  }

  public String getChatMessage() {
    return chatMessage;
  }

  public void setChatMessage(String chatMessage) {
    this.chatMessage = chatMessage;
  }

  public String getBotToken() {
    return botToken;
  }

  public void setBotToken(String botToken) {
    this.botToken = botToken;
  }
}
