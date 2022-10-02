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

import org.apache.hop.core.Const;
import org.apache.hop.core.Props;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.util.Utils;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.transform.BaseTransformMeta;
import org.apache.hop.pipeline.transform.ITransformDialog;
import org.apache.hop.pipeline.transform.TransformMeta;
import org.apache.hop.ui.core.dialog.BaseDialog;
import org.apache.hop.ui.core.dialog.ErrorDialog;
import org.apache.hop.ui.core.widget.ColumnInfo;
import org.apache.hop.ui.core.widget.StyledTextComp;
import org.apache.hop.ui.core.widget.TableView;
import org.apache.hop.ui.core.widget.TextVar;
import org.apache.hop.ui.pipeline.transform.BaseTransformDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;

import java.util.*;
import java.util.List;

public class TelegramBotDialog extends BaseTransformDialog implements ITransformDialog {
  private static final Class<?> PKG = TelegramBotDialog.class; // Needed by Translator

  private final TelegramBotMeta input;
  private TextVar wBotToken;
  private TextVar wChatId;
  private ColumnInfo[] notificationEntriesArray;
  private ColumnInfo[] cmdEntriesArray;
  private Button wEnableCommand;
  private Button wEnableNotification;
  private Button wOmitFieldName;
  private TableView wCommands;
  private TableView wNotifications;
  private CTabFolder wTabFolder;
  private Button wGet;
  private Label wlHeader;
  private Label wlFooter;
  private Label wlHelpHeader;
  private StyledTextComp wHeader;
  private StyledTextComp wFooter;
  private StyledTextComp wHelpHeader;

  private final Map<String, Integer> inputFields;

