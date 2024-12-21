package org.dev.optiboost.entity;

import java.awt.*;
public class ProcessInfo {
    public Image image;
    public String name;
    public int memoryUsage;

    public ProcessInfo(Image image, String name, int memoryUsage) {
        this.image = image;
        this.name = name;
        this.memoryUsage = memoryUsage;
    }

    @Override
    public String toString() {
        return "Process: " + name + ", Memory Usage: " + memoryUsage + " KB";
    }
}
