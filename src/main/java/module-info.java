module org.dev.optiboost {
    requires javafx.controls;
    requires javafx.fxml;
    requires eu.hansolo.fx.charts;
    requires java.management;
    requires jdk.management;
    requires com.github.oshi;

    opens org.dev.optiboost to javafx.fxml;
    exports org.dev.optiboost;
    exports org.dev.optiboost.Controller;
    opens org.dev.optiboost.Controller to javafx.fxml;
}