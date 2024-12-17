package org.dev.optiboost.entity;

public class DiskUsageNode {
    private String diskName;
    private Double totalSpace;
    private Double usedSpace;

    public DiskUsageNode(String diskName, Double totalSpace, Double usedSpace) {
        this.diskName = diskName;
        this.totalSpace = totalSpace;
        this.usedSpace = usedSpace;
    }

    public String getDiskName() {
        return diskName;
    }

    public Double getTotalSpace() {
        return totalSpace;
    }

    public Double getUsedSpace() {
        return usedSpace;
    }

    public void setDiskName(String diskName) {
        this.diskName = diskName;
    }

    public void setTotalSpace(Double totalSpace) {
        this.totalSpace = totalSpace;
    }

    public void setUsedSpace(Double usedSpace) {
        this.usedSpace = usedSpace;
    }

    @Override
    public String toString() {
        return "DiskUsageNode{" +
                "diskName='" + diskName + '\'' +
                ", totalSpace='" + totalSpace + '\'' +
                ", usedSpace='" + usedSpace + '\'' +
                '}';
    }
}
