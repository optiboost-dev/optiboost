package org.dev.optiboost.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.dev.optiboost.Logic.MemoryCleanLogic;
import org.dev.optiboost.Logic.MemoryShowLogic;
import org.dev.optiboost.Logic.ProcessIcon;
import org.dev.optiboost.entity.ProcessData;
import org.dev.optiboost.entity.ProcessInfo;

import java.util.List;

public class ProcessTableController {
    @FXML
    public VBox loading;
    @FXML
    private TableView<ProcessData> processTable;
    @FXML
    private TableColumn<ProcessData, Boolean> selectColumn;
    @FXML
    private TableColumn<ProcessData, Image> iconColumn;
    @FXML
    private TableColumn<ProcessData, String> nameColumn;
    @FXML
    private TableColumn<ProcessData, String> memoryColumn;



    private ObservableList<ProcessData> processDataList = FXCollections.observableArrayList();


    public void initialize() {

        // 设置选择列
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));

        // 设置图标列
        iconColumn.setCellValueFactory(new PropertyValueFactory<>("icon"));
        iconColumn.setCellFactory(column -> new ImageViewCell<>());

        // 设置名称列
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // 设置内存列
        memoryColumn.setCellValueFactory(cellData -> cellData.getValue().memoryProperty());

        // 添加表头
        //processDataList.add(new ProcessData(null, "进程图标", "进程名称", "占用内存"));

        // 添加数据
        memoryTask();

    }

    private void memoryTask(){
        Task<List<ProcessInfo>> getMemoryInfoTask = new Task<>() {
            @Override
            protected List<ProcessInfo> call() {
                processTable.setVisible(false);
                loading.setVisible(true);
                loading.setMaxHeight(400);
                return MemoryShowLogic.getMemoryInfo();
            }
        };

        getMemoryInfoTask.setOnSucceeded(event -> {
            List<ProcessInfo> memoryInfo = getMemoryInfoTask.getValue();
            initProcessTable(memoryInfo);
        });

        new Thread(getMemoryInfoTask).start();
    }

    private void initProcessTable(List<ProcessInfo> memoryInfo) {
        processDataList.clear();
        for (int i = 1; i <= 20; i++) {
            ProcessData processData = new ProcessData(memoryInfo.get(memoryInfo.size() - i));
            Image icon = processData.getIcon(); // 传入的参数
            String name = processData.getName(); // 传入的参数
            String memory = processData.getMemory(); // 传入的参数 + 固定的文字
            processDataList.add(new ProcessData(icon, name, memory));
        }

        processTable.setItems(processDataList);
        processTable.setEditable(true);
        loading.setVisible(false);
        loading.setMaxHeight(0);
        processTable.setVisible(true);
    }

    @FXML
    private void endProcesses() {
        processDataList.stream()
                .filter(ProcessData::isSelected)
                .forEach(data -> endProcess(data.getName()));
        memoryTask();
    }

    private void endProcess(String processName) {
        if (processName == "idea.exe") return;

        MemoryCleanLogic memoryCleanLogic = new MemoryCleanLogic();
        memoryCleanLogic.killProcess(processName);
        // 调用结束进程的函数
        System.out.println("结束进程: " + processName);
    }

    // 自定义单元格渲染器
    private static class ImageViewCell<S, T> extends TableCell<S, T> {
        private ImageView imageView = new ImageView();

        public ImageViewCell() {
            imageView.setFitWidth(32);
            imageView.setFitHeight(32);
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                imageView.setImage((Image) item);
                setGraphic(imageView);
            }
        }
    }
}
