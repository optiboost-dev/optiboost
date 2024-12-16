package org.dev.optiboost.Logic;

import org.dev.optiboost.entity.DiskCleanPathList;
import org.dev.optiboost.entity.DiskPathNode;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiskCleanLogic {
    private int totalThreadCount = 4;

    public static void main(String[] args) {
        List<DiskPathNode> pathList = DiskCleanPathList.getCleanupPaths();
        // 挨个访问，看看能否访问得到，输出False或True
        for (DiskPathNode node : pathList) {
            System.out.println(node.getCategory());
            Double size = 0.0;
            for(String path : node.getPaths()) {
                System.out.println("\t"+ path + " " + Files.exists(Paths.get(path)));
                try {
                    size += Files.walk(Paths.get(path)).mapToLong(p -> p.toFile().length()).sum();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            System.out.println(size+" bytes");
//            转化成MB
            System.out.println(size/1024/1024+" MB");
        }
    }

}
