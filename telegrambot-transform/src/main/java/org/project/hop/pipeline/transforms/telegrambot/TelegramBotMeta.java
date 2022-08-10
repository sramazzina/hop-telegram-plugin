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

import org.apache.hop.core.ICheckResult;
import org.apache.hop.core.annotations.Transform;
import org.apache.hop.core.exception.HopTransformException;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.row.IValueMeta;
import org.apache.hop.core.row.value.ValueMetaString;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.transform.BaseTransformMeta;
import org.apache.hop.pipeline.transform.TransformMeta;

import java.util.List;

/** Meta data for the sample transform. */
@Transform(
    id = "TelegramBot",
    name = "i18n::TelegramBot.Name",
    description = "i18n::TelegramBot.Description",
    image = "telegram.svg",
    categoryDescription = "Category",
    documentationUrl = "" /*url to your documentation */)
public class TelegramBotMeta extends BaseTransformMeta<TelegramBot, TelegramBotData> {

  private static final Class<?> PKG = TelegramBotMeta.class; // Needed by Translator
  public static final String SAMPLE_TEXT_FIELD_NAME = "Value";

  @HopMetadataProperty(
      injectionKeyDescription = "TelegramBot.Injection.BotToken.Description")
  private String botToken;

  @HopMetadataProperty(
      injectionKeyDescription = "TelegramBot.Injection.ChatId.Description")
  private String chatId;

  @HopMetadataProperty(
          injectionKeyDescription = "TelegramBot.Injection.EnableCommands.Description")
  private boolean enableCommands;

  @HopMetadataProperty(key = "cmdItem", groupKey = "cmdItems")
  private List<TelegramBotCmdItem> cmdItems;

  @HopMetadataProperty(
          injectionKeyDescription = "TelegramBot.Injection.EnableNotifications.Description")
  private boolean enableNotifications;

  @HopMetadataProperty(
          injectionKeyDescription = "TelegramBot.Injection.NotificationHeader.Description")
  private String notificationHeaderText;

  @HopMetadataProperty(
          injectionKeyDescription = "TelegramBot.Injection.NotificationFooter.Description")
  private String notificationFooterText;

  @HopMetadataProperty(key = "fieldItem", groupKey = "fieldItems")
  private List<TelegramBotFieldItem> fieldItems;

  public List<TelegramBotCmdItem> getCmdItems() {
    return cmdItems;
  }

  public void setCmdItems(List<TelegramBotCmdItem> cmdItems) {
    this.cmdItems = cmdItems;
  }

  public String getBotToken() {
    return botToken;
  }

  public void setBotToken(String botToken) {
    this.botToken = botToken;
  }

  public String getChatId() {
    return chatId;
  }

  public void setChatId(String chatId) {
    this.chatId = chatId;
  }

  public boolean isEnableCommands() {
    return enableCommands;
  }

  public void setEnableCommands(boolean enableCommands) {
    this.enableCommands = enableCommands;
  }

  public boolean isEnableNotifications() {
    return enableNotifications;
  }

  public void setEnableNotifications(boolean enableNotifications) {
    this.enableNotifications = enableNotifications;
  }

  public List<TelegramBotFieldItem> getFieldItems() {
    return fieldItems;
  }

  public void setFieldItems(List<TelegramBotFieldItem> fieldItems) {
    this.fieldItems = fieldItems;
  }

  public String getNotificationHeaderText() {
    return notificationHeaderText;
  }

  public void setNotificationHeaderText(String notificationHeaderText) {
    this.notificationHeaderText = notificationHeaderText;
  }

  public String getNotificationFooterText() {
    return notificationFooterText;
  }

  public void setNotificationFooterText(String notificationFooterText) {
    this.notificationFooterText = notificationFooterText;
  }


  @Override
  public void check(
      List<ICheckResult> remarks,
      PipelineMeta pipelineMeta,
      TransformMeta transforminfo,
      IRowMeta prev,
      String[] input,
      String[] output,
      IRowMeta info,
      IVariables variables,
      IHopMetadataProvider metadataProvider) {
    // Checks to perform when validating a transform
  }

  @Override
  public void setDefault() {
    // Set default value for new sample text field
  }
}
