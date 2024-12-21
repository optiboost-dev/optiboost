package org.dev.optiboost.Logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MemoryCleanLogic {
    public void killProcess(String processName) {
            String cmd = "taskkill /IM " + processName + " /F";
            try {
                Process process = Runtime.getRuntime().exec(cmd);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                reader.close();
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    System.out.println("Process " + processName + " has been terminated.");
                } else {
                    System.out.println("Failed to terminate process " + processName);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
    }
}
