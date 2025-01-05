package org.dev.optiboost.entity;

import java.awt.image.BufferedImage;

public class ProcessInfo {
    private BufferedImage image;
    private String name;
    private double memoryUsage;

    public ProcessInfo(BufferedImage image, String name, double memoryUsage) {
        this.image = image;
        this.name = name;
        this.memoryUsage = memoryUsage;
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMemoryUsage(double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public double getMemoryUsage() {
        return memoryUsage;
    }

    @Override
    public String toString() {
        return "Process: " + name + ", Memory Usage: " + memoryUsage + " KB";
    }

}
