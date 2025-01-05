package org.dev.optiboost.Logic;

import com.sun.management.OperatingSystemMXBean;
import java.awt.Image;
import org.dev.optiboost.entity.ProcessInfo;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class MemoryShowLogic {
    protected OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    public long getTotalMemory() {
        return osBean.getTotalMemorySize();
    }

    public long getFreeMemory() {
        return osBean.getFreeMemorySize();
    }

    public long getUsedMemory() {
        return getTotalMemory() - getFreeMemory();
    }

    public static List<ProcessInfo> getMemoryInfo() {
        ProcessIcon processIcon = new ProcessIcon();
        ProcessBuilder processBuilder = new ProcessBuilder("tasklist");
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // 缓存图标获取
            Map<String, Image> iconCache = new ConcurrentHashMap<>();

            List<ProcessInfo> processes = reader.lines()
                    .skip(3) // 跳过标题行
                    .parallel() // 并行处理
                    .map(taskLine -> {
                        String[] parts = taskLine.trim().split("\\s+");
                        if (parts.length < 3) return null;
                        String processName = parts[0];
                        int memoryUsage = Integer.parseInt(parts[parts.length - 2].replace(",", ""));
                        return new ProcessInfo(null, processName, memoryUsage); // 先不获取图标
                    })
                    .filter(Objects::nonNull)
                    .filter(processInfo -> !isSystemProcess(processInfo.getName())) // 排除系统进程
                    .sorted(Comparator.comparingDouble(processInfo -> -processInfo.getMemoryUsage())) // 按内存使用量降序排序
                    .limit(20) // 获取前20个进程
                    .map(processInfo -> {
                        // 只对最终的前20个进程获取图标
                        BufferedImage icon = (BufferedImage) iconCache.computeIfAbsent(processInfo.getName(), name -> getImageSafely(processIcon, name));
                        return new ProcessInfo(icon, processInfo.getName(), processInfo.getMemoryUsage()*1024);
                    })
                    .collect(Collectors.toList());

            reader.close();
            return processes;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    // 判断是否为系统进程
    private static boolean isSystemProcess(String processName) {
        // 常见的系统进程名称
        String[] systemProcesses = {"System", "svchost.exe", "csrss.exe", "lsass.exe", "wininit.exe", "services.exe", "smss.exe", "explorer.exe"};
        for (String sysProcess : systemProcesses) {
            if (processName.equalsIgnoreCase(sysProcess)) {
                return true;
            }
        }
        return false;
    }

    private static BufferedImage getImageSafely(ProcessIcon processIcon, String processName) {
        try {
            return processIcon.getImageByProcessname(processName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean endsWithExe(String str) {
        return str != null && str.endsWith(".exe");
    }
    public static void main(String[] args) {
        getMemoryInfo().forEach(System.out::println);
    }

}
