module localdrop {
    requires javafx.controls;
    requires javafx.fxml;

    exports org.deg.ui;
    opens org.deg.ui to javafx.fxml;
}
