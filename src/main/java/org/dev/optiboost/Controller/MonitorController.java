package org.dev.optiboost.Controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.shape.Arc;
import javafx.scene.text.Text;
import javafx.util.Duration;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import java.util.List;

// 监控控制器类
public class MonitorController {
    private static final int UPDATE_INTERVAL = 1000; // 数据更新间隔，单位毫秒
    private final SystemInfo si = new SystemInfo();
    private final HardwareAbstractionLayer hal = si.getHardware();
    private final CentralProcessor cpu = hal.getProcessor();
    private final OperatingSystem os = si.getOperatingSystem();

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

    // 获取CPU使用率，进一步优化计算逻辑以及异常处理
    public double getCpuUsage() {
        try {
            long[] prevTicks = cpu.getSystemCpuLoadTicks();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long[] ticks = cpu.getSystemCpuLoadTicks();

            long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
            long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
            long system = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
            long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
            long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
            long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
            long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
            long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
            long totalCpu = user + nice + system + idle + iowait + irq + softirq + steal;

            if (totalCpu == 0) {
                return 0;
            }
            return (double) (totalCpu - idle) / totalCpu * 100;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 获取内存使用率
    public double getMemoryUsage() {
        long totalMemory = hal.getMemory().getTotal();
        long availableMemory = hal.getMemory().getAvailable();
        return 100d - (100d * availableMemory / totalMemory);
    }

    // 获取磁盘使用率，优化计算逻辑
    public double getDiskUsage() {
        List<OSFileStore> fileStores = os.getFileSystem().getFileStores();
        if (fileStores.isEmpty()) {
            return 0;
        }

        long totalDiskSpace = 0;
        long usedDiskSpace = 0;
        for (OSFileStore store : fileStores) {
            totalDiskSpace += store.getTotalSpace();
            usedDiskSpace += store.getTotalSpace() - store.getFreeSpace();
        }
        return (double) usedDiskSpace / totalDiskSpace * 100;
    }

    // 创建包含系统性能监控图表的场景，重点优化定时器部分逻辑，确保持续更新
    public void setupMonitorScene() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(UPDATE_INTERVAL), event -> {
            double cpuUsage = getCpuUsage();
            double memoryUsage = getMemoryUsage();
            double diskUsage = getDiskUsage();

            Platform.runLater(() -> {
                try {
                    updateArcAndText(cpuArc, cpuPercentageText, cpuDataText, cpuUsage);
                    updateArcAndText(memoryArc, memoryPercentageText, memoryDataText, memoryUsage);
                    updateArcAndText(diskArc, diskPercentageText, diskDataText, diskUsage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            // 主动释放线程资源，避免潜在的阻塞影响后续更新
            Thread.yield();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void updateArcAndText(Arc arc, Text percentageText, Text dataText, double usage) {
        // 更新弧形进度
        arc.setLength(usage * 3.6);
        // 更新百分比文本
        percentageText.setText(String.format("%.1f%%", usage));
        // 更新具体数据文本 （这里根据不同资源类型计算并设置对应的数据展示格式）
        if (arc == cpuArc) {
            dataText.setText(String.format("%.1f/%.1f%%", usage, 100.0));
        } else if (arc == memoryArc) {
            long totalMemory = hal.getMemory().getTotal();
            double usedMemory = (totalMemory / 1024.0 / 1024.0 / 1024.0) * (usage / 100.0);
            long availableMemory = (long) ((totalMemory / 1024.0 / 1024.0 / 1024.0) - usedMemory);
            dataText.setText(String.format("%.1f/%.1f GB", usedMemory, (totalMemory / 1024.0 / 1024.0 / 1024.0)));
        } else if (arc == diskArc) {
            double totalDiskSpace = 0;
            double usedDiskSpace = 0;
            List<OSFileStore> fileStores = os.getFileSystem().getFileStores();
            if (!fileStores.isEmpty()) {
                for (OSFileStore store : fileStores) {
                    totalDiskSpace += store.getTotalSpace() / 1024.0 / 1024.0 / 1024.0;
                    usedDiskSpace += (store.getTotalSpace() - store.getFreeSpace()) / 1024.0 / 1024.0 / 1024.0;
                }
                dataText.setText(String.format("%.1f/%.1f GB", usedDiskSpace, totalDiskSpace));
            } else {
                dataText.setText("0/0 GB");
            }
        }
    }
}