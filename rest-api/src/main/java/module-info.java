module com.edencoding {
    //needed for JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;

    //needed for HTTP
    requires unirest.java;

    //needed for JSON
    requires gson;
    requires java.sql;

    //needed for JavaFX
    opens com.edencoding.controllers to javafx.fxml;

    //needed for JSON
    opens com.edencoding.models.openVision to gson;
    opens com.edencoding.models.dogs to gson;

    exports com.edencoding;
}