  public TelegramBotDialog(
      Shell parent, IVariables variables, Object in, PipelineMeta pipelineMeta, String sname) {
    super(parent, variables, (BaseTransformMeta) in, pipelineMeta, sname);
    inputFields = new HashMap<>();
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
    changed = input.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = 15;
    formLayout.marginHeight = 15;

    shell.setLayout(formLayout);
    shell.setText(BaseMessages.getString(PKG, "TelegramBot.Shell.Title"));

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

    // TransformName line
    wlTransformName = new Label(shell, SWT.RIGHT);
    wlTransformName.setText(BaseMessages.getString(PKG, "TelegramBot.TransformName.Label"));
    props.setLook(wlTransformName);
    fdlTransformName = new FormData();
    fdlTransformName.left = new FormAttachment(0, 0);
    fdlTransformName.right = new FormAttachment(middle, -margin);
    fdlTransformName.top = new FormAttachment(0, margin);
    wlTransformName.setLayoutData(fdlTransformName);
    wTransformName = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wTransformName.setText(transformName);
    props.setLook(wTransformName);
    wTransformName.addModifyListener(lsMod);
    fdTransformName = new FormData();
    fdTransformName.width = 150;
    fdTransformName.left = new FormAttachment(middle, 0);
    fdTransformName.top = new FormAttachment(0, margin);
    fdTransformName.right = new FormAttachment(100, 0);
    wTransformName.setLayoutData(fdTransformName);

    // BotToken
    Label wlBotToken = new Label(shell, SWT.RIGHT);
    wlBotToken.setText(BaseMessages.getString(PKG, "TelegramBot.BotToken.Label"));
    props.setLook(wlBotToken);
    FormData fdlBotToken = new FormData();
    fdlBotToken.left = new FormAttachment(0, 0);
    fdlBotToken.top = new FormAttachment(wTransformName, margin);
    fdlBotToken.right = new FormAttachment(middle, -margin);
    wlBotToken.setLayoutData(fdlBotToken);

    wBotToken = new TextVar(variables, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    props.setLook(wBotToken);
    wBotToken.addModifyListener(lsMod);
    FormData fdBotToken = new FormData();
    fdBotToken.left = new FormAttachment(middle, 0);
    fdBotToken.top = new FormAttachment(wTransformName, margin);
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

    wTabFolder = new CTabFolder(shell, SWT.BORDER);
    props.setLook(wTabFolder, Props.WIDGET_STYLE_TAB);

    addNotificationsTab(margin, middle, lsMod);
    addCommandsTab(margin, middle, lsMod);

    FormData fdTabFolder = new FormData();
    fdTabFolder.left = new FormAttachment(0, 0);
    fdTabFolder.right = new FormAttachment(100, 0);
    fdTabFolder.top = new FormAttachment(wChatId, margin);
    fdTabFolder.bottom = new FormAttachment(wOk, -margin);
    wTabFolder.setLayoutData(fdTabFolder);

    FormData fdComp = new FormData();
    fdComp.left = new FormAttachment(0, 0);
    fdComp.top = new FormAttachment(0, 0);
    fdComp.right = new FormAttachment(100, 0);
    fdComp.bottom = new FormAttachment(100, 0);
    shell.setLayoutData(fdComp);

    shell.pack();

    setButtonPositions(new Button[] {wOk, wCancel}, margin, null);
    wTabFolder.setSelection(input.isEnableNotifications() ? 0 : 1);

    // Set the shell size, based upon previous time...
    setSize();
    getData();

    input.setChanged(changed);
    doChangeNotificationsTabControlsStatus();
    doChangeCommandsTabControlsStatus();

    BaseDialog.defaultShellHandling(shell, c -> ok(), c -> cancel());

    return transformName;
  }

  private void addCommandsTab(int margin, int middle, ModifyListener lsMod) {

    CTabItem wCommandTab = new CTabItem(wTabFolder, SWT.NONE);
    wCommandTab.setText(BaseMessages.getString(PKG, "TelegramBot.CommandsTab.CTabItem"));

    FormLayout commandLayout = new FormLayout();
    commandLayout.marginWidth = 3;
    commandLayout.marginHeight = 3;

    Composite wCommandComp = new Composite(wTabFolder, SWT.NONE);
    props.setLook(wCommandComp);
    wCommandComp.setLayout(commandLayout);

    wEnableCommand = new Button(wCommandComp, SWT.CHECK);
    wEnableCommand.setText(BaseMessages.getString(PKG, "TelegramBot.EnableCommands.Label"));
    props.setLook(wEnableCommand);
    FormData fdEnableCommand = new FormData();
    fdEnableCommand.left = new FormAttachment(0, 0);
    fdEnableCommand.top = new FormAttachment(0, margin);
    wEnableCommand.setLayoutData(fdEnableCommand);

    wlHelpHeader = new Label(wCommandComp, SWT.RIGHT);
    wlHelpHeader.setText(BaseMessages.getString(PKG, "TelegramBot.Commands.HelpHeader.Label"));
    props.setLook(wlHelpHeader);
    FormData fdlHelpHeader = new FormData();
    fdlHelpHeader.left = new FormAttachment(0, 0);
    fdlHelpHeader.top = new FormAttachment(wEnableCommand, margin);
    wlHelpHeader.setLayoutData(fdlHelpHeader);

    wHelpHeader =
            new StyledTextComp(
                    variables,
                    wCommandComp,
                    SWT.MULTI | SWT.LEFT | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    props.setLook(wHelpHeader, Props.WIDGET_STYLE_FIXED);
    wHelpHeader.addModifyListener(lsMod);
    FormData fdHelpHeader = new FormData();
    fdHelpHeader.left = new FormAttachment(0, 0);
    fdHelpHeader.right = new FormAttachment(100, 0);
    fdHelpHeader.top = new FormAttachment(wlHelpHeader, margin);
    fdHelpHeader.height = (int) (60 * props.getZoomFactor());
    wHelpHeader.setLayoutData(fdHelpHeader);

    int nrKeyCols = 3;
    int nrKeyRows = (input.getCmdItems() != null ? input.getCmdItems().size() : 1);

    cmdEntriesArray = new ColumnInfo[nrKeyCols];
    cmdEntriesArray[0] =
        new ColumnInfo(
            BaseMessages.getString(PKG, "TelegramBot.Commands.Command.Column"),
            ColumnInfo.COLUMN_TYPE_TEXT,
            false);
    cmdEntriesArray[0].setUsingVariables(true);

    cmdEntriesArray[1] =
        new ColumnInfo(
            BaseMessages.getString(PKG, "TelegramBot.Commands.Pipeline.Column"),
            ColumnInfo.COLUMN_TYPE_TEXT,
            false);
    cmdEntriesArray[1].setUsingVariables(true);

    cmdEntriesArray[2] =
        new ColumnInfo(
            BaseMessages.getString(PKG, "TelegramBot.Commands.Description.Column"),
            ColumnInfo.COLUMN_TYPE_TEXT,
            false);
    cmdEntriesArray[2].setUsingVariables(true);

    wCommands =
        new TableView(
            variables,
            wCommandComp,
            SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL,
                cmdEntriesArray,
            nrKeyRows,
            lsMod,
            props);

    wCommands.addModifyListener(lsMod);

    FormData fdCommandList = new FormData();
    fdCommandList.left = new FormAttachment(0, 0);
    fdCommandList.top = new FormAttachment(wHelpHeader, margin);
    fdCommandList.right = new FormAttachment(100, -margin);
    fdCommandList.bottom = new FormAttachment(100, -margin);
    wCommands.setLayoutData(fdCommandList);

    wEnableCommand.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent arg0) {
            changeCommandsTabControlsStatus();
          }
        });

