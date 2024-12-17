package org.dev.optiboost.Controller;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TopBarController {

    @FXML
    public StackPane minimizeButtonContainer, closeButtonContainer;

    @FXML
    private ImageView minimizeButton, closeButton;

    @FXML
    private HBox topBar;

    private double offsetX, offsetY;

    private void handleMousePressed(MouseEvent event) {
        Stage stage = (Stage) topBar.getScene().getWindow();
        offsetX = event.getSceneX();
        offsetY = event.getSceneY();
    }

    private void handleMouseDragged(MouseEvent event) {
        Stage stage = (Stage) topBar.getScene().getWindow();
        stage.setX(event.getScreenX() - offsetX);
        stage.setY(event.getScreenY() - offsetY);
    }

    @FXML
    public void initialize() {
        minimizeButtonContainer.setOnMouseClicked(e -> {
            Stage stage = (Stage) minimizeButton.getScene().getWindow();
            stage.setIconified(true);
        });

        // 关闭按钮功能
        closeButtonContainer.setOnMouseClicked(e -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });
        topBar.setOnMousePressed(this::handleMousePressed);
        topBar.setOnMouseDragged(this::handleMouseDragged);
    }
}
