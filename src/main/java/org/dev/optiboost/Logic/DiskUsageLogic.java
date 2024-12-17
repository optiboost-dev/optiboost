package org.dev.optiboost.Logic;

import org.dev.optiboost.entity.DiskUsageNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiskUsageLogic {
    private List<DiskUsageNode> diskUsageNodes;

    public DiskUsageLogic() {
        this.diskUsageNodes = new ArrayList<>();
        getDiskUsageSituation();
    }

    public void getDiskUsageSituation(){
        String command = "powershell.exe Get-PSDrive -PSProvider FileSystem | " +
                "Select-Object Name, @{Name='UsedSpace';Expression={($_.Used / 1GB).ToString('0.00') }}, " +
                "@{Name='TotalSpace';Expression={(($_.Used + $_.Free) / 1GB).ToString('0.00')}} | " +
                "Format-Table -Property Name, UsedSpace, TotalSpace";



        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            processBuilder.redirectErrorStream(true);

            // 启动进程并获取输出流
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // 解析输出
                if (line.contains("Name")||line.contains("----")) {
                    continue; // 跳过表头
                }
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 3) {
                    String diskName = parts[0];
                    Double usedSpace = Double.parseDouble(parts[1]);
                    Double totalSpace = Double.parseDouble(parts[2]);
                    if(totalSpace.equals(0.0)){
                        continue;
                    }
                    diskUsageNodes.add(new DiskUsageNode(diskName, totalSpace, usedSpace));
                }
            }
            process.waitFor();
        } catch (IOException | InterruptedException ignored) {
        }
    }

    public List<DiskUsageNode> getDiskUsageNodes() {
        return diskUsageNodes;
    }

    public void setDiskUsageNodes(List<DiskUsageNode> diskUsageNodes) {
        this.diskUsageNodes = diskUsageNodes;
    }

    public static void main(String[] args) {
        DiskUsageLogic diskUsageLogic = new DiskUsageLogic();
        for (DiskUsageNode diskUsageNode : diskUsageLogic.getDiskUsageNodes()) {
            System.out.println(diskUsageNode);
        }
    }


}
