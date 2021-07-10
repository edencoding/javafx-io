package com.edencoding.controllers;

import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class DragFileIntoJavaFX {

    public TextArea textArea;
    public Pane dropInstructions;

    public void initialize() {
        makeTextAreaDragTarget(textArea);
    }

    private void makeTextAreaDragTarget(Node node) {
        node.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.COPY);
        });

        node.setOnDragExited(event -> {
            dropInstructions.setVisible(false);
        });

        node.setOnDragEntered(event -> {
            dropInstructions.setVisible(true);
        });

        node.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();

            if(event.getDragboard().hasFiles()){
                fileLoaderTask(db.getFiles().get(0)).run();
            }

            dropInstructions.setVisible(false);
        });
    }

    private Task<String> fileLoaderTask(File fileToLoad){
        //Create a task to load the file asynchronously
        Task<String> loadFileTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                BufferedReader reader = new BufferedReader(new FileReader(fileToLoad));

                //Use Files.lines() to calculate total lines - used for progress
                long lineCount;
                try (Stream<String> stream = Files.lines(fileToLoad.toPath())) {
                    lineCount = stream.count();
                }

                //Load in all lines one by one into a StringBuilder separated by "\n" - compatible with TextArea
                String line;
                StringBuilder totalFile = new StringBuilder();
                long linesLoaded = 0;
                while((line = reader.readLine()) != null) {
                    totalFile.append(line);
                    totalFile.append("\n");
                    updateProgress(++linesLoaded, lineCount);
                }

                return totalFile.toString();
            }
        };

        //If successful, update the text area, display a success message and store the loaded file reference
        loadFileTask.setOnSucceeded(workerStateEvent -> {
            try {
                textArea.setText(loadFileTask.get());
            } catch (InterruptedException | ExecutionException e) {
                textArea.setText("Could not load file from:\n " + fileToLoad.getAbsolutePath());
            }
        });

        //If unsuccessful, set text area with error message and status message to failed
        loadFileTask.setOnFailed(workerStateEvent -> {
            textArea.setText("Could not load file from:\n " + fileToLoad.getAbsolutePath());
        });

        return loadFileTask;
    }

}
