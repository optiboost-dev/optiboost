package org.dev.optiboost.Controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.dev.optiboost.Logic.DiskCleanLogic;
import org.dev.optiboost.entity.DiskCleanPathList;
import org.dev.optiboost.entity.DiskPathNode;
import org.dev.optiboost.entity.FileNode;

import java.util.*;

import static org.dev.optiboost.Utils.calculateSize;

public class DiskCleanDownloadController {

    @FXML
    public VBox fileListVBox;
    @FXML
    public ImageView foldArrow;
    private List<DiskPathNode> diskPathNodes = DiskCleanPathList.getDownloadPaths();
    private Map<DiskPathNode,Double> diskPathNodeInfoMap;

    private Map<String, List<FileNode>> fileNodeMap;

    private List<FileNode> filesNeedToBeDelete;

    public void getDownloadFileInfo(){
        diskPathNodeInfoMap = DiskCleanLogic.getDownloadFilesSize();
        fileNodeMap = DiskCleanLogic.getDownloadFiles();
    }

    private Map<HBox, Boolean> arrowStateMap = new HashMap<>();
    private Map<HBox, Boolean> isFileAllChecked = new HashMap<>();

    private void populateTreeView(String option) {
        // Clear previous file items
        fileListVBox.getChildren().clear();

        for (DiskPathNode diskPathNode : diskPathNodes) {
            if (diskPathNodeInfoMap.get(diskPathNode) == null || diskPathNodeInfoMap.get(diskPathNode) == 0.0) {
                continue;
            }
            String[] paths = diskPathNode.getPaths();
            CheckBox diskPathNodeCheckBox = new CheckBox();
            VBox fileVBox = getDetailedFileInNode(diskPathNode, diskPathNodeCheckBox);
            HBox diskPathNodeHBox = new HBox();  // Use HBox layout for each folder item
            if(Objects.equals(option, "new")) isFileAllChecked.put(diskPathNodeHBox, false);
            diskPathNodeHBox.setStyle("-fx-pref-width: 620px; -fx-padding: 10px ; -fx-pref-height: 60px ; -fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1px; -fx-border-radius: 2px; -fx-background-radius: 2px");
            diskPathNodeHBox.setAlignment(Pos.CENTER);
            // Parent checkbox action to toggle all child checkboxes
            diskPathNodeCheckBox.setOnAction(event -> {
                boolean isSelected = diskPathNodeCheckBox.isSelected();
                isFileAllChecked.put(diskPathNodeHBox, isSelected);
                fileVBox.getChildren().forEach(node -> {
                    if(!(node instanceof HBox)){
                        return;
                    }
                    HBox fileHBox = (HBox) node;
                    CheckBox fileCheckBox = (CheckBox) fileHBox.getChildren().get(0);
                    fileCheckBox.setSelected(isSelected);
                    // Add or remove file from filesNeedToBeDelete list based on selection
                    if (isSelected) {
                        filesNeedToBeDelete.add(fileNodeMap.get(diskPathNode.getCategory()).get(fileVBox.getChildren().indexOf(fileHBox)));
                    } else {
                        filesNeedToBeDelete.remove(fileNodeMap.get(diskPathNode.getCategory()).get(fileVBox.getChildren().indexOf(fileHBox)));
                    }
                });
            });

            Label diskPathNodeNameLabel = new Label(diskPathNode.getCategory());
            diskPathNodeNameLabel.setStyle("-fx-padding:0 0 0 5px; -fx-font-size: 14px; -fx-max-width: 400px");
            Region diskPathNodeSpacer = new Region();
            HBox.setHgrow(diskPathNodeSpacer, Priority.ALWAYS);
            Label diskPathNodeSizeLabel = new Label(calculateSize(diskPathNodeInfoMap.get(diskPathNode)));
            diskPathNodeSizeLabel.setStyle("-fx-padding:0 10px 0 0; -fx-font-size: 12px");
            ImageView foldArrow = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/RightArrow.png"))));
            foldArrow.setFitHeight(16);
            foldArrow.setFitWidth(16);
            diskPathNodeHBox.getChildren().addAll(diskPathNodeCheckBox, diskPathNodeNameLabel, diskPathNodeSpacer, diskPathNodeSizeLabel, foldArrow);
            arrowStateMap.put(diskPathNodeHBox, false);

            diskPathNodeHBox.setOnMouseClicked(event -> {
                if (arrowStateMap.get(diskPathNodeHBox)) {
                    arrowStateMap.put(diskPathNodeHBox, false);
                    foldArrow.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/RightArrow.png"))));
                    fileListVBox.getChildren().remove(fileVBox);
                } else {
                    arrowStateMap.put(diskPathNodeHBox, true);
                    foldArrow.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/DownArrow.png"))));
                    int index = fileListVBox.getChildren().indexOf(diskPathNodeHBox);
                    fileListVBox.getChildren().add(index + 1, fileVBox);
                }
            });
            Region diskPathNodeSpacer2 = new Region();
            diskPathNodeSpacer2.setStyle("-fx-pref-height: 10px");
            fileListVBox.getChildren().add(diskPathNodeHBox);
            fileListVBox.getChildren().add(diskPathNodeSpacer2);
        }
        Button deleteButton = new Button("删除选中文件");
        deleteButton.setStyle("-fx-background-color: #005fb8; -fx-text-fill: white; -fx-pref-width: 300px; -fx-pref-height: 40px; -fx-font-size: 16px; -fx-border-radius: 50px; -fx-background-radius: 50px;");
        deleteButton.setOnAction(event -> {
            DiskCleanLogic.cleanFileList(filesNeedToBeDelete);
            getDownloadFileInfo();
            populateTreeView("old");
        });
        Region deleteButtonSpacer = new Region();
        deleteButtonSpacer.setStyle("-fx-pref-height: 20px");
        fileListVBox.getChildren().add(deleteButtonSpacer);
        fileListVBox.getChildren().add(deleteButton);
    }

