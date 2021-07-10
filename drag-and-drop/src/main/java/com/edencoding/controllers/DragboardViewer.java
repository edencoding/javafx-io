package com.edencoding.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DragboardViewer {

    public TableView<Data> viewer;
    public TableColumn<Data, String> formatColumn;
    public TableColumn<Data, String> contentColumn;
    public TextArea textArea;

    List<DataFormat> formats = new ArrayList<>();

    public void initialize() {
        formats = Arrays.asList(
                DataFormat.PLAIN_TEXT,
                DataFormat.FILES,
                DataFormat.HTML,
                DataFormat.IMAGE,
                DataFormat.RTF,
                DataFormat.URL);

        createTableViewValueFactories();
        makeViewerDragTarget(viewer);
    }

    private void createTableViewValueFactories() {
        formatColumn.setCellValueFactory(new PropertyValueFactory<>("format"));
        contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
    }

    private void makeViewerDragTarget(Node node) {
        node.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.COPY);
        });

        node.setOnDragDropped(event -> {
            viewer.getItems().clear();

            Dragboard db = event.getDragboard();

            for (DataFormat dataFormat : formats) {
                if(db.hasContent(dataFormat)){

                    viewer.getItems().add(new Data(
                            dataFormat.toString(),
                            db.getContent(dataFormat).toString()));
                }
            }
        });
    }

    public static class Data{
        StringProperty format;
        StringProperty content;

        public String getFormat() {
            return format.get();
        }

        public StringProperty formatProperty() {
            return format;
        }

        public String getContent() {
            return content.get();
        }

        public StringProperty contentProperty() {
            return content;
        }

        public Data(String format, String content) {
            this.format = new SimpleStringProperty(format);
            this.content = new SimpleStringProperty(content);
        }
    }

}
