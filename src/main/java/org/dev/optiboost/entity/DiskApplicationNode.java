package org.dev.optiboost.entity;

// \HKEY_CURRENT_USER\Software\Tencent\WeChat
public class DiskApplicationNode {
    private String displayName;
    private String displayVersion;
    private String installLocation;
    private String uninstallString;
    private String fileStorageString;
    private double estimatedSize; // 以 MB 为单位

    public DiskApplicationNode(String displayName, String displayVersion, String installLocation, String uninstallString, String fileStorageString, double estimatedSize) {
        this.displayName = displayName;
        this.displayVersion = displayVersion;
        this.installLocation = installLocation;
        this.uninstallString = uninstallString;
        this.fileStorageString = fileStorageString;
        this.estimatedSize = estimatedSize;
    }
    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayVersion() {
        return displayVersion;
    }

    public String getInstallLocation() {
        return installLocation;
    }

    public String getUninstallString() {
        return uninstallString;
    }

    public String getFileStorageString() {
        return fileStorageString;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setDisplayVersion(String displayVersion) {
        this.displayVersion = displayVersion;
    }

    public void setInstallLocation(String installLocation) {
        this.installLocation = installLocation;
    }

    public void setUninstallString(String uninstallString) {
        this.uninstallString = uninstallString;
    }

    public void setFileStorageString(String fileStorageString) {
        this.fileStorageString = fileStorageString;
    }

    public double getEstimatedSize() {
        return estimatedSize;
    }

    public void setEstimatedSize(double estimatedSize) {
        this.estimatedSize = estimatedSize;
    }

    @Override
    public String toString() {
        return "DiskApplicationNode{" +
                "displayName='" + displayName + '\'' +
                ", displayVersion='" + displayVersion + '\'' +
                ", installLocation='" + installLocation + '\'' +
                ", uninstallString='" + uninstallString + '\'' +
                ", estimatedSize=" + estimatedSize + " MB" +
                '}';
    }
}
