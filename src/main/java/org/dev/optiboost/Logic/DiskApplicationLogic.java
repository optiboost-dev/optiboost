package org.dev.optiboost.Logic;

import org.dev.optiboost.entity.DiskApplicationNode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class DiskApplicationLogic {
    List<DiskApplicationNode> diskApplicationNodeList = new ArrayList<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10); // 使用线程池

    public DiskApplicationLogic() {
        scanApplications();
    }

    private void scanApplications() {
        // 定义要执行的两个 PowerShell 命令
        String[] commands = {
                "powershell.exe -Command \"[Console]::OutputEncoding = [System.Text.Encoding]::UTF8; " +
                        "Get-ItemProperty -Path 'HKLM:\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\*' | " +
                        "Select-Object DisplayName, DisplayVersion, InstallLocation, UninstallString, EstimatedSize | Format-List\"",

                "powershell.exe -Command \"[Console]::OutputEncoding = [System.Text.Encoding]::UTF8; " +
                        "Get-ItemProperty -Path 'HKLM:\\SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\*' | " +
                        "Select-Object DisplayName, DisplayVersion, InstallLocation, UninstallString, EstimatedSize | Format-List\""
        };

        for (String command : commands) {
            executeCommand(command);
        }
    }

    private double getFolderSizeFromPowershell(DiskApplicationNode node) {
        String path = node.getInstallLocation();
        if(path.isEmpty()){
            String t = node.getUninstallString();
            if (t.contains("exe")) {
                if(!t.contains("steam.exe")){
                    path = t.substring(0, t.lastIndexOf("\\"));
                }
            }
        }
        if(path.isEmpty()) return 0.0;
        String command = "powershell.exe -Command \"$OutputEncoding = [System.Text.Encoding]::UTF8; " +
                "(Get-ChildItem -Path '"+ path +"' -Recurse | Measure-Object -Property Length -Sum).Sum / 1MB; ";

        try {
            ProcessBuilder pb = new ProcessBuilder(command.split(" "));
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            String line = reader.readLine(); // 获取输出的大小
            process.waitFor();

            if (line != null) {
                double size = 0.0;
                try{
                    size = Double.parseDouble(line.trim());
                }catch (NumberFormatException ignored){

                }
                return size;
            }
        } catch (Exception ignored) {

        }

        return 0.0;
    }

    private Future<Double> getFolderSizeAsync(DiskApplicationNode node) {
        return executorService.submit(() -> {
            return getFolderSizeFromPowershell(node);
        });
    }

    private String dealWithApplicationLine(String line){
        String[] t = line.split(":");
//        删掉t数组的第一个元素
        String[] t1 = new String[t.length-1];
        System.arraycopy(t, 1, t1, 0, t1.length);
        return String.join(":", t1).trim();
    }

    private void executeCommand(String command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command.split(" "));
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            String line;
            String displayName = "";
            String displayVersion = "";
            String installLocation = "";
            String uninstallString = "";
            double estimatedSize = 0.0;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("DisplayName")) {
                    if (!displayName.isEmpty()) {
                        DiskApplicationNode node = new DiskApplicationNode(displayName, displayVersion, installLocation, uninstallString, "", estimatedSize);
                        diskApplicationNodeList.add(node);
                        // 重置所有字段
                        displayName = displayVersion = installLocation = uninstallString = "";
                        estimatedSize = 0.0;
                    }
                    displayName = dealWithApplicationLine(line);
                } else if (line.startsWith("DisplayVersion")) {
                    displayVersion = dealWithApplicationLine(line);
                } else if (line.startsWith("InstallLocation")) {
                    installLocation = dealWithApplicationLine(line);
                } else if (line.startsWith("UninstallString")) {
                    uninstallString = dealWithApplicationLine(line);
                } else if (line.startsWith("EstimatedSize")) {
                    estimatedSize = parseEstimatedSize(line);
                }
            }

            // 确保最后一个应用被添加
            if (!displayName.isEmpty()) {
                DiskApplicationNode node = new DiskApplicationNode(displayName, displayVersion, installLocation, uninstallString, "", estimatedSize);
                diskApplicationNodeList.add(node);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Command exited with error code: " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 解析 EstimatedSize 并转换为 MB
    private double parseEstimatedSize(String line) {
        try {
            String value = dealWithApplicationLine(line);
            if (!value.isEmpty()) {
                double sizeInKB = Double.parseDouble(value);
                return sizeInKB / 1024.0; // 转换为 MB
            }
        } catch (NumberFormatException e) {

        }
        return 0.0;
    }


    public void displayApplications() {
        for (DiskApplicationNode node : diskApplicationNodeList) {
            System.out.println(node);
        }
    }


    public void updateApplicationsSize() {
        List<Future<Double>> futures = new ArrayList<>();

        for (DiskApplicationNode node : diskApplicationNodeList) {
            futures.add(getFolderSizeAsync(node));
        }
        Map<String,Integer> map = new HashMap<>();
        // 等待所有任务完成并更新节点的大小
        for (int i = 0; i < futures.size(); i++) {
            try {
                Double size = futures.get(i).get(); // 获取异步计算结果
                DiskApplicationNode node = diskApplicationNodeList.get(i);
                if(size!=0.0&&node.getInstallLocation().isEmpty()){
                    node.setInstallLocation(node.getUninstallString().substring(0,node.getUninstallString().lastIndexOf("\\")));
                }
                node.setEstimatedSize(size);
            } catch (InterruptedException | ExecutionException ignored) {

            }
        }
    }

    public static void main(String[] args) {
        DiskApplicationLogic logic = new DiskApplicationLogic();
        logic.updateApplicationsSize();
        logic.displayApplications();
    }
}
