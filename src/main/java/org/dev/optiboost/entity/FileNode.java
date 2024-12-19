package org.dev.optiboost.entity;

public class FileNode {
    private String name;
    private String path;
    private double size;
    private boolean isDirectory;

    public FileNode(String name, String path, double size, boolean isDirectory) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.isDirectory = isDirectory;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public double getSize() {
        return size;
    }

    public boolean isDirectory() {
        return isDirectory;
    }
}
