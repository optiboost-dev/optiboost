package org.dev.optiboost.Logic;

import com.sun.management.OperatingSystemMXBean;
import org.dev.optiboost.entity.ProcessInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<ProcessInfo>> futures = new ArrayList<>();

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            // 跳过标题行
            reader.readLine();
            reader.readLine();
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String taskLine = line; // 避免 Lambda 问题
                Callable<ProcessInfo> task = () -> {
                    String[] parts = taskLine.trim().split("\\s+");
                    if (parts.length < 3) return null; // 确保有足够数据解析

                    String processName = parts[0];
                    try {
                        int memoryUsage = Integer.parseInt(parts[parts.length - 2].replace(",", ""));
                        if (memoryUsage != -1 && processIcon.getImageByProcessname(processName) != null) {
                            return new ProcessInfo(processIcon.getImageByProcessname(processName), processName, memoryUsage);
                        }
                    } catch (NumberFormatException e) {
                        // 忽略格式错误的行
                    }
                    return null;
                };
                futures.add(executor.submit(task));
            }
            reader.close();

            // 收集结果
            List<ProcessInfo> processes = new ArrayList<>();
            for (Future<ProcessInfo> future : futures) {
                try {
                    ProcessInfo info = future.get();
                    if (info != null) {
                        processes.add(info);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // 按照内存使用量排序
            processes.sort(Comparator.comparingInt(processInfo -> processInfo.memoryUsage));
            return processes;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
        return Collections.emptyList();
    }
    public static boolean endsWithExe(String str) {
        return str != null && str.endsWith(".exe");
    }
    public static void main(String[] args) {
        getMemoryInfo();
    }

}
