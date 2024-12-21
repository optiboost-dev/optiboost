package org.dev.optiboost.Logic;

import com.sun.management.OperatingSystemMXBean;
import org.dev.optiboost.entity.ProcessInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
            String line;
            List<ProcessInfo> processes = new ArrayList<>();

            // 跳过标题行
            reader.readLine();
            reader.readLine();
            reader.readLine();


            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                String processName = parts[0];

                int memoryUsage = Integer.parseInt(parts[parts.length - 2].replace(",", ""));
                System.out.println(line);

                if (memoryUsage != -1 ) {
                    if (processIcon.getImageByProcessname(processName)!= null)processes.add(new ProcessInfo(processIcon.getImageByProcessname(processName), processName, memoryUsage));
                }
            }
            reader.close();

            // 按照内存使用量排序
            processes.sort(Comparator.comparingInt(processInfo -> processInfo.memoryUsage));

            return processes;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static boolean endsWithExe(String str) {
        return str != null && str.endsWith(".exe");
    }
    public static void main(String[] args) {
        getMemoryInfo();
    }

}
