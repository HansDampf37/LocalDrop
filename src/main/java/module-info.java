module localdrop {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports org.deg.ui;
    exports org.deg.backend;
    exports org.deg.core;
    exports org.deg.core.callbacks;
    exports org.deg.discovery;
    opens org.deg.ui to javafx.fxml;
}
