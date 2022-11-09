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

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ScriptSender {
    // IP of the robot
    private final String TCP_IP;
    // Port for secondary client
    private final int TCP_port = 30002;

    /**
     * Default constructor, using localhost IP (127.0.0.1)
     */
    public ScriptSender() {
        this.TCP_IP = "127.0.0.1";
    }

    /**
     * Constructor for IP different from localhost
     * 
     * @param IP the IP address of the robot
     */
    public ScriptSender(String IP) {
        this.TCP_IP = IP;
    }

    /**
     * Method used to send a ScriptCommand as a primary program to the Secondary
     * Client Interface.
     * If called while a program is already running, the existing program will halt.
     * 
     * @param command the ScriptCommand object to send.
     */
    public void sendScriptCommand(ScriptCommand command) {
        sendToSecondary(command.toString());
    }

    // Internal method that sends script to client
    private void sendToSecondary(String command) {
        try {
            // Create a new Socket Client
            Socket sc = new Socket(TCP_IP, TCP_port);

            if (sc.isConnected()) {
                // Create stream for data
                DataOutputStream out;
                out = new DataOutputStream(sc.getOutputStream());

                // Send command
                out.write(command.getBytes("US-ASCII"));
                out.flush();

                // Perform housekeeping
                out.close();
            } else {
                System.out.println("Could not connect to secondary interface!");
            }
            sc.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Checks if the robot is running prior to sending the URscript to the primary
     * client interface of the UR-controller.
     *
     * @return success
     */
    public boolean executeURScript(ScriptCommand command) {
        DashboardServerCommunicator dashboard = new DashboardServerCommunicator();
        try {
            String robotmode = dashboard.robotmode();

            if (robotmode.matches("Robotmode: RUNNING")) {
                sendScriptCommand(command);
            } else {
                System.out.println("Robot is not running, failed to send script");
                dashboard.popup("Robot is not running");
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
