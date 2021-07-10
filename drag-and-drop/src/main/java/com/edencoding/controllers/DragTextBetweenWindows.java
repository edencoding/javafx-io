package com.edencoding.controllers;

import javafx.scene.control.TextArea;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class DragTextBetweenWindows {

    public TextArea textArea;

    public void initialize() {
        textArea.setOnDragDetected(event -> {
            Dragboard db = textArea.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(textArea.getText());
            db.setContent(content);

            //remove rectangle from current parent (prevent duplicate parent exception)
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