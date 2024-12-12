module org.dev.optiboost {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.dev.optiboost to javafx.fxml;
    exports org.dev.optiboost;
}