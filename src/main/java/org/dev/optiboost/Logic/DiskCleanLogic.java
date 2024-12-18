package org.dev.optiboost.Logic;

import org.dev.optiboost.entity.DiskCleanPathList;
import org.dev.optiboost.entity.DiskPathNode;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class DiskCleanLogic {
    private static final int totalThreadCount = 4;

    public static Map<DiskPathNode, Double> getPathFileSize(List<DiskPathNode> pathList) {
        Map<DiskPathNode, Double> pathSizeMap = new ConcurrentHashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(totalThreadCount);

        List<Callable<Void>> tasks = pathList.stream()
                .map(node -> (Callable<Void>) () -> {
                    double size = 0.0;
                    for (String path : node.getPaths()) {
                        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
                            size += paths.mapToLong(p -> p.toFile().length()).sum();
                        } catch (IOException ignored) {}
                    }
                    pathSizeMap.put(node, size / 1024 / 1024);
                    return null;
                })
                .toList();

        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executorService.shutdown();
        }

        return pathSizeMap;
    }

    public static Boolean cleanPathFiles(DiskPathNode node) {
        for (String path : node.getPaths()) {
            try {
                // 使用Windows命令删除所有文件
                String deleteCommand = "powershell.exe Remove-Item \"" + path + "\\*\" -Recurse -Force";
                ProcessBuilder processBuilder = new ProcessBuilder(deleteCommand.split(" "));
                processBuilder.inheritIO();
                Process process = processBuilder.start();
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                System.err.println("Failed to clean path: " + path + " due to " + e.getMessage());
                return false;
            }
        }
        return true;
    }


    public static Map<DiskPathNode, Double> getDownloadFilesSize() {
        List<DiskPathNode> pathList = DiskCleanPathList.getAllCleanupPaths();
        return getPathFileSize(pathList);
    }

    public static Map<DiskPathNode, Double> getTempFilesSize() {
        List<DiskPathNode> pathList = DiskCleanPathList.getTempPaths();
        return getPathFileSize(pathList);
    }

    public static Boolean cleanTempFiles() {
        List<DiskPathNode> pathList = DiskCleanPathList.getTempPaths();
        return pathList.stream().allMatch(DiskCleanLogic::cleanPathFiles);
    }

    public static void main(String[] args) {
        System.out.println(getTempFilesSize());
        System.out.println(cleanTempFiles());
    }

}
