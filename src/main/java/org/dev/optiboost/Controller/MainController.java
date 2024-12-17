package org.dev.optiboost.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainController {
    @FXML
    public Button memoryCleanButton, diskCleanButton, monitorButton;

    @FXML
    public StackPane content;

    @FXML
    public VBox navBar;

    @FXML
    public ImageView navIcon1, navIcon2, navIcon3;

    private int navigationIndex;

    @FXML
    public void initialize() {
        loadScreen("/org/dev/optiboost/fxml/memory-clean.fxml");
        setNavigationIndex(1);
    }

    private void loadScreen(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Node screen = loader.load();
            content.getChildren().setAll(screen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changeIcon(int index){
        switch (navigationIndex){
            case 1:
                navIcon1.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/MemoryCleanIcon.png"))));
                break;
            case 2:
                navIcon2.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/DiskCleanIcon.png"))));
                break;
            case 3:
                navIcon3.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/MonitorIcon.png"))));
                break;
        }
        switch(index){
            case 1:
                navIcon1.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/MemoryCleanIconSelected.png"))));
                break;
            case 2:
                navIcon2.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/DiskCleanIconSelected.png"))));
                break;
            case 3:
                navIcon3.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/MonitorIconSelected.png"))));
                break;
        }
    }

    private void setNavigationIndex(int index) {
        switch (index) {
            case 1:
                memoryCleanButton.getStyleClass().add("nav-selected");
                diskCleanButton.getStyleClass().remove("nav-selected");
                monitorButton.getStyleClass().remove("nav-selected");
                break;
            case 2:
                diskCleanButton.getStyleClass().add("nav-selected");
                memoryCleanButton.getStyleClass().remove("nav-selected");
                monitorButton.getStyleClass().remove("nav-selected");
                break;
            case 3:
                monitorButton.getStyleClass().add("nav-selected");
                memoryCleanButton.getStyleClass().remove("nav-selected");
                diskCleanButton.getStyleClass().remove("nav-selected");
                break;
        }
        changeIcon(index);
        navigationIndex = index;
    }



    @FXML
    protected void loadMemoryCleanPage(ActionEvent event) {
        if(navigationIndex == 1) return;
        setNavigationIndex(1);
        loadScreen("/org/dev/optiboost/fxml/memory-clean.fxml");
    }

    @FXML
    protected void loadDiskCleanPage(ActionEvent event) {
        if(navigationIndex == 2) return;
        setNavigationIndex(2);
        loadScreen("/org/dev/optiboost/fxml/disk-clean.fxml");
    }

    @FXML
    protected void loadMonitorPage(ActionEvent event) {
        if(navigationIndex == 3) return;
        setNavigationIndex(3);
        loadScreen("/org/dev/optiboost/fxml/monitor.fxml");
    }
}
