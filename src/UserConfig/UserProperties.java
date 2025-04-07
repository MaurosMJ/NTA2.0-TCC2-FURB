/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UserConfig;

import Enum.LogLevel;

/**
 *
 * @author Mauros
 */
public class UserProperties {

    private static LogLevel logLevel = LogLevel.DEBUG;

    public static LogLevel getLogLevel() {
        return logLevel;
    }

}
