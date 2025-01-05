package org.dev.optiboost.Controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.dev.optiboost.Logic.DiskCleanLogic;
import org.dev.optiboost.Logic.MonitorLogic;
import org.dev.optiboost.Utils;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MemoryUsageController {

    @FXML
    public ProgressBar tempProgressBar;
    @FXML
    public Button tempFileCleanBtn;
    @FXML
    public HBox memoryHeadContainer;
    @FXML
    public Label processManagementValue, cDiskUsageValue, lastStartUpValue;
    @FXML
    public VBox mainProcessManagement, mainDiskClean, mainStartUp, mainOurProject;
    @FXML
    private ProgressBar memoryProgressBar;

    @FXML
    private Label memoryUsageLabel, tempUsageLabel;

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    // 初始化方法，可以在这里设置初始值或添加事件监听器等
    public void initialize() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // 每10秒调用一次 updateProgress
        scheduler.scheduleAtFixedRate(() -> Platform.runLater(this::updateProgress), 0, 3, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(() -> Platform.runLater(this::calculateBasic), 0, 10, TimeUnit.SECONDS);
        calculateBasic();
        bindJump();
        memoryHeadContainer.setOnMouseClicked(event -> {
//            打开链接
            Utils.openLink("https://blog.csdn.net/paschen/article/details/52829867");
        });
    }

    public void updateProgress() {
        double usedMemory = MonitorLogic.getMemoryUsage(); // 已用内存
        double totalMemory = MonitorLogic.getWholeMemory(); // 总内存
        double progress = usedMemory / totalMemory; // 计算进度
        double tempFileSize = DiskCleanLogic.getTempFilesSize();

        String usedMemoryGB = Utils.calculateSize(usedMemory);
        String totalMemoryGB = Utils.calculateSize(totalMemory);

        // 更新进度条和标签
        memoryProgressBar.setProgress(progress);
        tempProgressBar.setProgress(tempFileSize/usedMemory*30);
        memoryUsageLabel.setText(String.format("%.2f%%", progress * 100));
        tempUsageLabel.setText(Utils.calculateSize(tempFileSize));
    }

    public void calculateBasic(){
        Task<List<String>> task = new Task<List<String>>() {
            @Override
            protected List<String> call() throws Exception {
                return MonitorLogic.getBasicInfo();
            }
        };

        task.setOnSucceeded(event -> {
            List<String> basicInfo = task.getValue();
            processManagementValue.setText(basicInfo.get(0));
            cDiskUsageValue.setText(basicInfo.get(1));
            lastStartUpValue.setText(basicInfo.get(2));
        });

        new Thread(task).start();
    }

    public void bindJump(){
        mainProcessManagement.setOnMouseClicked(event -> {
            mainController.loadProcessManagementPage();
        });

        mainDiskClean.setOnMouseClicked(event -> {
            mainController.loadDiskCleanPage();
        });

        mainStartUp.setOnMouseClicked(event -> {
            mainController.loadStartUpManagementPage();
        });

        mainOurProject.setOnMouseClicked(event -> {
            Utils.openLink("https://github.com/optiboost-dev/optiboost");
        });
    }

    @FXML
    private void cleanTempFiles() {
        try{
            tempFileCleanBtn.setDisable(true);
            tempFileCleanBtn.setText("正在清理中...");
            tempFileCleanBtn.setTextFill(Paint.valueOf("#000000"));
            tempFileCleanBtn.setBackground(Background.fill(Paint.valueOf("#eeeeee")));
            Task<Boolean> task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return DiskCleanLogic.cleanTempFiles();
                }
            };

            new Thread(task).start();

            Task<Void> sleepTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(1000);
                    return null;
                }
            };

            sleepTask.setOnSucceeded(event -> {
                tempFileCleanBtn.setText("清理临时文件");
            });

            task.setOnSucceeded(event -> {
                tempFileCleanBtn.setDisable(false);
                tempFileCleanBtn.setText("清理完成✓");
                tempFileCleanBtn.setTextFill(Paint.valueOf("#000000"));
                tempFileCleanBtn.setBackground(Background.fill(Paint.valueOf("#005fb8")));
                new Thread(sleepTask).start();

            });

        } catch (Exception e) {
            e.printStackTrace();
        }

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
