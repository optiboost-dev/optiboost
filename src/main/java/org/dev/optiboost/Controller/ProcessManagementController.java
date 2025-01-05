package org.dev.optiboost.Controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import org.dev.optiboost.Logic.MemoryShowLogic;
import org.dev.optiboost.entity.ProcessInfo;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class ProcessManagementController {

    @FXML
    public VBox processContainer;

    public void initialize() {
        try {
            startLoading();
            Task<List<ProcessInfo>> getProcessListTask = getProcessListTask();
            new Thread(getProcessListTask).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startLoading() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/dev/optiboost/fxml/common/loading.fxml"));
        Parent load = loader.load();
        VBox loading;
        processContainer.getChildren().clear();
        if (load instanceof VBox) {
            loading = (VBox) load;
            loading.setPrefWidth(340);
            loading.setPrefHeight(490);
            processContainer.getChildren().add(loading);
        } else {
            loading = null;
        }
    }

    public Task<List<ProcessInfo>> getProcessListTask() {
        Task<List<ProcessInfo>> getProcessListTask = new Task<>() {
            @Override
            protected List<ProcessInfo> call() throws Exception {
                return MemoryShowLogic.getMemoryInfo();
            }
        };

        getProcessListTask.setOnSucceeded(event -> {
            List<ProcessInfo> processInfoList = getProcessListTask.getValue();
            processContainer.getChildren().clear();
            for (ProcessInfo processInfo : processInfoList) {
                try {
                    FXMLLoader processItemLoader = new FXMLLoader(getClass().getResource("/org/dev/optiboost/fxml/common/processItem.fxml"));
                    Parent processItem = processItemLoader.load();
                    ProcessItemController processItemController = processItemLoader.getController();
                    processItemController.setProcessManagementController(this);
                    processItemController.setProcessInfo(processInfo);
                    processContainer.getChildren().add(processItem);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return getProcessListTask;
    }

}
