package org.dev.optiboost;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/main-layout.fxml")));

        // 设置无装饰窗口
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("Vertical Layout Example");
        primaryStage.setScene(new Scene(root, 450, 650));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}