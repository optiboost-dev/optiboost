package org.dev.optiboost.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class DiskCleanInnerController {
    @FXML
    public VBox navBar;
    @FXML
    public Button downloadFileCleanButton, applicationCleanButton;
    @FXML
    public ImageView navIcon1, navIcon2;
    @FXML
    public StackPane content;

    private int navigationIndex;

    public void setMessage(String message) {
        if(message.equals("download")) {
            loadDownloadFileCleanPage(null);
        } else if(message.equals("application")) {
            loadApplicationCleanPage(null);
        }
    }

    @FXML
    public void initialize() {
//        loadScreen("/org/dev/optiboost/fxml/disk-clean-download.fxml");
//        setNavigationIndex(1);
    }

    private void loadScreen(String fxmlFile) {
        new Thread(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                Node screen = loader.load();
                Platform.runLater(() -> content.getChildren().setAll(screen));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void changeIcon(int index){
        switch (navigationIndex){
            case 1:
                navIcon1.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/DownloadFileIcon.png"))));
                break;
            case 2:
                navIcon2.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/ApplicationCleanIcon.png"))));
                break;
        }
        switch(index){
            case 1:
                navIcon1.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/DownloadFileIconSelected.png"))));
                break;
            case 2:
                navIcon2.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/dev/optiboost/assets/ApplicationCleanIconSelected.png"))));
                break;
        }
    }

    private void setNavigationIndex(int index) {
        switch (index) {
            case 1:
                downloadFileCleanButton.getStyleClass().add("nav-selected");
                applicationCleanButton.getStyleClass().remove("nav-selected");
                break;
            case 2:
                applicationCleanButton.getStyleClass().add("nav-selected");
                downloadFileCleanButton.getStyleClass().remove("nav-selected");
                break;
        }
        changeIcon(index);
        navigationIndex = index;
    }

    @FXML
    protected void loadDownloadFileCleanPage(ActionEvent event){
        if(navigationIndex == 1){
            return;
        }
        loadScreen("/org/dev/optiboost/fxml/disk-clean-download.fxml");
        setNavigationIndex(1);
    }

    @FXML
    protected void loadApplicationCleanPage(ActionEvent event){
        if(navigationIndex == 2){
            return;
        }
        System.out.println("start loadApplicationCleanPage");
        loadScreen("/org/dev/optiboost/fxml/disk-clean-application.fxml");
        System.out.println("loaded ApplicationCleanPage");
        setNavigationIndex(2);
    }
}
