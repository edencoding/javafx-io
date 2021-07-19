package com.edencoding;

import com.edencoding.dao.Database;
import com.edencoding.util.Alerts;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SQLiteExampleApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //TODO: the checks here take some time, so I would advise a splash screen
        if (Database.isOK()) {
            Parent root = FXMLLoader.load(getClass().getResource("fxml/sqLiteView.fxml"));
            primaryStage.setTitle("Connecting SQLite to JavaFX");
            primaryStage.setScene(new Scene(root));
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.show();
        } else {
            Alerts.error(
                    "Database error",
                    "Could not load database",
                    "Error loading SQLite database. See log. Quitting..."
            ).showAndWait();
            Platform.exit();
        }
    }
}
