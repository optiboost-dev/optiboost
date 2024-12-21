package org.dev.optiboost.Controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
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

    // 创建包含系统性能监控图表的场景，重点优化定时器部分逻辑，确保持续更新
    public void setupMonitorScene() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            double cpuUsage = MonitorLogic.getCpuUsage();
            double memoryUsage = MonitorLogic.getMemoryUsage();
            double diskUsage = MonitorLogic.getDiskUsage();
            double totalMemory = MonitorLogic.getWholeMemory();
            double totalDiskSpace = MonitorLogic.getDiskTotalSpace();

            Platform.runLater(() -> {
                updateArcAndText(cpuArc, cpuPercentageText, cpuDataText, cpuUsage, 100.0);
                updateArcAndText(memoryArc, memoryPercentageText, memoryDataText, memoryUsage, totalMemory);
                updateArcAndText(diskArc, diskPercentageText, diskDataText, diskUsage, totalDiskSpace);
            });
        }, 0, MonitorLogic.UPDATE_INTERVAL, TimeUnit.MILLISECONDS);
    }


    private void updateArcAndText(Arc arc, Text percentageText, Text dataText, double usage, double total) {
        if (arc == cpuArc) {
            arc.setLength(usage * 3.6);
            percentageText.setText(String.format("%.1f%%", usage));
            dataText.setText(String.format("%.1f/%.1f%%", usage, 100.0));
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