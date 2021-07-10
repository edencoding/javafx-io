package com.edencoding.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.util.UUID;

public class DragNodeBetweenWindows {

    public Rectangle rectangle;
    public Pane pane;
    private static final DataFormat rectangleShape = new DataFormat("shape/rectangle");

    public void initialize() {
        makeRectangleDragSource();
        makePaneDragTarget();
        pane.setId(UUID.randomUUID().toString());
    }

    private void makeRectangleDragSource() {
        rectangle.setOnDragDetected(event -> {
            //set dragboard content
            Dragboard db = rectangle.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.put(rectangleShape, DragHelper.storeNode(rectangle));
            db.setContent(content);

            //create image to visually indicate dragging
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            WritableImage image = new WritableImage((int) Math.round(rectangle.getWidth()), (int) Math.round(rectangle.getHeight()));
            ImageView iv = new ImageView(rectangle.snapshot(params, image));
            iv.setScaleX(1.5);
            iv.setScaleY(1.5);
            db.setDragView(iv.snapshot(params, null));

            //remove rectangle from current parent (prevent duplicate parent exception)
            Pane parent = DragHelper.getParent(rectangle);
            if(parent != null){
                parent.getChildren().remove(rectangle);
            } else {
                pane.getChildren().remove(rectangle);
            }
        });
    }

    private void makePaneDragTarget() {
        pane.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.MOVE);
        });




        pane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();

            if (db.hasContent(rectangleShape)) {
                int shapeStorageIndex = (int) db.getContent(rectangleShape);
                Shape draggedShape = (Shape) DragHelper.getNode(shapeStorageIndex);
                pane.getChildren().add(draggedShape);
                DragHelper.removeNode(shapeStorageIndex);
                DragHelper.storeNodeParent(draggedShape, pane);
            } else {
                Timeline errorAnimation = errorAnimation(pane, (Color) pane.getBackground().getFills().get(0).getFill());
                errorAnimation.playFromStart();
            }
        });
    }

    private Timeline errorAnimation(Pane paneToAnimate, Color initialColor) {
        DoubleProperty frac = new SimpleDoubleProperty();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(frac, 0)),
                new KeyFrame(Duration.millis(125), new KeyValue(frac, 1))
        );
        timeline.setCycleCount(5);
        timeline.setOnFinished(event -> {
            setBackgroundColor(paneToAnimate, initialColor);
        });

        frac.addListener((observable, oldValue, newValue) -> {
            setBackgroundColor(paneToAnimate, lerp(initialColor, Color.RED, frac.doubleValue()));
        });
        return timeline;
    }

    private Color lerp(Color init, Color end, double frac) {
        return Color.color(
                (init.getRed() * (1 - frac) + end.getRed() * frac),
                (init.getGreen() * (1 - frac) + end.getGreen() * frac),
                (init.getBlue() * (1 - frac) + end.getBlue() * frac)
        );
    }

    private void setBackgroundColor(Pane pane, Color color) {
        pane.setBackground(
                new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY))
        );

    }

    public void deleteShape(){
        pane.getChildren().remove(rectangle);
    }

}
