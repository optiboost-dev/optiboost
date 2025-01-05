package org.dev.optiboost;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.net.URI;
import java.util.Date;

public class Utils {
    public static String calculateSize(Double size) {
        if (size < 1024) {
            return String.format(size % 1 == 0 ? "%.0fB" : "%.2fB", size);
        } else if (size < 1024 * 1024) {
            double kb = size / 1024;
            return String.format(kb % 1 == 0 ? "%.0fKB" : "%.2fKB", kb);
        } else if (size < 1024 * 1024 * 1024) {
            double mb = size / 1024 / 1024;
            return String.format(mb % 1 == 0 ? "%.0fMB" : "%.2fMB", mb);
        } else {
            double gb = size / 1024 / 1024 / 1024;
            return String.format(gb % 1 == 0 ? "%.0fGB" : "%.2fGB", gb);
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

    public static void openLink(String url) {
        if(Desktop.isDesktopSupported()){
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static BufferedImage toBufferedImage(Image awtImage) {
        if (awtImage instanceof BufferedImage) {
            return (BufferedImage) awtImage;
        }

        // 创建一个 BufferedImage
        BufferedImage bufferedImage = new BufferedImage(
                awtImage.getWidth(null), awtImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // 将 AWT Image 绘制到 BufferedImage 上
        java.awt.Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(awtImage, 0, 0, null);
        g2d.dispose();

        return bufferedImage;
    }

    public static long parseCimDateTime(String line) {
        // 从字符串中提取日期和时间
        String[] parts = line.split("\\.");
        String date = parts[0];
        String time = parts[1].substring(0, 6);

        // 将日期和时间合并为一个长整型
        return Long.parseLong(date + time);
    }

    public static String getDateToBack(Date date) {
//        计算一下时间差
        long time = new Date().getTime() - date.getTime();
        if (time < 1000 * 60) {
            return "刚刚";
        } else if (time < 1000 * 60 * 60) {
            return time / 1000 / 60 + "分钟前";
        } else if (time < 1000 * 60 * 60 * 24) {
            return time / 1000 / 60 / 60 + "小时前";
        } else {
            return time / 1000 / 60 / 60 / 24 + "天前";
        }
    }
}
