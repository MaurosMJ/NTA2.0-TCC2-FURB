/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Mauros
 */
public class ServiceHandler {

    public static void startJar(String scriptPath) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder builder;

            if (os.contains("win")) {
                builder = new ProcessBuilder("cmd.exe", "/c", "start", "", scriptPath);
            } else {
                builder = new ProcessBuilder("bash", scriptPath);
            }

            builder.directory(new File(new File(scriptPath).getParent()));
            builder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopJar(String jarName) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder builder;

            if (os.contains("win")) {
                builder = new ProcessBuilder("taskkill", "/F", "/IM", "java.exe", "/FI", "WINDOWTITLE eq " + jarName);
            } else {
                builder = new ProcessBuilder("pkill", "-f", jarName);
            }

            builder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isJarRunning(String jarName) {
        try {
            Process process = Runtime.getRuntime().exec("jps -l");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.contains(jarName)) {
                    System.out.println(jarName);
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
