package org.dev.optiboost.entity;

public class DiskPathNode {
    private String category;
    private String[] paths;

    public DiskPathNode(String category, String[] paths) {
        this.category = category;
        this.paths = paths;
    }

    public String getCategory() {
        return category;
    }

    public String[] getPaths() {
        return paths;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPaths(String[] paths) {
        this.paths = paths;
    }

    @Override
    public String toString() {
        return "DiskPathNode{" +
                "category='" + category + '\'' +
                ", paths=" + paths +
                '}';
    }
}
