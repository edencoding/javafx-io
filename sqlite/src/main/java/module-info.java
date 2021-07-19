module com.edencoding {
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires javafx.controls;
    requires javafx.fxml;

    opens com.edencoding.models to javafx.base;
    opens com.edencoding.controllers to javafx.fxml;
    exports com.edencoding;
}