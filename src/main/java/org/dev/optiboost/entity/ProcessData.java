package org.dev.optiboost.entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.embed.swing.SwingFXUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ProcessData {
    private final BooleanProperty selected = new SimpleBooleanProperty();
    private final ObjectProperty<Image> icon = new SimpleObjectProperty<>();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty memory = new SimpleStringProperty();

    public ProcessData(ProcessInfo processInfo) {

        BufferedImage bufferedImage = new BufferedImage(processInfo.image.getWidth(null), processInfo.image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(processInfo.image, 0, 0, null);
        g2d.dispose();

// 使用SwingFXUtils将BufferedImage转换为JavaFX的Image
        Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
        this.icon.set(fxImage);
        this.name.set(processInfo.name);
        this.memory.set(processInfo.memoryUsage / 1024   + "MB"); // 假设内存使用量以KB为单位
    }

    public ProcessData(String name, Image icon, String memory) {
        this.name.set(name);
        this.icon.set(icon);
        this.memory.set(memory);
    }

    public ProcessData(Image icon, String name, String memory) {
        this.name.set(name);
        this.icon.set(icon);
        this.memory.set(memory);
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public Image getIcon() {
        return icon.get();
    }

    public ObjectProperty<Image> iconProperty() {
        return icon;
    }

    public void setIcon(Image icon) {
        this.icon.set(icon);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getMemory() {
        return memory.get();
    }

    public StringProperty memoryProperty() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory.set(memory);
    }
}
