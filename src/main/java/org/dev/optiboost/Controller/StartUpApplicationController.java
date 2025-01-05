package org.dev.optiboost.Controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import org.dev.optiboost.Logic.StartUpManagementLogic;
import org.dev.optiboost.entity.StartUpInfo;

import java.io.IOException;
import java.util.List;

public class StartUpApplicationController {
    @FXML
    public VBox startupContainer;

    public void initialize() {
        startLoading();
        Task<List<StartUpInfo>> task = new Task<List<StartUpInfo>>() {
            @Override
            protected List<StartUpInfo> call() throws Exception {
                return StartUpManagementLogic.getAllStartupItems();
            }
        };

        task.setOnSucceeded(event -> {
            List<StartUpInfo> startUpInfos = task.getValue();
            startupContainer.getChildren().clear();
            for (StartUpInfo startUpInfo : startUpInfos) {
                FXMLLoader startUpItemLoader = new FXMLLoader(getClass().getResource("/org/dev/optiboost/fxml/common/startUpItem.fxml"));
                try {
                    Parent startUpItem = startUpItemLoader.load();
                    StartUpItemController startUpItemController = startUpItemLoader.getController();
                    startUpItemController.setStartUpInfo(startUpInfo);
                    startupContainer.getChildren().add(startUpItem);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        new Thread(task).start();
    }

    public void startLoading() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/dev/optiboost/fxml/common/loading.fxml"));
        Parent load = null;
        try {
            load = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        VBox loading;
        startupContainer.getChildren().clear();
        if (load instanceof VBox) {
            loading = (VBox) load;
            loading.setPrefWidth(340);
            loading.setPrefHeight(490);
            startupContainer.getChildren().add(loading);
        } else {
            loading = null;
        }
    }
}
