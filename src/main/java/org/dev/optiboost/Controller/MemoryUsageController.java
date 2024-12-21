package org.dev.optiboost.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import org.dev.optiboost.Logic.MemoryShowLogic;

public class MemoryUsageController {

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label memoryUsageLabel;

    MemoryShowLogic memoryShowLogic;

    // 初始化方法，可以在这里设置初始值或添加事件监听器等
    public void initialize() {
        memoryShowLogic = new MemoryShowLogic();
        // 假设这里获取了实际的内存使用情况
        double usedMemory = memoryShowLogic.getUsedMemory(); // 已用内存
        double totalMemory = memoryShowLogic.getTotalMemory(); // 总内存
        double progress = usedMemory / totalMemory; // 计算进度

        String usedMemoryGB = String.format("%.2f", (double) usedMemory / 1024 / 1024 / 1024);
        String totalMemoryGB = String.format("%.2f", (double) totalMemory / 1024 / 1024 / 1024);


        // 更新进度条和标签
        progressBar.setProgress(progress);
        memoryUsageLabel.setText("已用内存/总内存：" +usedMemoryGB + "GB/" + totalMemoryGB + "GB");
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
