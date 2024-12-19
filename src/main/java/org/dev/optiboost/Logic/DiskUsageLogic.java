package org.dev.optiboost.Logic;

import org.dev.optiboost.entity.DiskUsageNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiskUsageLogic {
    private static List<DiskUsageNode> diskUsageNodes = getDiskUsageSituation();

    public static List<DiskUsageNode> getDiskUsageSituation() {
        String command = "cmd.exe /c wmic logicaldisk get DeviceID, Size, FreeSpace";
        List<DiskUsageNode> result = new ArrayList<>();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("DeviceID")) {
                    continue;
                }
                String[] parts = line.split("\\s+");
                if (parts.length == 3) {
                    String diskName = parts[0];
                    double freeSpace = Double.parseDouble(parts[1]) / (1024 * 1024 * 1024); // 转换为 GB
                    double totalSpace = Double.parseDouble(parts[2]) / (1024 * 1024 * 1024); // 转换为 GB
                    double usedSpace = totalSpace - freeSpace;
                    result.add(new DiskUsageNode(diskName, totalSpace, usedSpace));
                }
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<String> getAllDiskNames() {
        List<String> diskNames = new ArrayList<>();

        // Get the root directories (e.g., C:\, D:\, etc.)
        File[] roots = File.listRoots();
        if (roots != null) {
            for (File root : roots) {
                if (root.exists() && root.isDirectory()) {
                    // Add the disk name (e.g., C:, D:, etc.)
                    diskNames.add(root.getAbsolutePath());
                }
            }
        }

        return diskNames;
    }



    public static List<DiskUsageNode> getDiskUsageNodes() {
        return diskUsageNodes;
    }

    public static void setDiskUsageNodes(List<DiskUsageNode> diskUsageNodes1) {
        diskUsageNodes = diskUsageNodes1;
    }

    public static void main(String[] args) {
        System.out.println(DiskUsageLogic.getAllDiskNames());
    }


}
