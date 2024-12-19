package org.dev.optiboost.Logic;

import javafx.scene.control.cell.CheckBoxListCell;
import org.dev.optiboost.entity.DiskCleanPathList;
import org.dev.optiboost.entity.DiskPathNode;
import org.dev.optiboost.entity.FileNode;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import java.io.File;
import java.util.List;
import java.util.concurrent.*;

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
                    pathSizeMap.put(node, size);
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
                String deleteCommand = "powershell.exe Remove-Item \"" + path + "\\*\" -Recurse -Force -ErrorAction SilentlyContinue";
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
        List<DiskPathNode> pathList = DiskCleanPathList.getDownloadPaths();
        return getPathFileSize(pathList);
    }

    public static Double getTempFilesSize() {
        List<DiskPathNode> pathList = DiskCleanPathList.getTempPaths();
        return getPathFileSize(pathList).values().stream().reduce(0.0, Double::sum);
    }

    public static Boolean cleanTempFiles() {
        List<DiskPathNode> pathList = DiskCleanPathList.getTempPaths();
        return pathList.stream().allMatch(DiskCleanLogic::cleanPathFiles);
    }

    public static Map<DiskPathNode, Double> getAllFilesSize() {
        List<DiskPathNode> pathList = DiskCleanPathList.getAllCleanupPaths();
        return getPathFileSize(pathList);
    }



    public static Boolean cleanFileList(List<FileNode> fileNodes) {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); // Use available processors for parallelism
        List<Future<Boolean>> futures = new ArrayList<>();

        for (FileNode fileNode : fileNodes) {
            // Submit each delete task to the executor
            futures.add(executorService.submit(() -> {
                try {
                    File file = new File(fileNode.getPath());
                    if (file.exists()) {
                        return file.delete();  // Delete file or directory
                    }
                    return false;
                } catch (Exception e) {
                    System.err.println("Failed to clean file: " + fileNode.getPath() + " due to " + e.getMessage());
                    return false;
                }
            }));
        }

        // Wait for all tasks to complete
        for (Future<Boolean> future : futures) {
            try {
                if (!future.get()) {
                    executorService.shutdownNow();
                    return false;  // If any file fails to delete, exit immediately
                }
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error while waiting for deletion: " + e.getMessage());
                executorService.shutdownNow();
                return false;
            }
        }

        executorService.shutdown();
        return true;
    }


    public static List<FileNode> getFilesInPath(String path) {
        if (path == null) return List.of();
        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            return paths
                    .filter(p -> !p.equals(Paths.get(path))) // Exclude the directory itself
                    .map(p -> new FileNode(p.getFileName().toString(), p.toString(), p.toFile().length(), p.toFile().isDirectory()))
                    .toList();
        } catch (IOException e) {
            return List.of();
        }
    }


    public static Map<String, List<FileNode>> getFilesInPaths(List<DiskPathNode> nodeList) {
        Map<String, List<FileNode>> filesMap = new HashMap<>();
        for (DiskPathNode node : nodeList) {
            List<FileNode> files = new ArrayList<>();
            for (String path : node.getPaths()) {
                files.addAll(getFilesInPath(path));
            }
            files.sort((f1, f2) -> Double.compare(f2.getSize(), f1.getSize()));
            filesMap.put(node.getCategory(), files);
        }
        return filesMap;
    }

    public static void main(String[] args) {
//        C:\Users\33091\Pictures\asd，获取这个文件夹下的所有文件
        List<FileNode> filesInPath = getFilesInPath("C:\\Users\\33091\\Pictures\\asd");
        System.out.println(cleanFileList(filesInPath));
    }

    public static Map<String, List<FileNode>> getDownloadFiles() {
        return getFilesInPaths(DiskCleanPathList.getDownloadPaths());
    }
}
