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

import org.apache.hop.core.Const;
import org.apache.hop.core.Props;
import org.apache.hop.core.util.Utils;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.ui.core.gui.WindowProperty;
import org.apache.hop.ui.core.widget.ControlSpaceKeyAdapter;
import org.apache.hop.ui.core.widget.TextVar;
import org.apache.hop.ui.pipeline.transform.BaseTransformDialog;
import org.apache.hop.ui.workflow.action.ActionDialog;
import org.apache.hop.ui.workflow.dialog.WorkflowDialog;
import org.apache.hop.workflow.WorkflowMeta;
import org.apache.hop.workflow.action.IAction;
import org.apache.hop.workflow.action.IActionDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;

public class ActionWriteToTelegramChatDialog extends ActionDialog implements IActionDialog {
  private static final Class<?> PKG = ActionWriteToTelegramChatDialog.class; // Needed by Translator

  private Shell shell;

  private ActionWriteToTelegramChat action;

  private boolean changed;

  private Text wName;
  private TextVar wBotToken;
  private TextVar wChatId;
  private TextVar wChatMessage;

  public ActionWriteToTelegramChatDialog(
      Shell parent, IAction action, WorkflowMeta workflowMeta, IVariables variables) {
    super(parent, workflowMeta, variables);
    this.action = (ActionWriteToTelegramChat) action;
    if (this.action.getName() == null) {
      this.action.setName(BaseMessages.getString(PKG, "WriteToTelgramChatAction.Label"));
    }
  }

  @Override
  public IAction open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.MIN | SWT.MAX | SWT.RESIZE);
    props.setLook(shell);
    WorkflowDialog.setShellImage(shell, action);

    ModifyListener lsMod = (ModifyEvent e) -> action.setChanged();
    changed = action.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout(formLayout);
    shell.setText(BaseMessages.getString(PKG, "WriteToTelgramChatAction.Title"));

    int middle = props.getMiddlePct();
    int margin = Const.MARGIN;


    Button wOk = new Button(shell, SWT.PUSH);
    wOk.setText(BaseMessages.getString(PKG, "System.Button.OK"));
    Button wCancel = new Button(shell, SWT.PUSH);
    wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

    // at the bottom
    BaseTransformDialog.positionBottomButtons(shell, new Button[] {wOk, wCancel}, margin, wName);

    // Add listeners
    wCancel.addListener(SWT.Selection, (Event e) -> cancel());
    wOk.addListener(SWT.Selection, (Event e) -> ok());

    // Filename line
    Label wlName = new Label(shell, SWT.RIGHT);
    wlName.setText(BaseMessages.getString(PKG, "WriteToTelgramChatAction.Label"));
    props.setLook(wlName);
    FormData fdlName = new FormData();
    fdlName.left = new FormAttachment(0, 0);
    fdlName.right = new FormAttachment(middle, -margin);
    fdlName.top = new FormAttachment(0, margin);
    wlName.setLayoutData(fdlName);
    wName = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    props.setLook(wName);
    wName.addModifyListener(lsMod);
    FormData fdName = new FormData();
    fdName.left = new FormAttachment(middle, 0);
    fdName.top = new FormAttachment(0, margin);
    fdName.right = new FormAttachment(100, 0);
    wName.setLayoutData(fdName);

    // BotToken
    Label wlBotToken = new Label(shell, SWT.RIGHT);
    wlBotToken.setText(BaseMessages.getString(PKG, "WriteToTelgramChatAction.BotToken.Label"));
    props.setLook(wlBotToken);
    FormData fdlBotToken = new FormData();
    fdlBotToken.left = new FormAttachment(0, 0);
    fdlBotToken.top = new FormAttachment(wName, margin);
    fdlBotToken.right = new FormAttachment(middle, -margin);
    wlBotToken.setLayoutData(fdlBotToken);

    wBotToken = new TextVar(variables, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    props.setLook(wBotToken);
    wBotToken.addModifyListener(lsMod);
    FormData fdBotToken = new FormData();
    fdBotToken.left = new FormAttachment(middle, 0);
    fdBotToken.top = new FormAttachment(wName, margin);
    fdBotToken.right = new FormAttachment(100, 0);
    wBotToken.setLayoutData(fdBotToken);

    // Chat ID
    Label wlChatId = new Label(shell, SWT.RIGHT);
    wlChatId.setText(BaseMessages.getString(PKG, "WriteToTelgramChatAction.ChatID.Label"));
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

    // Chat message to send
    Label wlChatMessage = new Label(shell, SWT.RIGHT);
    wlChatMessage.setText(BaseMessages.getString(PKG, "WriteToTelgramChatAction.ChatMessage.Label"));
    props.setLook(wlChatMessage);
    FormData fdlChatMessage = new FormData();
    fdlChatMessage.left = new FormAttachment(0, 0);
    fdlChatMessage.top = new FormAttachment(wChatId, margin);
    fdlChatMessage.right = new FormAttachment(middle, -margin);
    wlChatMessage.setLayoutData(fdlChatMessage);

    wChatMessage =
            new TextVar(
                    variables, shell, SWT.MULTI | SWT.LEFT | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    props.setLook(wChatMessage, Props.WIDGET_STYLE_FIXED);
    wChatMessage.addModifyListener(lsMod);
    FormData fdChatMessage = new FormData();
    fdChatMessage.left = new FormAttachment(middle, 0);
    fdChatMessage.top = new FormAttachment(wChatId, margin);
    fdChatMessage.right = new FormAttachment(100, 0);
    fdChatMessage.bottom = new FormAttachment(wOk, -margin);
    wChatMessage.setLayoutData(fdChatMessage);
    wChatMessage.addKeyListener(new ControlSpaceKeyAdapter(variables, wChatMessage));

    getData();

    BaseTransformDialog.setSize(shell);

    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    return action;
  }

  public void dispose() {
    WindowProperty winprop = new WindowProperty(shell);
    props.setScreen(winprop);
    shell.dispose();
  }

  /** Copy information from the meta-data input to the dialog fields. */
  public void getData() {
    wName.setText(Const.nullToEmpty(action.getName()));
    wBotToken.setText(Const.nullToEmpty(action.getBotToken()));
    wChatId.setText(Const.nullToEmpty(action.getChatId()));
    wChatMessage.setText(Const.nullToEmpty(action.getChatMessage()));

    wName.selectAll();
    wName.setFocus();
  }

  private void cancel() {
    action.setChanged(changed);
    action = null;
    dispose();
  }

  private void ok() {
    if (Utils.isEmpty(wName.getText())) {
      MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
      mb.setText(BaseMessages.getString(PKG, "System.TransformActionNameMissing.Title"));
      mb.setMessage(BaseMessages.getString(PKG, "System.ActionNameMissing.Msg"));
      mb.open();
      return;
    }
    action.setName(wName.getText());
    action.setBotToken(wBotToken.getText());
    action.setChatId(wChatId.getText());
    action.setChatMessage(wChatMessage.getText());
    dispose();
  }
}
