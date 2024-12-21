package org.dev.optiboost.Controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import org.dev.optiboost.Logic.MemoryShowLogic;
import org.dev.optiboost.Logic.MonitorLogic;
import org.dev.optiboost.Utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MemoryUsageController {

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label memoryUsageLabel;


    // 初始化方法，可以在这里设置初始值或添加事件监听器等
    public void initialize() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // 每10秒调用一次 updateProgress
        scheduler.scheduleAtFixedRate(() -> Platform.runLater(this::updateProgress), 0, 3, TimeUnit.SECONDS);

    }

    public void updateProgress() {
        double usedMemory = MonitorLogic.getMemoryUsage(); // 已用内存
        double totalMemory = MonitorLogic.getWholeMemory(); // 总内存
        double progress = usedMemory / totalMemory; // 计算进度

        String usedMemoryGB = Utils.calculateSize(usedMemory);
        String totalMemoryGB = Utils.calculateSize(totalMemory);

        // 更新进度条和标签
        progressBar.setProgress(progress);
        memoryUsageLabel.setText("已用内存/总内存：" +usedMemoryGB + "/" + totalMemoryGB);
    }


    @FXML
    private void goToProcessTable() {
        try {
            // 加载ProcessTable.fxml界面
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/dev/optiboost/fxml/ProcessTable.fxml"));
            Parent root = loader.load();

            // 创建一个新的场景
            Scene scene = new Scene(root);

            // 创建一个新的舞台
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("进程管理"); // 设置新界面的标题
            newStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
