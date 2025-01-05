package org.dev.optiboost.Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.dev.optiboost.Cell.DiskApplicationCell;
import org.dev.optiboost.Logic.DiskApplicationLogic;
import org.dev.optiboost.Logic.DiskUsageLogic;
import org.dev.optiboost.entity.DiskApplicationNode;

import java.util.List;

public class DiskCleanApplicationController {
    @FXML
    public ScrollPane diskCleanApplicationContainer;

    @FXML
    public VBox diskCleanApplicationMainPart;

    @FXML
    public ComboBox filterConditionComboBox,sortByComboBox;

    public DiskApplicationLogic diskApplicationLogic;

    public String filterCondition = "all", sortCondition = "name", searchCondition = "";

    @FXML
    public TextField searchTextField;
    @FXML
    public Label searchButton;

    @FXML
    public void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/dev/optiboost/fxml/common/loading.fxml"));
            Parent load = loader.load();
            VBox loading;
            if(load instanceof VBox){
                loading = (VBox) load;
                loading.setPrefWidth(700);
                loading.setPrefHeight(490);
                diskCleanApplicationMainPart.getChildren().add(loading);
            } else {
                loading = null;
            }
            sortByComboBox.getSelectionModel().select(0);
            Task<List<String>> getAllDiskNamesTask = getListTask();
            new Thread(getAllDiskNamesTask).start();
            Task<List<DiskApplicationNode>> getApplicationWithFilterTask = getApplicationWithFilter("name", "all", "");
            new Thread(getApplicationWithFilterTask).start();
            filterConditionComboBox.setOnAction(event -> {
                filterCondition = filterConditionComboBox.getSelectionModel().getSelectedItem().toString();
                if(filterCondition.equals("全部磁盘")){
                    filterCondition = "all";
                }
            });
            sortByComboBox.setOnAction(event -> {
                sortCondition = sortByComboBox.getSelectionModel().getSelectedItem().toString();
                if(sortCondition.equals("应用名称")){
                    sortCondition = "name";
                } else if(sortCondition.equals("应用大小（从大到小）")){
                    sortCondition = "size";
                }
            });
            searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                searchCondition = newValue;
            });
            searchButton.setOnMouseClicked(event -> {
                diskCleanApplicationMainPart.getChildren().clear();
                diskCleanApplicationMainPart.getChildren().add(loading);
                Task<List<DiskApplicationNode>> getApplicationWithFilterTask1 = getApplicationWithFilter(sortCondition, filterCondition, searchCondition);
                new Thread(getApplicationWithFilterTask1).start();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Task<List<String>> getListTask() {
        Task<List<String>> getAllDiskNamesTask = new Task<List<String>>() {
            @Override
            protected List<String> call() throws Exception {
                return DiskUsageLogic.getAllDiskNames();
            }
        };

        getAllDiskNamesTask.setOnSucceeded(event -> {
            System.out.println(event);
            List<String> diskNames = getAllDiskNamesTask.getValue();
            filterConditionComboBox.getItems().add("全部磁盘");
            for (String diskName : diskNames) {
                filterConditionComboBox.getItems().add(diskName);
            }
            filterConditionComboBox.getSelectionModel().select(0);
        });
        return getAllDiskNamesTask;
    }

    private Task<List<DiskApplicationNode>> getApplicationWithFilter(String sortCondition, String filterCondition, String keyword) {
        Task<List<DiskApplicationNode>> getApplicationWithFilterTask = new Task<>() {
            @Override
            protected List<DiskApplicationNode> call() throws Exception {
                if(diskApplicationLogic == null){
                    diskApplicationLogic = new DiskApplicationLogic();
                }
                return diskApplicationLogic.getDiskApplicationNodeListWithOption(sortCondition, filterCondition, keyword);
            }
        };

        getApplicationWithFilterTask.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                diskCleanApplicationMainPart.getChildren().clear();

                ListView<DiskApplicationNode> listView = new ListView<>();
                listView.setPrefHeight(490);
                listView.setPrefWidth(710);
                listView.setStyle("-fx-background-color: transparent;-fx-background: transparent;-fx-border-width: 0;-fx-padding: 0;-fx-border-color: transparent;");
                listView.setFocusTraversable(false);
                List<DiskApplicationNode> nodes = getApplicationWithFilterTask.getValue();

                listView.setItems(FXCollections.observableArrayList(nodes));
                listView.setCellFactory(listViewParam -> {
                    DiskApplicationCell cell = new DiskApplicationCell();
                    cell.setController(DiskCleanApplicationController.this);  // Pass the controller to each cell
                    return cell;
                });

                diskCleanApplicationMainPart.getChildren().add(listView);
            });
        });

        return getApplicationWithFilterTask;
    }

    public void updateListView() {
        diskCleanApplicationMainPart.getChildren().clear();
        Task<List<DiskApplicationNode>> getApplicationWithFilterTask = getApplicationWithFilter(sortCondition, filterCondition, searchCondition);
        new Thread(getApplicationWithFilterTask).start();
    }
}
