/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Service;

import Entities.LogOccurrence;
import Enum.LogLevel;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author 320167484
 */
public class SocketClient {

    private final StringWriter sw = new StringWriter();
    private final PrintWriter pw = new PrintWriter(sw);
    private final ArrayList<LogOccurrence> LogArray = new ArrayList<>();

    public ArrayList<LogOccurrence> PerformServerConnection(String host, String port) {
        if (host == null || host.isEmpty() || Integer.parseInt(port) <= 0) {
            this.addToArray("Argumentos inválidos para estabelecer a conexão.", LogLevel.ERROR);
        }
        try {
            try (Socket socket = new Socket(host, Integer.parseInt(port))) {
                socket.close();
                this.addToArray("Successful connection with " + host + ":" + Integer.parseInt(port), LogLevel.INFO);
            }
        } catch (IOException e) {
            e.printStackTrace(pw);
            this.addToArray("Unable to connect to " + host + ":" + Integer.parseInt(port), LogLevel.SEVERE);

        }
        return getLogArray();
    }

    private void addToArray(String input, LogLevel level) {

        LogOccurrence log = new LogOccurrence(input, level);
        this.LogArray.add(log);
    }

    public ArrayList<LogOccurrence> getLogArray() {
        return LogArray;
    }

}
