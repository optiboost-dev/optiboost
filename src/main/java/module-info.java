module org.dev.optiboost {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.management;
    requires jdk.management;
    requires com.sun.jna.platform;
    requires com.sun.jna;
    requires java.desktop;
    requires javafx.swing;
    requires javafx.base;
    requires javafx.graphics;
    requires eu.hansolo.fx.charts;
    requires com.github.oshi;

    opens org.dev.optiboost to javafx.fxml;
    exports org.dev.optiboost;
    exports org.dev.optiboost.Controller;
    opens org.dev.optiboost.Controller to javafx.fxml;
}
