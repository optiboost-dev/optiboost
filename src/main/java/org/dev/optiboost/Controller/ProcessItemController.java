package org.dev.optiboost.Controller;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.dev.optiboost.Logic.MemoryCleanLogic;
import org.dev.optiboost.Utils;
import org.dev.optiboost.entity.ProcessInfo;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ProcessItemController {
    @FXML
    public ImageView icon;
    @FXML
    public Text name, size;
    @FXML
    public Button action;

    ProcessManagementController processManagementController;

    ProcessInfo processInfo;

    public void setProcessManagementController(ProcessManagementController processManagementController) {
        this.processManagementController = processManagementController;
    }

    public void setProcessInfo(ProcessInfo processInfo) {
        this.processInfo = processInfo;
        if(processInfo.getImage() != null) icon.setImage(SwingFXUtils.toFXImage(processInfo.getImage(), null));
        else icon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/Process.png"))));
        name.setText(processInfo.getName());
        size.setText("内存占用: "+Utils.calculateSize(processInfo.getMemoryUsage()));
    }

    public void initialize() {
        action.setOnAction(event -> {
            if(processInfo != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("确定结束进程？");
                alert.setHeaderText("确定要结束 "+processInfo.getName()+" 的进程吗？结束后将丢失未保存的更改。");
                alert.showAndWait().ifPresent(response -> {
                    if(response == ButtonType.OK) {
                        MemoryCleanLogic.killProcess(processInfo.getName());
                        Task<List<ProcessInfo>> getProcessListTask = processManagementController.getProcessListTask();
                        //                            删除自己
                        processManagementController.processContainer.getChildren().remove(processManagementController.processContainer.getChildren().indexOf(action.getParent()));
                        new Thread(getProcessListTask).start();

                    }
                });
            }
        });
    }
}
