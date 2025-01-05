package org.dev.optiboost.Controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.dev.optiboost.Logic.MonitorLogic;
import org.dev.optiboost.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


// 监控控制器类
public class MonitorController {
    @FXML
    public ProgressBar cpuTemperatureBar, cpuUsageBar, gpuTemperatureBar, gpuUsageBar, memoryUsageBar, diskUsageBar;
    @FXML
    public Label cpuTemperatureText, cpuUsageText, gpuTemperatureText, gpuUsageText, memoryUsageText, diskUsageText;
    @FXML
    public Label memoryDetailText, diskDetailText;
    @FXML
    public VBox suggestionBox;

    private MainController mainController;

    // 用于设置 MainController 的方法
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    // 创建包含系统性能监控图表的场景，重点优化定时器部分逻辑，确保持续更新
    public void setupMonitorScene() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
//            不保留小数
            int cpuUsage = (int) MonitorLogic.getCpuUsage();
            double memoryUsage = MonitorLogic.getMemoryUsage();
            double diskUsage = MonitorLogic.getDiskUsage();
            double totalMemory = MonitorLogic.getWholeMemory();
            double totalDiskSpace = MonitorLogic.getDiskTotalSpace();
            int gpuUsage = (int) MonitorLogic.getGPUUsage();
            int cpuTemperature = MonitorLogic.getCPUTemperature();
            int gpuTemperature = MonitorLogic.getGPUTemperature();

            Platform.runLater(() -> {
                List<String> suggestions = new ArrayList<>();
                updateArcAndText(cpuTemperatureBar, cpuTemperatureText, cpuTemperature+"度", cpuTemperature, 100, suggestions);
                updateArcAndText(cpuUsageBar, cpuUsageText, cpuUsage+"%", cpuUsage, 100, suggestions);
                updateArcAndText(gpuTemperatureBar, gpuTemperatureText, gpuTemperature+"度", gpuTemperature, 100, suggestions);
                updateArcAndText(gpuUsageBar, gpuUsageText, gpuUsage+"%", gpuUsage, 100, suggestions);
                memoryDetailText.setText(Utils.calculateSize(memoryUsage) + "/" + Utils.calculateSize(totalMemory));
                updateArcAndText(memoryUsageBar, memoryUsageText, ((int) (100*memoryUsage/totalMemory)) +"%", memoryUsage, totalMemory, suggestions);
                diskDetailText.setText(Utils.calculateSize(diskUsage) + "/" + Utils.calculateSize(totalDiskSpace));
                updateArcAndText(diskUsageBar, diskUsageText, ((int) (100*diskUsage/totalDiskSpace)) +"%", diskUsage, totalDiskSpace, suggestions);
                if(!suggestions.isEmpty()) {
                    suggestionBox.getChildren().clear();
                    for(String suggestion : suggestions){
                        suggestionBox.getChildren().add(createSuggestion(suggestion));
                    }
                }else{
                    suggestionBox.getChildren().clear();
                    suggestionBox.getChildren().add(new Text("电脑状态很好，暂无清理建议"));
                }
            });
        }, 0, MonitorLogic.UPDATE_INTERVAL, TimeUnit.MILLISECONDS);
    }


    private void updateArcAndText(ProgressBar bar, Label text, String dataText, double usage, double total, List<String> suggestions) {
        double percentUsage;
        if(total == -1) {
            percentUsage = usage;
        }else{
            percentUsage = (usage / total) * 100;
        }
        if(percentUsage > 80) {
            bar.setStyle("-fx-accent: red;");
            if(bar.equals(memoryUsageBar)) {
                suggestions.add("memory");
            }else if(bar.equals(diskUsageBar)) {
                suggestions.add("disk");
            }
        }else if(percentUsage > 60) {
            bar.setStyle("-fx-accent: orange;");
            if(bar.equals(memoryUsageBar)) {
                suggestions.add("memory");
            }else if(bar.equals(diskUsageBar)) {
                suggestions.add("disk");
            }
        }else{
            bar.setStyle("-fx-accent: #005fb8;");
        }

        text.setText(dataText);
        bar.setProgress(percentUsage / 100);

    }

    public HBox createSuggestion(String option){
        HBox suggestion = new HBox();
        if(Objects.equals(option, "memory")){
            suggestion.getChildren().add(new Text("内存使用过高，建议开始内存清理哦~"));
            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);
            suggestion.getChildren().add(region);
            Label label = new Label(">>前往");
            label.setStyle("-fx-cursor: hand");
            label.setTextFill(Paint.valueOf("#005fb8"));
            label.setOnMouseClicked(event -> {
                // 跳转到内存清理页面
                mainController.loadMemoryCleanPage();
            });
            suggestion.getChildren().add(label);
        }else if(Objects.equals(option, "disk")){
            suggestion.getChildren().add(new Text("磁盘使用过高，建议开始磁盘清理哦~"));
            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);
            suggestion.getChildren().add(region);
            Label label = new Label(">>前往");
            label.setStyle("-fx-cursor: hand");
            label.setTextFill(Paint.valueOf("#005fb8"));
            label.setOnMouseClicked(event -> {
                // 跳转到磁盘清理页面
                mainController.loadDiskCleanPage();
            });
            suggestion.getChildren().add(label);
        }
        return suggestion;
    }

    @FXML
    public void initialize() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                setupMonitorScene();
                return null;
            }
        };

        new Thread(task).start();
    }

    @FXML
    public void startMissionManagement(ActionEvent actionEvent) {
//        启动任务管理器
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                MonitorLogic.startMissionManagement();
                return null;
            }
        };

        new Thread(task).start();

    }
}