package org.dev.optiboost.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.dev.optiboost.Logic.DiskUsageLogic;
import org.dev.optiboost.entity.DiskUsageNode;

import java.io.IOException;
import java.util.List;

public class DiskCleanController {
    @FXML
    public VBox diskUsageContainer;
    @FXML
    public HBox DiskCleanSystemCleanBtn;

    @FXML
    public HBox DiskCleanApplicationCleanBtn;

    DiskUsageLogic diskUsageLogic;

    @FXML
    public void initialize() {
        diskUsageLogic = new DiskUsageLogic();
        List<DiskUsageNode> diskUsageNodes = diskUsageLogic.getDiskUsageNodes();
        for (DiskUsageNode diskUsageNode : diskUsageNodes) {
            addDiskUsageToContainer(diskUsageNode);
        }
        DiskCleanSystemCleanBtn.setOnMouseClicked(event -> {
            try {
                // 使用 ProcessBuilder 执行 cleanmgr 命令
                ProcessBuilder processBuilder = new ProcessBuilder("cleanmgr");
                processBuilder.start();
            } catch (IOException e) {
                Platform.runLater(() -> {
                    new javafx.scene.control.Alert(
                            javafx.scene.control.Alert.AlertType.ERROR,
                            "无法启动磁盘清理工具: " + e.getMessage()
                    ).showAndWait();
                });
            }
        });

        DiskCleanApplicationCleanBtn.setOnMouseClicked(event -> {
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/dev/optiboost/fxml/disk-clean-application.fxml"));
                Parent root = fxmlLoader.load();

                Stage newStage = new Stage();
                newStage.initStyle(StageStyle.TRANSPARENT);
                newStage.setTitle("应用程序清理");
                newStage.setScene(new javafx.scene.Scene(root, 800, 600));

                newStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void addDiskUsageToContainer(DiskUsageNode diskUsageNode) {
        VBox diskUsageItem = new VBox();
        diskUsageItem.setAlignment(Pos.CENTER);
        diskUsageItem.setStyle("-fx-padding: 5 0 5 0;");

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setStyle("-fx-padding: 0 32 2 32px;");

        // 创建并设置磁盘名称的标签
        Label diskNameLabel = new Label(diskUsageNode.getDiskName()+":");
        diskNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        hbox.getChildren().add(diskNameLabel);

        // 创建并设置占位符（Region），它会自动占据剩余空间
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        hbox.getChildren().add(region);

        // 创建并设置磁盘使用情况的标签
        Label usageLabel = new Label(diskUsageNode.getUsedSpace() + "GB/" + diskUsageNode.getTotalSpace() + "GB");
        usageLabel.setStyle("-fx-alignment: CENTER_RIGHT;");
        hbox.getChildren().add(usageLabel);

        // 将 HBox 添加到 VBox 中
        diskUsageItem.getChildren().add(hbox);

        // 创建并设置进度条
        ProgressBar progressBar = new ProgressBar();
        double usedPercentage = diskUsageNode.getUsedSpace() / diskUsageNode.getTotalSpace();
        progressBar.setProgress(usedPercentage);
        progressBar.setStyle("-fx-pref-width: 320px;");

        diskUsageItem.getChildren().add(progressBar);

        diskUsageContainer.getChildren().add(diskUsageItem);
    }


}
