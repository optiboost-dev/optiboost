package org.dev.optiboost.entity;

public class DiskCleanSuggestionNode {
    private String suggestion;
    private String size;
    private String[] paths;

    public DiskCleanSuggestionNode(String suggestion, String size, String[] paths) {
        this.suggestion = suggestion;
        this.size = size;
        this.paths = paths;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public String getSize() {
        return size;
    }

    public String[] getPaths() {
        return paths;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setPaths(String[] paths) {
        this.paths = paths;
    }

    @Override
    public String toString() {
        return "DiskCleanSuggestionNode{" +
                "suggestion='" + suggestion + '\'' +
                ", size='" + size + '\'' +
                ", paths=" + paths +
                '}';
    }
}
