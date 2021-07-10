package com.edencoding.controllers;

import javafx.scene.control.TextArea;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;

public class DragFileOutOfJavaFX {

    public TextArea textArea;

    public void initialize() {
        textArea.setOnDragDetected(event -> {
            Dragboard db = textArea.startDragAndDrop(TransferMode.MOVE);

            //save the file to disk
            File tempFile = new File("Output.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                writer.write(textArea.getText());
            } catch (IOException e) {
                textArea.setText("Unable to transfer data to dragboard");
            }

            //instruct the dragboard to move the file when the drag's dropped
            ClipboardContent content = new ClipboardContent();
            content.putFiles(Collections.singletonList(tempFile));
            db.setContent(content);
        });
    }

    private void saveFileToDisk(File fileToSave) {
    }
}
