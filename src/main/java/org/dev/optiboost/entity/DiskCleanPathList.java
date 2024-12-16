package org.dev.optiboost.entity;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiskCleanPathList {
    static String username = System.getProperty("user.name");

    static DiskPathNode deliveryOptimizationNode = new DiskPathNode("传递优化文件",
            new String[]{
                    "C:/Windows/SoftwareDistribution/DeliveryOptimization",
                    "C:/Windows/SoftwareDistribution/DataStore",
                    "C:/Windows/SoftwareDistribution/Download"
            }
    );

    static DiskPathNode logsNode = new DiskPathNode("日志文件",
            new String[]{
                    "C:/Windows/Logs",
//                    "C:/Windows/System32/LogFiles"
            }
    );

    static DiskPathNode explorerNode = new DiskPathNode("缩略图、历史记录等临时文件",
            new String[]{
                    "C:/Users/" + username + "/AppData/Local/Microsoft/Windows/Explorer"
            }
    );

    static DiskPathNode tempNode = new DiskPathNode("临时文件",
            new String[]{
                    "C:/Windows/Temp",
                    "C:/Users/" + username + "/AppData/Local/Temp"
            }
    );

    static DiskPathNode defenderNode = new DiskPathNode("Windows Defender",
            new String[]{
                    "C:/ProgramData/Microsoft/Windows Defender/Scans/History"
            }
    );

    static DiskPathNode inetCacheNode = new DiskPathNode("IE 浏览器缓存",
            new String[]{
                    "C:/Users/" + username + "/AppData/Local/Microsoft/Windows/INetCache/IE"
            }
    );

    static DiskPathNode werNode = new DiskPathNode("Windows 错误报告",
            new String[]{
                    "C:/ProgramData/Microsoft/Windows/WER/ReportQueue",
                    "C:/ProgramData/Microsoft/Windows/WER/ReportArchive"
            }
    );

    static DiskPathNode recycleBinNode = new DiskPathNode("回收站",
            new String[]{
                    "C:/$Recycle.Bin"
            }
    );

    static DiskPathNode edgeDownloadNode = new DiskPathNode("Edge 浏览器下载",
            new String[]{
                    getEdgeDownloadPath()
            }
    );

    public static String getEdgeDownloadPath() {
        String edgeDownloadPreference = "C:\\Users\\33091\\AppData\\Local\\Microsoft\\Edge\\User Data\\Default\\Preferences";
        // 读取整个文件内容
        StringBuilder content = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(edgeDownloadPreference)))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
            }

            // 使用正则表达式匹配 "download":{"default_directory":"路径"
            Pattern pattern = Pattern.compile("\"download\"\\s*:\\s*\\{\\s*\"default_directory\"\\s*:\\s*\"([^\"]+)\"");
            Matcher matcher = pattern.matcher(content.toString());
            if (matcher.find()) {
                return matcher.group(1); // 返回下载路径
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // 如果没有找到下载路径，返回null
    }

    public static List<DiskPathNode> getCleanupPaths() {
        return List.of(
                deliveryOptimizationNode,
                logsNode,
                explorerNode,
                tempNode,
                defenderNode,
                inetCacheNode,
                werNode,
                recycleBinNode,
                edgeDownloadNode
        );
    }
}
