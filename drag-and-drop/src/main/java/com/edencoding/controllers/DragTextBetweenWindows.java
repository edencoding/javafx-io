package com.edencoding.controllers;

import javafx.scene.control.TextArea;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class DragTextBetweenWindows {

    public TextArea textArea;

    public void initialize() {
        textArea.setOnDragDetected(event -> {
            //1. Start the drag and drop
            Dragboard db = textArea.startDragAndDrop(TransferMode.MOVE);

            //2. Push the text area's content to the dragboard
            ClipboardContent content = new ClipboardContent();
            content.putString(textArea.getText());
            db.setContent(content);

            //3. Clear the text area
            textArea.setText("");
        });

        textArea.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.MOVE);
        });

        textArea.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();

            if (db.hasString()) {
                textArea.setText(db.getString());
            }
        });
    }
}