    public VBox getDetailedFileInNode(DiskPathNode diskPathNode, CheckBox diskPathNodeCheckBox) {
        VBox fileVBox = new VBox();
        fileVBox.setStyle("-fx-pref-width: 620px; -fx-max-width: 620px; -fx-padding: 10px 20px 10px 20px ;-fx-border-color: #e0e0e0; -fx-border-width: 1px; -fx-background-color: white;");
        List<FileNode> fileNodes = fileNodeMap.get(diskPathNode.getCategory());
        Label noticeLabel = new Label("注意：仅展示较大文件，全选后会删除所有文件");
        noticeLabel.setStyle("-fx-pref-width: 580px; -fx-max-width: 580px; -fx-font-size: 12px; -fx-text-fill: #ff0000");
        fileVBox.getChildren().add(noticeLabel);
        for (FileNode fileNode : fileNodes) {
            // Skip files smaller than 100KB
            if (fileNode.getSize() < diskPathNodeInfoMap.get(diskPathNode) / 1024) {
                continue;
            }

            HBox fileHBox = new HBox();
            fileHBox.setStyle("-fx-pref-width: 620px; -fx-padding: 10px ; -fx-pref-height: 40px ; -fx-background-color: white;");
            fileHBox.setAlignment(Pos.CENTER);
            CheckBox fileCheckBox = new CheckBox();

            // Child checkbox action to update parent checkbox state
            fileCheckBox.setOnAction(event -> {
                FileNode file = fileNode;
                if (fileCheckBox.isSelected()) {
                    filesNeedToBeDelete.add(file);
                } else {
                    filesNeedToBeDelete.remove(file);
                }
                updateParentCheckbox(diskPathNode, fileVBox, diskPathNodeCheckBox);
            });

            Label fileNameLabel = new Label(fileNode.getName());
            fileNameLabel.setStyle("-fx-padding:0 0 0 5px; -fx-font-size: 14px; -fx-max-width: 400px");
            Region fileSpacer = new Region();
            HBox.setHgrow(fileSpacer, Priority.ALWAYS);
            Label fileSizeLabel = new Label(calculateSize(fileNode.getSize()));
            fileSizeLabel.setStyle("-fx-padding:0 10px 0 0; -fx-font-size: 12px");
            fileHBox.getChildren().addAll(fileCheckBox, fileNameLabel, fileSpacer, fileSizeLabel);
            fileVBox.getChildren().add(fileHBox);
        }

        return fileVBox;
    }

    private void updateParentCheckbox(DiskPathNode diskPathNode, VBox fileVBox, CheckBox diskPathNodeCheckBox) {
        long selectedCount = fileVBox.getChildren().stream()
                .filter(node -> node instanceof HBox)  // Check if the node is an instance of HBox
                .map(node -> (HBox) node)
                .map(hbox -> (CheckBox) hbox.getChildren().get(0))
                .filter(CheckBox::isSelected)
                .count();

        long totalCount = fileVBox.getChildren().stream()
                .filter(node -> node instanceof HBox)  // Only count HBox nodes
                .count();

        // Update the parent checkbox state based on child selection
        if (selectedCount == totalCount) {
            diskPathNodeCheckBox.setSelected(true);  // All selected
        } else if (selectedCount == 0) {
            diskPathNodeCheckBox.setSelected(false);  // None selected
        } else {
            diskPathNodeCheckBox.setSelected(false);  // Some selected (you could set indeterminate state if needed)
        }
    }



    @FXML
    public void initialize() {
        getDownloadFileInfo();
        filesNeedToBeDelete = new ArrayList<>();
        populateTreeView("new");
    }
}
