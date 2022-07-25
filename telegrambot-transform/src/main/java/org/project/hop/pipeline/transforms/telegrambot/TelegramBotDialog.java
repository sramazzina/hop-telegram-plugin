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

import org.apache.hop.core.util.Utils;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.transform.BaseTransformMeta;
import org.apache.hop.pipeline.transform.ITransformDialog;
import org.apache.hop.ui.core.ConstUi;
import org.apache.hop.ui.core.dialog.BaseDialog;
import org.apache.hop.ui.core.widget.TextVar;
import org.apache.hop.ui.pipeline.transform.BaseTransformDialog;
import org.apache.hop.ui.util.SwtSvgImageUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;

public class TelegramBotDialog extends BaseTransformDialog implements ITransformDialog {
  private static final Class<?> PKG = TelegramBotDialog.class; // Needed by Translator

  private final TelegramBotMeta input;
  private TextVar wBotToken;
  private TextVar wChatId;

  public TelegramBotDialog(
      Shell parent, IVariables variables, Object in, PipelineMeta pipelineMeta, String sname) {
    super(parent, variables, (BaseTransformMeta) in, pipelineMeta, sname);
    input = (TelegramBotMeta) in;
  }

  @Override
  public String open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.MIN | SWT.MAX | SWT.RESIZE);
    props.setLook(shell);
    shell.setMinimumSize(400, 520);
    setShellImage(shell, input);

    int margin = props.getMargin();
    int middle = props.getMiddlePct();

    ModifyListener lsMod = e -> input.setChanged();
    SelectionAdapter lsSelMod =
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent arg0) {
            input.setChanged();
          }
        };
    changed = input.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = 15;
    formLayout.marginHeight = 15;

    shell.setLayout(formLayout);
    shell.setText(BaseMessages.getString(PKG, "TelegramBot.Shell.Title"));

    // TransformName line
    wlTransformName = new Label(shell, SWT.RIGHT);
    wlTransformName.setText(BaseMessages.getString(PKG, "TelegramBot.TransformName.Label"));
    props.setLook(wlTransformName);
    fdlTransformName = new FormData();
    fdlTransformName.left = new FormAttachment(0, 0);
    fdlTransformName.top = new FormAttachment(0, 0);
    wlTransformName.setLayoutData(fdlTransformName);

    wTransformName = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wTransformName.setText(transformName);
    props.setLook(wTransformName);
    wTransformName.addModifyListener(lsMod);
    fdTransformName = new FormData();
    fdTransformName.width = 150;
    fdTransformName.left = new FormAttachment(0, 0);
    fdTransformName.top = new FormAttachment(wlTransformName, 5);
    wTransformName.setLayoutData(fdTransformName);

    Label spacer = new Label(shell, SWT.HORIZONTAL | SWT.SEPARATOR);
    FormData fdSpacer = new FormData();
    fdSpacer.height = 2;
    fdSpacer.left = new FormAttachment(0, 0);
    fdSpacer.top = new FormAttachment(wTransformName, 15);
    fdSpacer.right = new FormAttachment(100, 0);
    spacer.setLayoutData(fdSpacer);

    // BotToken
    Label wlBotToken = new Label(shell, SWT.RIGHT);
    wlBotToken.setText(BaseMessages.getString(PKG, "TelegramBot.BotToken.Label"));
    props.setLook(wlBotToken);
    FormData fdlBotToken = new FormData();
    fdlBotToken.left = new FormAttachment(0, 0);
    fdlBotToken.top = new FormAttachment(spacer, margin);
    fdlBotToken.right = new FormAttachment(middle, -margin);
    wlBotToken.setLayoutData(fdlBotToken);

    wBotToken = new TextVar(variables, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    props.setLook(wBotToken);
    wBotToken.addModifyListener(lsMod);
    FormData fdBotToken = new FormData();
    fdBotToken.left = new FormAttachment(middle, 0);
    fdBotToken.top = new FormAttachment(spacer, margin);
    fdBotToken.right = new FormAttachment(100, 0);
    wBotToken.setLayoutData(fdBotToken);

    // Chat ID
    Label wlChatId = new Label(shell, SWT.RIGHT);
    wlChatId.setText(BaseMessages.getString(PKG, "TelegramBot.ChatID.Label"));
    props.setLook(wlChatId);
    FormData fdlChatId = new FormData();
    fdlChatId.left = new FormAttachment(0, 0);
    fdlChatId.top = new FormAttachment(wBotToken, margin);
    fdlChatId.right = new FormAttachment(middle, -margin);
    wlChatId.setLayoutData(fdlChatId);

    wChatId = new TextVar(variables, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    props.setLook(wChatId);
    wChatId.addModifyListener(lsMod);
    FormData fdChatId = new FormData();
    fdChatId.left = new FormAttachment(middle, 0);
    fdChatId.top = new FormAttachment(wBotToken, margin);
    fdChatId.right = new FormAttachment(100, 0);
    wChatId.setLayoutData(fdChatId);

    // Some buttons
    wCancel = new Button(shell, SWT.PUSH);
    wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));
    FormData fdCancel = new FormData();
    fdCancel.right = new FormAttachment(100, 0);
    fdCancel.bottom = new FormAttachment(100, 0);
    wCancel.addListener(SWT.Selection, e -> cancel());
    wCancel.setLayoutData(fdCancel);

    wOk = new Button(shell, SWT.PUSH);
    wOk.setText(BaseMessages.getString(PKG, "System.Button.OK"));
    FormData fdOk = new FormData();
    fdOk.right = new FormAttachment(wCancel, -5);
    fdOk.bottom = new FormAttachment(100, 0);
    wOk.setLayoutData(fdOk);
    wOk.addListener(SWT.Selection, e -> ok());

    setButtonPositions(new Button[] {wOk, wCancel}, margin, null);

    // Set the shell size, based upon previous time...
    setSize();
    getData();

    input.setChanged(changed);

    BaseDialog.defaultShellHandling(shell, c -> ok(), c -> cancel());

    return transformName;
  }

  private Image getImage() {
    return SwtSvgImageUtil.getImage(
        shell.getDisplay(),
        getClass().getClassLoader(),
        "transform.svg",
        ConstUi.LARGE_ICON_SIZE,
        ConstUi.LARGE_ICON_SIZE);
  }

  /** Copy information from the meta-data input to the dialog fields. */
  public void getData() {

    // Get sample text and put it on dialog's text field
    if (!Utils.isEmpty(input.getBotToken()))
      wBotToken.setText(input.getBotToken());
    if (!Utils.isEmpty(input.getChatId()))
      wChatId.setText(input.getChatId());

    wTransformName.selectAll();
    wTransformName.setFocus();
  }

  /**
   * save data to metadata
   *
   * @param in
   */
  private void getInfo(TelegramBotMeta in) {
    // Save sample text content
    input.setBotToken(wBotToken.getText());
    input.setChatId(wChatId.getText());
  }

  /** Cancel the dialog. */
  private void cancel() {
    transformName = null;
    input.setChanged(changed);
    dispose();
  }

  private void ok() {
    if (Utils.isEmpty(wTransformName.getText())) {
      return;
    }

    getInfo(input);
    transformName = wTransformName.getText(); // return value
    dispose();
  }
}
