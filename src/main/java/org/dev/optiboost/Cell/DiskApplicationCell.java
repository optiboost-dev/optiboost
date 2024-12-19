package org.dev.optiboost.Cell;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.dev.optiboost.Controller.DiskCleanApplicationController;
import org.dev.optiboost.Utils;
import org.dev.optiboost.entity.DiskApplicationNode;

import java.util.Optional;

public class DiskApplicationCell extends ListCell<DiskApplicationNode> {
    private DiskCleanApplicationController controller;  // Reference to the controller

    // Setter to set the controller reference from outside
    public void setController(DiskCleanApplicationController controller) {
        this.controller = controller;
    }
    @Override
    protected void updateItem(DiskApplicationNode item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            setAlignment(Pos.CENTER);
            setStyle("-fx-background-color: transparent");
        } else {
            // 使用 createApplicationNode 方法来创建单元格的 UI
            setGraphic(createApplicationNode(item));
            setAlignment(Pos.CENTER);
            setStyle("-fx-background-color: transparent");
            setOnMouseClicked(event -> {
                event.consume();  // This prevents the ListView from taking focus
            });
        }
    }

    private HBox createApplicationNode(DiskApplicationNode diskApplicationNode) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setStyle("-fx-padding: 10px; -fx-pref-width: 680px ; -fx-pref-height: 60px; -fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1px; -fx-border-radius: 2px; -fx-background-radius: 2px");

        VBox nameInfoVBox = new VBox();
        nameInfoVBox.setPrefWidth(400);
        nameInfoVBox.setStyle("-fx-padding: 5px");
        nameInfoVBox.setAlignment(Pos.TOP_LEFT);

        Label nameLabel = new Label(diskApplicationNode.getDisplayName());
        nameLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: black");

        String infoString = diskApplicationNode.getDisplayVersion() + " | " + diskApplicationNode.getPublisher();
        Label infoLabel = new Label(infoString);
        infoLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666");

        nameInfoVBox.getChildren().addAll(nameLabel, infoLabel);

        Button deleteBtn = new Button("删除");
        deleteBtn.setStyle("-fx-background-color: transparent;-fx-font-size: 14px; -fx-text-fill: #005fb8; -fx-padding: 5px 10px; -fx-border-color: #005fb8; -fx-border-width: 1px; -fx-border-radius: 2px; -fx-background-radius: 2px;-fx-cursor: hand");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label sizeLabel = new Label(Utils.calculateSize(diskApplicationNode.getEstimatedSize()));
        sizeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666; -fx-padding: 0 10px 0 0");

        deleteBtn.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("删除确认");
            alert.setHeaderText("你确定要删除"+diskApplicationNode.getDisplayName()+"吗?");

            // Show the alert and wait for a response
            Optional<ButtonType> result = alert.showAndWait();

            // If the user clicked the "OK" button (confirmed the deletion)
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Proceed with the deletion logic
                System.out.println("Delete " + diskApplicationNode.getUninstallString());
//                运行卸载命令
                Utils.runCommand(diskApplicationNode.getUninstallString());
                controller.diskApplicationLogic.removeOneItem(diskApplicationNode);
                controller.updateListView();
                event.consume(); // Consume the event to stop further propagation
            } else {
                // User canceled the action, do nothing or handle cancellation
                System.out.println("Deletion canceled");
                event.consume(); // Consume the event to stop further propagation
            }
        });

        hBox.getChildren().addAll(nameInfoVBox, spacer, sizeLabel, deleteBtn);

        return hBox;
    }
}

