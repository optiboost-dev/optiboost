package org.dev.optiboost.Logic;

import org.dev.optiboost.Utils;
import org.dev.optiboost.entity.StartUpInfo;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StartUpManagementLogic {

    // 注册表中的自启动项路径
    private static final String RUN_KEY = "HKCU:\\Software\\Microsoft\\Windows\\CurrentVersion\\Run";
    private static final String BACKUP_KEY = "HKCU:\\Software\\Microsoft\\Windows\\CurrentVersion\\RunBackup";

    // 获取所有自启动项
    public static List<StartUpInfo> getAllStartupItems() {
        List<StartUpInfo> startupItems = new ArrayList<>();
        ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", "-Command", "Test-Path -Path 'HKCU:\\Software\\Microsoft\\Windows\\CurrentVersion\\RunBackup'");
        processBuilder.redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            process.waitFor();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().equals("False")) {
                        // 如果备份键不存在，创建备份键
                        processBuilder = new ProcessBuilder("powershell.exe", "-Command", "New-Item -Path 'HKCU:\\Software\\Microsoft\\Windows\\CurrentVersion\\RunBackup' -Force");
                        processBuilder.redirectErrorStream(true);
                        process = processBuilder.start();
                        process.waitFor();
                    }
                }
            }
        } catch (IOException | InterruptedException e) {}

        // 读取原始注册表中的自启动项
        readStartupItems(RUN_KEY, startupItems, true);

        // 读取备份注册表中的自启动项
        readStartupItems(BACKUP_KEY, startupItems, false);

        return startupItems;
    }

    // 读取注册表中的自启动项
    private static void readStartupItems(String keyPath, List<StartUpInfo> startupItems, boolean isOriginal) {
        try {
            // 构建 PowerShell 命令
            ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", "-Command", "Get-ItemProperty -Path '" + keyPath + "' | Out-String -Width 4096");
            processBuilder.redirectErrorStream(true); // 将错误流合并到标准输出流

            // 启动进程
            Process process = processBuilder.start();

            // 读取输出
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().startsWith("PS") || line.trim().isEmpty()) {
                        continue; // 跳过 PowerShell 的输出头
                    }
                    // 解析键值对
                    String[] parts = line.split(":");
                    if(parts.length < 2){
                        continue;
                    }
                    String name = parts[0].trim();
//                        后续的值可能包含冒号，所以需要重新拼接
                    StringBuilder path = new StringBuilder(parts[1].trim());
                    for (int i = 2; i < parts.length; i++) {
                        path.append(":").append(parts[i].trim());
                    }

                    startupItems.add(new StartUpInfo(name, path.toString(), isOriginal, loadIcon(path.toString())));
                }
            }

            // 等待进程结束
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 启用自启动项（从备份中恢复）
    public static void enableStartupItem(String name) {
        try {
            // 构建 PowerShell 命令
            String command = String.format(
                    "Get-ItemProperty -Path '" + BACKUP_KEY + "' -Name '%s' | " +
                            "ForEach-Object { " +
                            "   $value = $_.'%s'; " +
                            "   Set-ItemProperty -Path '"+RUN_KEY+"' -Name '%s' -Value $value; " +
                            "   Remove-ItemProperty -Path '" + BACKUP_KEY + "' -Name '%s'; " +
                            "}", name, name, name, name);

            ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", "-Command", command);
            processBuilder.redirectErrorStream(true);

            // 启动进程
            Process process = processBuilder.start();

            // 读取输出
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line); // 打印 PowerShell 输出
                }
            }

            // 等待进程结束
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("已启用并恢复自启动项: " + name);
            } else {
                System.err.println("启用并恢复失败，退出代码: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 禁用自启动项
    public static void disableStartupItem(String name) {
        try {
            String command = String.format(
                    "Get-ItemProperty -Path '"+RUN_KEY+"' -Name '%s' | " +
                            "ForEach-Object { " +
                            "   $value = $_.'%s'; " +
                            "   Set-ItemProperty -Path '" + BACKUP_KEY + "' -Name '%s' -Value $value; " +
                            "   Remove-ItemProperty -Path '"+RUN_KEY+"' -Name '%s'; " +
                            "}", name, name, name, name);

            ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", "-Command", command);
            processBuilder.redirectErrorStream(true);

            // 启动进程
            Process process = processBuilder.start();

            // 读取输出
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line); // 打印 PowerShell 输出
                }
            }

            // 等待进程结束
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("已禁用并备份自启动项: " + name);
            } else {
                System.err.println("禁用并备份失败，退出代码: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 添加自启动项
    public static void addStartupItem(String name, String path) {
        if (isPathValid(path)) {
            try {
                // 构建 PowerShell 命令
                ProcessBuilder processBuilder = new ProcessBuilder("powershell", "-command", "Set-ItemProperty -Path '" + RUN_KEY + "' -Name '" + name + "' -Value '" + path + "'");
                processBuilder.redirectErrorStream(true);

                // 启动进程
                Process process = processBuilder.start();
                process.waitFor();

                System.out.println("已添加自启动项: " + name + ", 路径: " + path);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("路径无效，无法添加自启动项: " + path);
        }
    }

    // 删除自启动项
    public static void deleteStartupItem(String name) {
        try {
            // 构建 PowerShell 命令
            ProcessBuilder processBuilder = new ProcessBuilder("powershell", "-command", "Remove-ItemProperty -Path '" + RUN_KEY + "' -Name '" + name + "'");
            processBuilder.redirectErrorStream(true);

            // 启动进程
            Process process = processBuilder.start();
            process.waitFor();

            System.out.println("已删除自启动项: " + name);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 检查路径是否存在
    private static boolean isPathValid(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        File file = new File(path);
        return file.exists();
    }

    // 加载图标（如果路径指向可执行文件）
    private static BufferedImage loadIcon(String path){
        String exePath = extractExePath(path);
        if(exePath.equals("no-exe")){
            try {
                return ImageIO.read(Objects.requireNonNull(StartUpManagementLogic.class.getResource("/org/dev/optiboost/assets/Process.png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File file1 = new File(exePath);
        BufferedImage image = null;
        // 获取系统图标
        try {
            // 获取 AWT 的系统图标
            java.awt.Image awtImage = ((ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file1)).getImage();
            // 将 java.awt.Image 转换为 BufferedImage
            image = Utils.toBufferedImage(awtImage);
        } catch (Exception e) {
            // 如果获取系统图标失败，使用默认图标
            try {
                image = ImageIO.read(Objects.requireNonNull(StartUpManagementLogic.class.getResource("/org/dev/optiboost/assets/Process.png")));
            } catch (IOException ex) {
                e.printStackTrace();
            }
        }
        return image;
    }

    private static String extractExePath(String path) {
        // 去掉前缀的双引号（如果有）
        if (path.startsWith("\"")) {
            path = path.substring(1);
        }
        // 定位 .exe 的位置
        int exeIndex = path.toLowerCase().indexOf(".exe");
        if (exeIndex == -1) {
            return "no-exe";
        }
        // 提取从开头到 .exe 的部分
        String exePath = path.substring(0, exeIndex + 4); // +4 是为了包含 ".exe"
        return exePath;
    }

    public static void main(String[] args) {

        // 获取所有自启动项
        List<StartUpInfo> startupItems = StartUpManagementLogic.getAllStartupItems();
//        StartUpManagementLogic.enableStartupItem("OneDrive");
        for (StartUpInfo item : startupItems) {
            System.out.println("Name: " + item.getName() + ", Path: " + item.getPath() + ", Enabled: " + item.isEnable() + ", Icon: " + item.getIcon());
        }
        StartUpInfo item = startupItems.get(0);

    }
}