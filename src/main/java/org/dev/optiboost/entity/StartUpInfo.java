package org.dev.optiboost.entity;

import java.awt.image.BufferedImage;

public class StartUpInfo {
    private String name;
    private String path;
    private boolean isEnable;
    private BufferedImage icon;


    public StartUpInfo(String name, String path, boolean isEnable, BufferedImage bufferedImage) {
        this.name = name;
        this.path = path;
        this.isEnable = isEnable;
        this.icon = bufferedImage;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public BufferedImage getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return "StartUpInfo{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", isEnable=" + isEnable +
                ", icon=" + icon +
                '}';
    }
}
