package org.dev.optiboost.Controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.dev.optiboost.Logic.MonitorLogic;
import org.dev.optiboost.Utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


// 监控控制器类
public class MonitorController {
    @FXML
    public Arc gpuArc;
    @FXML
    private Arc cpuArc;
    @FXML
    private Text cpuPercentageText;
    @FXML
    private Text cpuDataText;
    @FXML
    private Arc memoryArc;
    @FXML
    private Text memoryPercentageText;
    @FXML
    private Text memoryDataText;
    @FXML
    private Arc diskArc;
    @FXML
    private Text diskPercentageText;
    @FXML
    private Text diskDataText;
    @FXML
    private Text gpuPercentageText;
    @FXML
    private Text gpuDataText;

    // 创建包含系统性能监控图表的场景，重点优化定时器部分逻辑，确保持续更新
    public void setupMonitorScene() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            double cpuUsage = MonitorLogic.getCpuUsage();
            double memoryUsage = MonitorLogic.getMemoryUsage();
            double diskUsage = MonitorLogic.getDiskUsage();
            double totalMemory = MonitorLogic.getWholeMemory();
            double totalDiskSpace = MonitorLogic.getDiskTotalSpace();
            double gpuUsage = MonitorLogic.getGPUUsage();

            Platform.runLater(() -> {
                updateArcAndText(cpuArc, cpuPercentageText, cpuDataText, cpuUsage, -1);
                updateArcAndText(memoryArc, memoryPercentageText, memoryDataText, memoryUsage, totalMemory);
                updateArcAndText(diskArc, diskPercentageText, diskDataText, diskUsage, totalDiskSpace);
                updateArcAndText(gpuArc, gpuPercentageText, gpuDataText, gpuUsage, -1);
            });
        }, 0, MonitorLogic.UPDATE_INTERVAL, TimeUnit.MILLISECONDS);
    }


    private void updateArcAndText(Arc arc, Text percentageText, Text dataText, double usage, double total) {
        double percentUsage;
        if(total == -1) {
            percentUsage = usage;
        }else{
            percentUsage = (usage / total) * 100;
        }
        if(percentUsage > 90) {
            arc.setFill(Paint.valueOf("red"));
        }else if(percentUsage > 70) {
            arc.setFill(Paint.valueOf("orange"));
        }else{
            arc.setFill(Paint.valueOf("#005fb8"));
        }
        if (arc == cpuArc) {
            arc.setLength(usage * 3.6);
            percentageText.setText(String.format("%.1f%%", usage));
            dataText.setText("");
        } else if (arc == memoryArc) {
            double t = (usage / total) * 100;
            arc.setLength(t * 3.6);
            percentageText.setText(String.format("%.1f%%", t));
            dataText.setText(Utils.calculateSize(usage) + "/" + Utils.calculateSize(total));
        } else if (arc == diskArc) {
            double t = (usage / total) * 100;
            arc.setLength(t * 3.6);
            percentageText.setText(String.format("%.1f%%", t));
            dataText.setText(Utils.calculateSize(usage) + "/" + Utils.calculateSize(total));
        } else if (arc == gpuArc) {
            arc.setLength(usage * 3.6);
            percentageText.setText(String.format("%.1f%%", usage));
            dataText.setText("");
        }
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

}