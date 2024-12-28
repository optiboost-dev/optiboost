package org.dev.optiboost;

import java.text.DecimalFormat;

public class Utils {
    public static String calculateSize(Double size) {
        if (size < 1024) {
            return String.format(size % 1 == 0 ? "%.0f B" : "%.2f B", size);
        } else if (size < 1024 * 1024) {
            double kb = size / 1024;
            return String.format(kb % 1 == 0 ? "%.0f KB" : "%.2f KB", kb);
        } else if (size < 1024 * 1024 * 1024) {
            double mb = size / 1024 / 1024;
            return String.format(mb % 1 == 0 ? "%.0f MB" : "%.2f MB", mb);
        } else {
            double gb = size / 1024 / 1024 / 1024;
            return String.format(gb % 1 == 0 ? "%.0f GB" : "%.2f GB", gb);
        }
    }

    public static String formatDouble(double value) {
        // 保留一位小数，如果没有小数则显示整数
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(value);
    }

    public static void runCommand(String uninstallString) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(uninstallString.split(" "));
            processBuilder.redirectErrorStream(true);
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showAlert(String 清理成功, String s) {

    }
}
