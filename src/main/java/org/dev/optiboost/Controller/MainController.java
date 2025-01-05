package org.dev.optiboost.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.dev.optiboost.Logic.MemoryCleanLogic;

import java.io.IOException;
import java.util.Objects;

public class MainController {
    @FXML
    public Button memoryCleanButton, diskCleanButton, monitorButton, processManagementButton, startUpManagementButton;

    @FXML
    public StackPane content;

    @FXML
    public VBox navBar;

    @FXML
    public ImageView navIcon1, navIcon2, navIcon3, navIcon4, navIcon5;

    private int navigationIndex;

    @FXML
    public void initialize() {
        loadScreen("/org/dev/optiboost/fxml/main-page.fxml");
        setNavigationIndex(1);
    }

    private void loadScreen(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Node screen = loader.load();
            if (loader.getController() instanceof MonitorController) {
                MonitorController monitorController = loader.getController();
                monitorController.setMainController(this);
            }else if(loader.getController() instanceof MemoryUsageController) {
                MemoryUsageController memoryUsageController = loader.getController();
                memoryUsageController.setMainController(this);
            }
            content.getChildren().clear();
            content.getChildren().setAll(screen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changeIcon(int index){
        switch (navigationIndex){
            case 1:
                navIcon1.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/HomeIcon.png"))));
                break;
            case 2:
                navIcon2.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/DiskCleanIcon.png"))));
                break;
            case 3:
                navIcon3.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/MonitorIcon.png"))));
                break;
            case 4:
                navIcon4.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/ProcessManagementIcon.png"))));
                break;
            case 5:
                navIcon5.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/StartUpIcon.png"))));
                break;
        }
        switch(index){
            case 1:
                navIcon1.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/HomeIconSelected.png"))));
                break;
            case 2:
                navIcon2.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/DiskCleanIconSelected.png"))));
                break;
            case 3:
                navIcon3.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/MonitorIconSelected.png"))));
                break;
            case 4:
                navIcon4.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/ProcessManagementIconSelected.png"))));
                break;
            case 5:
                navIcon5.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/StartUpIconSelected.png"))));
                break;
        }
    }

    public void setNavigationIndex(int index) {
        switch (index) {
            case 1:
                memoryCleanButton.getStyleClass().add("nav-selected");
                diskCleanButton.getStyleClass().remove("nav-selected");
                monitorButton.getStyleClass().remove("nav-selected");
                processManagementButton.getStyleClass().remove("nav-selected");
                startUpManagementButton.getStyleClass().remove("nav-selected");
                break;
            case 2:
                diskCleanButton.getStyleClass().add("nav-selected");
                memoryCleanButton.getStyleClass().remove("nav-selected");
                monitorButton.getStyleClass().remove("nav-selected");
                processManagementButton.getStyleClass().remove("nav-selected");
                startUpManagementButton.getStyleClass().remove("nav-selected");
                break;
            case 3:
                monitorButton.getStyleClass().add("nav-selected");
                memoryCleanButton.getStyleClass().remove("nav-selected");
                diskCleanButton.getStyleClass().remove("nav-selected");
                processManagementButton.getStyleClass().remove("nav-selected");
                startUpManagementButton.getStyleClass().remove("nav-selected");
                break;
            case 4:
                processManagementButton.getStyleClass().add("nav-selected");
                memoryCleanButton.getStyleClass().remove("nav-selected");
                diskCleanButton.getStyleClass().remove("nav-selected");
                monitorButton.getStyleClass().remove("nav-selected");
                startUpManagementButton.getStyleClass().remove("nav-selected");
                break;
            case 5:
                startUpManagementButton.getStyleClass().add("nav-selected");
                memoryCleanButton.getStyleClass().remove("nav-selected");
                diskCleanButton.getStyleClass().remove("nav-selected");
                monitorButton.getStyleClass().remove("nav-selected");
                processManagementButton.getStyleClass().remove("nav-selected");
                break;
        }
        changeIcon(index);
        navigationIndex = index;
    }



    @FXML
    protected void loadMemoryCleanPage() {
        if(navigationIndex == 1) return;
        setNavigationIndex(1);
        loadScreen("/org/dev/optiboost/fxml/main-page.fxml");
    }

    @FXML
    protected void loadDiskCleanPage() {
        if(navigationIndex == 2) return;
        setNavigationIndex(2);
        loadScreen("/org/dev/optiboost/fxml/disk-clean.fxml");
    }

    @FXML
    protected void loadMonitorPage() {
        if (navigationIndex == 3) return;
        setNavigationIndex(3);
        loadScreen("/org/dev/optiboost/fxml/monitor.fxml");
    }

    @FXML
    protected void loadProcessManagementPage() {
        if (navigationIndex == 4) return;
        setNavigationIndex(4);
        loadScreen("/org/dev/optiboost/fxml/process-management.fxml");
    }

    @FXML
    protected void loadStartUpManagementPage() {
        if (navigationIndex == 5) return;
        setNavigationIndex(5);
        loadScreen("/org/dev/optiboost/fxml/startup-management.fxml");
    }
}
