package org.dev.optiboost.Controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.dev.optiboost.Logic.StartUpManagementLogic;
import org.dev.optiboost.entity.StartUpInfo;

import java.util.Objects;

public class StartUpItemController {

    @FXML
    public ImageView icon;

    @FXML
    public Text name, impact;
    public ToggleButton switchButton;

    StartUpInfo startUpInfo;

    public void setStartUpInfo(StartUpInfo startUpInfo) {
        this.startUpInfo = startUpInfo;
        if (startUpInfo.getIcon() != null) icon.setImage(SwingFXUtils.toFXImage(startUpInfo.getIcon(), null));
        else icon.setImage(new Image(Objects.requireNonNull(StartUpItemController.class.getResourceAsStream("/org/dev/optiboost/assets/StartUp.png"))));
        String nm = startUpInfo.getName();
        if (nm.length() > 20) nm = nm.substring(0, 20) + "...";
        name.setText(nm);
        impact.setText("启动影响: 未计量");
        switchButton.setText(startUpInfo.isEnable() ? "已启用" : "已禁用");
        switchButton.setSelected(startUpInfo.isEnable());
    }

    public void initialize() {
        switchButton.setOnAction(event -> {
            if (switchButton.isSelected()) {
                switchButton.setText("已启用");
                StartUpManagementLogic.enableStartupItem(startUpInfo.getName());
            } else {
                switchButton.setText("已禁用");
                StartUpManagementLogic.disableStartupItem(startUpInfo.getName());
            }
        });
    }
}
