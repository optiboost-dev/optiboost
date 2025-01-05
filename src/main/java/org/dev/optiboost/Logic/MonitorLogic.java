package org.dev.optiboost.Logic;

import org.dev.optiboost.Utils;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MonitorLogic {
    public static final int UPDATE_INTERVAL = 500; // 数据更新间隔，单位毫秒
    public static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS; // 时间单位
    private static final SystemInfo si = new SystemInfo();
    private static final HardwareAbstractionLayer hal = si.getHardware();
    private static final OperatingSystem os = si.getOperatingSystem();
    public static double getCpuUsage() {
        try {
            // 创建ProcessBuilder并指定typeperf命令
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "cmd.exe", "/c",
                    "typeperf \"\\Processor Information(_Total)\\% Processor Utility\" -sc 1"
            );
            // 启动进程
            Process process = processBuilder.start();

            // 读取进程输出
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                boolean isDataLine = false; // 标志是否到达数据行
                while ((line = reader.readLine()) != null) {
//                    System.out.println(line);
                    // 跳过非数据行
                    if (line.startsWith("\"") && isDataLine) {
                        String[] parts = line.split(",");
                        if (parts.length > 1) {
                            // 去掉引号并解析为浮点数
                            String cpuUsageString = parts[1].replace("\"", "").trim();
                            return Double.parseDouble(cpuUsageString);
                        }
                    }

                    // 检测到数据行开始标志
                    if (line.contains("(PDH-CSV")) {
                        isDataLine = true;
                    }
                }
            }

            // 等待进程完成并检查退出码
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Error occurred while executing command, exit code: " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 如果出错或未能成功获取CPU使用率，返回 -1 作为默认值
        return -1;
    }

    public static double getGPUUsage(){
        try {
            // 创建ProcessBuilder并指定nvidia-smi命令
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "nvidia-smi", "--query-gpu=utilization.gpu", "--format=csv,noheader,nounits"
            );
            // 启动进程
            Process process = processBuilder.start();

            // 读取进程输出
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 去掉空格并解析为浮点数
                    return Double.parseDouble(line.trim());
                }
            }

            // 等待进程完成并检查退出码
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Error occurred while executing command, exit code: " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static double getMemoryUsage() {
        long totalMemory = hal.getMemory().getTotal();
        long availableMemory = hal.getMemory().getAvailable();
        return totalMemory - availableMemory;
    }

    public static double getWholeMemory() {
        return hal.getMemory().getTotal();
    }

    public static double getDiskUsage() {
        List<OSFileStore> fileStores = os.getFileSystem().getFileStores();
        if (fileStores.isEmpty()) {
            return 0;
        }

        long usedDiskSpace = 0;
        for (OSFileStore store : fileStores) {
            usedDiskSpace += store.getTotalSpace() - store.getFreeSpace();
        }
        return usedDiskSpace;
    }

    public static double getDiskTotalSpace() {
        List<OSFileStore> fileStores = os.getFileSystem().getFileStores();
        if (fileStores.isEmpty()) {
            return 0;
        }

        long totalDiskSpace = 0;
        for (OSFileStore store : fileStores) {
            totalDiskSpace += store.getTotalSpace();
        }
        return totalDiskSpace;
    }

    public static void main(String[] args) {
    }

    public static void startMissionManagement() {
        // 启动windows系统任务管理器
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "cmd.exe", "/c",
                    "taskmgr"
            );
            processBuilder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getCPUTemperature(){
        ProcessBuilder processBuilder = new ProcessBuilder(
                "powershell.exe", "((gwmi msacpi_thermalzonetemperature -namespace \"root/wmi\").CurrentTemperature) / 10-273.15"
        );
        try {
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    return (int) Double.parseDouble(line);
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Error occurred while executing command, exit code: " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getGPUTemperature() {
        // 使用 ProcessBuilder 构建命令
        ProcessBuilder processBuilder = new ProcessBuilder(
                "nvidia-smi", "--query-gpu=temperature.gpu", "--format=csv,noheader"
        );

        try {
            // 启动进程
            Process process = processBuilder.start();

            // 读取命令输出
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 解析温度值
                    try {
                        int temperature = Integer.parseInt(line.trim());
                        return temperature; // 返回 GPU 温度
                    } catch (NumberFormatException e) {
                        System.err.println("Failed to parse temperature: " + line);
                    }
                }
            }

            // 等待进程结束并检查退出码
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Error occurred while executing command, exit code: " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 如果发生错误，返回默认值 -1
        return -1;
    }

    public static int getProcessCount() {
        return os.getProcessCount();
    }

//    获取C盘使用情况
    public static String getCDiskUsage(){
//         获取C盘大小
        long totalDiskSpace = 0;
        long usedDiskSpace = 0;
        List<OSFileStore> fileStores = os.getFileSystem().getFileStores();
        for (OSFileStore store : fileStores) {
            if(store.getMount().equals("C:\\")){
                totalDiskSpace = store.getTotalSpace();
                usedDiskSpace = store.getTotalSpace() - store.getFreeSpace();
            }
        }
        return Utils.calculateSize((double) usedDiskSpace) + "/" + Utils.calculateSize((double) totalDiskSpace);
    }

    public static List<String> getBasicInfo() {
        return List.of(
                getProcessCount() + " 个进程",
                getCDiskUsage(),
                getRunningTime()
        );
    }

    public static String getRunningTime() {
        try {
            // 使用 ProcessBuilder 执行命令
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "systeminfo | find \"系统启动时间\"");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            // 读取命令行输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
            String line;
            if ((line = reader.readLine()) != null) {
                // 解析启动时间
                String startTimeStr = line.replace("系统启动时间: ", "").trim();
                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd, HH:mm:ss");
                Date startTime = format.parse(startTimeStr);

                // 返回几几年几月几号 几点几分几秒
                return Utils.getDateToBack(startTime);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "未知";
    }
}