    FormData fdCommandComp = new FormData();
    fdCommandComp.left = new FormAttachment(0, 0);
    fdCommandComp.top = new FormAttachment(0, 0);
    fdCommandComp.right = new FormAttachment(100, 0);
    fdCommandComp.bottom = new FormAttachment(100, 0);
    wCommandComp.setLayoutData(fdCommandComp);

    wCommandComp.layout();
    wCommandTab.setControl(wCommandComp);
  }

  private void addNotificationsTab(int margin, int middle, ModifyListener lsMod) {

    CTabItem wNotificationTab = new CTabItem(wTabFolder, SWT.NONE);
    wNotificationTab.setText(BaseMessages.getString(PKG, "TelegramBot.NotificationsTab.CTabItem"));

    FormLayout notificationLayout = new FormLayout();
    notificationLayout.marginWidth = 3;
    notificationLayout.marginHeight = 3;

    Composite wNotificationComp = new Composite(wTabFolder, SWT.NONE);
    props.setLook(wNotificationComp);
    wNotificationComp.setLayout(notificationLayout);

    wEnableNotification = new Button(wNotificationComp, SWT.CHECK);
    wEnableNotification.setText(
        BaseMessages.getString(PKG, "TelegramBot.EnableNotifications.Label"));
    props.setLook(wEnableNotification);
    FormData fdEnableNotification = new FormData();
    fdEnableNotification.left = new FormAttachment(0, 0);
    fdEnableNotification.top = new FormAttachment(0, margin);
    wEnableNotification.setLayoutData(fdEnableNotification);

    wOmitFieldName = new Button(wNotificationComp, SWT.CHECK);
    wOmitFieldName.setText(BaseMessages.getString(PKG, "TelegramBot.OmitFieldName.Label"));
    props.setLook(wOmitFieldName);
    FormData fdOmitFieldName = new FormData();
    fdOmitFieldName.left = new FormAttachment(0, 0);
    fdOmitFieldName.top = new FormAttachment(wEnableNotification, margin);
    wOmitFieldName.setLayoutData(fdOmitFieldName);

    wlHeader = new Label(wNotificationComp, SWT.RIGHT);
    wlHeader.setText(BaseMessages.getString(PKG, "TelegramBot.Header.Label"));
    props.setLook(wlHeader);
    FormData fdlHeader = new FormData();
    fdlHeader.left = new FormAttachment(0, 0);
    fdlHeader.top = new FormAttachment(wOmitFieldName, margin);
    wlHeader.setLayoutData(fdlHeader);

    wHeader =
        new StyledTextComp(
            variables,
            wNotificationComp,
            SWT.MULTI | SWT.LEFT | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    props.setLook(wHeader, Props.WIDGET_STYLE_FIXED);
    wHeader.addModifyListener(lsMod);
    FormData fdHeader = new FormData();
    fdHeader.left = new FormAttachment(0, 0);
    fdHeader.right = new FormAttachment(100, 0);
    fdHeader.top = new FormAttachment(wlHeader, margin);
    fdHeader.height = (int) (60 * props.getZoomFactor());
    wHeader.setLayoutData(fdHeader);

    wlFooter = new Label(wNotificationComp, SWT.RIGHT);
    wlFooter.setText(BaseMessages.getString(PKG, "TelegramBot.Footer.Label"));
    props.setLook(wlFooter);
    FormData fdlFooter = new FormData();
    fdlFooter.left = new FormAttachment(0, 0);
    fdlFooter.top = new FormAttachment(wHeader, margin);
    wlFooter.setLayoutData(fdlFooter);

    wFooter =
        new StyledTextComp(
            variables,
            wNotificationComp,
            SWT.MULTI | SWT.LEFT | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    props.setLook(wFooter, Props.WIDGET_STYLE_FIXED);
    wFooter.addModifyListener(lsMod);
    FormData fdFooter = new FormData();
    fdFooter.top = new FormAttachment(wlFooter, margin);
    fdFooter.left = new FormAttachment(0, 0);
    fdFooter.right = new FormAttachment(100, 0);
    fdFooter.height = (int) (60 * props.getZoomFactor());
    wFooter.setLayoutData(fdFooter);

    int nrKeyCols = 1;
    int nrKeyRows = (input.getFieldItems() != null ? input.getFieldItems().size() : 1);

    notificationEntriesArray = new ColumnInfo[nrKeyCols];
    notificationEntriesArray[0] =
        new ColumnInfo(
            BaseMessages.getString(PKG, "TelegramBot.Notifications.Field.Column"),
            ColumnInfo.COLUMN_TYPE_CCOMBO,
            new String[] {""},
            false);

    wNotifications =
        new TableView(
            variables,
            wNotificationComp,
            SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL,
                notificationEntriesArray,
            nrKeyRows,
            lsMod,
            props);

    wNotifications.addModifyListener(lsMod);

    wGet = new Button(wNotificationComp, SWT.PUSH);
    wGet.setText(BaseMessages.getString(PKG, "System.Button.GetFields"));
    wGet.setToolTipText(BaseMessages.getString(PKG, "System.Tooltip.GetFields"));
    FormData fdGet = new FormData();
    fdGet.top = new FormAttachment(wFooter, margin);
    fdGet.right = new FormAttachment(100, 0);
    wGet.setLayoutData(fdGet);
    wGet.addListener(SWT.Selection, e -> get());

    FormData fdNotificationList = new FormData();
    fdNotificationList.left = new FormAttachment(0, 0);
    fdNotificationList.top = new FormAttachment(wFooter, margin);
    fdNotificationList.right = new FormAttachment(wGet, -margin);
    fdNotificationList.bottom = new FormAttachment(100, -margin);
    wNotifications.setLayoutData(fdNotificationList);

    wEnableNotification.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent arg0) {
            changeNotificationsTabControlsStatus();
          }
        });

    wOmitFieldName.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent arg0) {
            input.setChanged();
          }
        });

    FormData fdNotificationsComp = new FormData();
    fdNotificationsComp.left = new FormAttachment(0, 0);
    fdNotificationsComp.top = new FormAttachment(0, 0);
    fdNotificationsComp.right = new FormAttachment(100, 0);
    fdNotificationsComp.bottom = new FormAttachment(100, 0);
    wNotificationComp.setLayoutData(fdNotificationsComp);

    wNotificationComp.layout();
    wNotificationTab.setControl(wNotificationComp);


    //
    // Search the fields in the background
    //

    final Runnable runnable =
            () -> {
              TransformMeta transformMeta = pipelineMeta.findTransform(transformName);
              if (transformMeta != null) {
                try {
                  IRowMeta row = pipelineMeta.getPrevTransformFields(variables, transformMeta);

                  // Remember these fields...
                  for (int i = 0; i < row.size(); i++) {
                    inputFields.put(row.getValueMeta(i).getName(), i);
                  }

                  setComboBoxes();
                } catch (HopException e) {
                  logError(BaseMessages.getString(PKG, "System.Dialog.GetFieldsFailed.Message"));
                }
              }
            };
    new Thread(runnable).start();

  }

  protected void setComboBoxes() {
    // Something was changed in the row.
    //
    final Map<String, Integer> fields = new HashMap<>();

    // Add the currentMeta fields...
    fields.putAll(inputFields);

    Set<String> keySet = fields.keySet();
    List<String> entries = new ArrayList<>(keySet);

    String[] fieldNames = entries.toArray(new String[entries.size()]);
    Const.sortStrings(fieldNames);
    // Key fields
    notificationEntriesArray[0].setComboValues(fieldNames);
  }
  private void changeCommandsTabControlsStatus() {
    if (wEnableNotification.getSelection()) {
      MessageBox mb = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
      mb.setMessage(BaseMessages.getString(PKG, "TelegramBot.DisableNotifications.Message"));
      mb.setText(BaseMessages.getString(PKG, "TelegramBot.DisableNotifications.Title"));
      int answer = mb.open();
      switch (answer) {
        case SWT.OK:
          doChangeCommandsTabControlsStatus();
          wEnableNotification.setSelection(false);
          wNotifications.table.clearAll();
          wHeader.setText("");
          wFooter.setText("");
          doChangeNotificationsTabControlsStatus();
          input.setChanged();
          break;
        case SWT.CANCEL:
          wEnableCommand.setSelection(false);
          break;
        default:
          break;
      }
    } else {
      doChangeCommandsTabControlsStatus();
    }
  }

  private void changeNotificationsTabControlsStatus() {
    if (wEnableCommand.getSelection()) {
      MessageBox mb = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
      mb.setMessage(BaseMessages.getString(PKG, "TelegramBot.DisableCommands.Message"));
      mb.setText(BaseMessages.getString(PKG, "TelegramBot.DisableCommands.Title"));
      int answer = mb.open();
      switch (answer) {
        case SWT.OK:
          doChangeNotificationsTabControlsStatus();
          wEnableCommand.setSelection(false);
          wCommands.table.clearAll();
          doChangeCommandsTabControlsStatus();
          input.setChanged();
          break;
        case SWT.CANCEL:
          wEnableNotification.setSelection(false);
          break;
        default:
          break;
      }
    } else {
      doChangeNotificationsTabControlsStatus();
    }
  }

  private void doChangeCommandsTabControlsStatus() {
    wCommands.setEnabled(wEnableCommand.getSelection());
  }

  private void doChangeNotificationsTabControlsStatus() {
    wNotifications.setEnabled(wEnableNotification.getSelection());
    wGet.setEnabled(wEnableNotification.getSelection());
    wlFooter.setEnabled(wEnableNotification.getSelection());
    wFooter.setEnabled(wEnableNotification.getSelection());
    wHeader.setEnabled(wEnableNotification.getSelection());
    wlHeader.setEnabled(wEnableNotification.getSelection());
  }

  private void get() {
    try {
      IRowMeta r = pipelineMeta.getPrevTransformFields(variables, transformName);
      if (r != null && !r.isEmpty()) {
        BaseTransformDialog.getFieldsFromPrevious(
            r, wNotifications, 1, new int[] {1}, new int[] {}, -1, -1, null);
      }
    } catch (HopException ke) {
      new ErrorDialog(
          shell,
          BaseMessages.getString(PKG, "GroupByDialog.FailedToGetFields.DialogTitle"),
          BaseMessages.getString(PKG, "GroupByDialog.FailedToGetFields.DialogMessage"),
          ke);
    }
  }

  /** Copy information from the meta-data input to the dialog fields. */
  public void getData() {

    // Get sample text and put it on dialog's text field
    if (!Utils.isEmpty(input.getBotToken())) wBotToken.setText(input.getBotToken());
    if (!Utils.isEmpty(input.getChatId())) wChatId.setText(input.getChatId());

    if (input.isEnableCommands()) {
      wEnableCommand.setSelection(input.isEnableCommands());
      if (!Utils.isEmpty(input.getCommandsHelpHeaderText()))
      wHelpHeader.setText(input.getCommandsHelpHeaderText());
      if (input.getCmdItems() != null) {
        for (int i = 0; i < input.getCmdItems().size(); i++) {
          TableItem item = wCommands.table.getItem(i);
          if (input.getCmdItems().get(i) != null) {
            item.setText(1, input.getCmdItems().get(i).getCommandString());
            item.setText(2, input.getCmdItems().get(i).getPipelineToStart());
            item.setText(3, Const.NVL(input.getCmdItems().get(i).getCommandDescription(), ""));
          }
        }
      }

      wCommands.setRowNums();
      wCommands.optWidth(true);
    }

    if (input.isEnableNotifications()) {
      wEnableNotification.setSelection(input.isEnableNotifications());
      wOmitFieldName.setSelection(input.isOmitFieldName());
      if (!Utils.isEmpty(input.getNotificationHeaderText()))
        wHeader.setText(input.getNotificationHeaderText());
      if (!Utils.isEmpty(input.getNotificationFooterText()))
        wFooter.setText(input.getNotificationFooterText());
      if (input.getFieldItems() != null) {
        for (int i = 0; i < input.getFieldItems().size(); i++) {
          TableItem item = wNotifications.table.getItem(i);
          if (input.getFieldItems().get(i) != null) {
            item.setText(1, input.getFieldItems().get(i).getFieldName());
          }
        }
      }

      wCommands.setRowNums();
      wCommands.optWidth(true);
    }

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
    input.setEnableCommands(wEnableCommand.getSelection());
    input.setCommandsHelpHeaderText(wHelpHeader.getText());
    input.getCmdItems().clear();

    int sizeCmdItems = wCommands.nrNonEmpty();
    for (int i = 0; i < sizeCmdItems; i++) {
      TableItem item = wCommands.getNonEmpty(i);
      input
          .getCmdItems()
          .add(new TelegramBotCmdItem(item.getText(1), item.getText(2), item.getText(3)));
    }

    input.setEnableNotifications(wEnableNotification.getSelection());
    input.setOmitFieldName(wOmitFieldName.getSelection());
    input.setNotificationHeaderText(wHeader.getText());
    input.setNotificationFooterText(wFooter.getText());
    input.getFieldItems().clear();
    int notificationFieldItems = wNotifications.nrNonEmpty();
    for (int i = 0; i < notificationFieldItems; i++) {
      TableItem item = wNotifications.getNonEmpty(i);
      input.getFieldItems().add(new TelegramBotFieldItem(item.getText(1)));
    }
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
