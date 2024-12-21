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
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MonitorLogic {
    public static final int UPDATE_INTERVAL = 1000; // 数据更新间隔，单位毫秒
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
        System.out.println(getCpuUsage());
        System.out.println(Utils.calculateSize(getMemoryUsage()));
        System.out.println(Utils.calculateSize(getWholeMemory()));
        System.out.println(Utils.calculateSize(getDiskUsage()));
        System.out.println(Utils.calculateSize(getDiskTotalSpace()));
    }
}
