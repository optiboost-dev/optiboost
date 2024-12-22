package org.dev.optiboost.Logic;

import com.sun.management.OperatingSystemMXBean;
import java.awt.Image;
import org.dev.optiboost.entity.ProcessInfo;

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
                        Image icon = iconCache.computeIfAbsent(processName, name -> getImageSafely(processIcon, name));
                        if (memoryUsage != -1 && icon != null) {
                            return new ProcessInfo(icon, processName, memoryUsage);
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingInt(processInfo -> processInfo.memoryUsage))
                    .collect(Collectors.toList());

            reader.close();
            return processes;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private static Image getImageSafely(ProcessIcon processIcon, String processName) {
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
        getMemoryInfo();
    }

}
