module com.edencoding {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.edencoding.controllers to javafx.fxml, javafx.base;
    exports com.edencoding;
}