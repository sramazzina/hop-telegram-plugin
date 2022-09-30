
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

import org.apache.hop.metadata.api.HopMetadataProperty;

import java.util.Objects;

public class TelegramBotCmdItem {

    @HopMetadataProperty
    private String commandString;
    @HopMetadataProperty
    private String commandDescription;
    @HopMetadataProperty
    private String pipelineToStart;

    public TelegramBotCmdItem() {
    }

    public TelegramBotCmdItem(String commandString, String pipelineToStart, String commandDescription) {
        this.commandString = commandString;
        this.pipelineToStart = pipelineToStart;
        this.commandDescription = commandDescription;
    }

    public String getCommandString() {
        return commandString;
    }

    public void setCommandString(String commandString) {
        this.commandString = commandString;
    }

    public String getPipelineToStart() {
        return pipelineToStart;
    }

    public void setPipelineToStart(String pipelineToStart) {
        this.pipelineToStart = pipelineToStart;
    }

    public String getCommandDescription() {
        return commandDescription;
    }

    public void setCommandDescription(String commandDescription) {
        this.commandDescription = commandDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TelegramBotCmdItem that = (TelegramBotCmdItem) o;
        return commandString.equals(that.commandString) && Objects.equals(commandDescription, that.commandDescription) && pipelineToStart.equals(that.pipelineToStart);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandString, commandDescription, pipelineToStart);
    }

    @Override
    public String toString() {
        return "TelegramBotCmdItem{" +
                "commandString='" + commandString + '\'' +
                ", commandDescription='" + commandDescription + '\'' +
                ", pipelineToStart='" + pipelineToStart + '\'' +
                '}';
    }
}
