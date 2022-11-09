/**
 * BSD 3-Clause License
 * 
 * Copyright (c) 2022, Universal Robots A/S
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.ur.urcap.examples.mytoolbarjog.URCommunicator;

public class ScriptCommand {

    private boolean sendAsPrimary = true;
    private final String primary_prefix = "def ";
    private final String secondary_prefix = "sec ";

    private final String programName;
    private final String postfix = "end\n";

    private String commandContent = "";

    /**
     * Create a new ScriptCommand
     */
    public ScriptCommand() {
        this.programName = "myCustomScript():\n";
    }

    /**
     * Create a new ScriptCommand with a custom name
     * 
     * @param commandName the custom name (must be alphanumeric, start with letter)
     */
    public ScriptCommand(String commandName) {
        this.programName = commandName + "():\n";
    }

    /**
     * Append a URScript line to the ScriptCommand
     * 
     * @param command the URScript line to append
     */
    public void appendLine(String command) {
        commandContent += " " + command + "\n";
    }

    /**
     * Send this ScriptCommand as a primary program
     * This is the default behavior
     */
    public void setAsPrimaryProgram() {
        this.sendAsPrimary = true;
    }

    /**
     * Send this ScriptCommand as a secondary program
     * Note: In this mode, no commands must take physical time
     */
    public void setAsSecondaryProgram() {
        this.sendAsPrimary = false;
    }

    /**
     * Returns if the ScriptCommand is configured as a primary program
     * 
     * @return true if a primary program, false if a secondary program
     */
    public boolean isPrimaryProgram() {
        return this.sendAsPrimary;
    }

    @Override
    public String toString() {
        String command = "";
        if (this.sendAsPrimary) {
            command += primary_prefix;
        } else {
            command += secondary_prefix;
        }
        command += this.programName;
        command += this.commandContent;
        command += this.postfix;
        return command;
    }

}
