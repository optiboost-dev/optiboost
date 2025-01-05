package org.dev.optiboost.Logic;

import org.dev.optiboost.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class ProcessIcon {
    public BufferedImage getImageByProcessname(String processname) throws IOException {
        String url = getProcessPathByName(processname);
        if (url == null) return null;

        File file1 = new File(url);
        if (!file1.exists()) {
            return null;
        }

        BufferedImage image;
        // 获取系统图标
        try {
            // 获取 AWT 的系统图标
            java.awt.Image awtImage = ((ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file1)).getImage();
            // 将 java.awt.Image 转换为 BufferedImage
            image = Utils.toBufferedImage(awtImage);
        } catch (Exception e) {
            // 如果获取系统图标失败，使用默认图标
            image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/org/dev/optiboost/assets/Process.png")));
        }

        return image;
    }



    public String getProcessPathByName(String processName) {
        try {
            // 使用 wmic 命令获取进程的完整路径
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "wmic", "process", "where", "name=\"" + processName + "\"", "get", "ExecutablePath"
            );
            Process process = processBuilder.start();

            // 读取命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // 跳过标题行
                if (!line.trim().isEmpty() && !line.contains("ExecutablePath")) {
                    return line.trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
