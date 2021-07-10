package com.edencoding.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
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
import javafx.util.Duration;

public class DragShapeBetweenPanes {

    public Pane leftPane;
    public Pane rightPane;
    public Rectangle rectangle;

    public void initialize() {
        makeRectangleDragSource();
        setDragFromTo(leftPane, rightPane);
    }

    private void makeRectangleDragSource() {
        rectangle.setOnDragDetected(event -> {
            Dragboard db = rectangle.startDragAndDrop(TransferMode.COPY_OR_MOVE);

            ClipboardContent content = new ClipboardContent();
            content.putString("rectangle");
            db.setContent(content);

            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            WritableImage image = new WritableImage((int) Math.round(rectangle.getWidth()), (int) Math.round(rectangle.getHeight()));
            ImageView iv = new ImageView(rectangle.snapshot(params, image));
            iv.setRotate(5);
            db.setDragView(iv.snapshot(params, null), 5, 5);
        });
    }

    private void setDragFromTo(Pane from, Pane to) {
        from.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.NONE);
        });

        to.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.COPY);
        });

        to.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();

            if (db.getContent(DataFormat.PLAIN_TEXT) == "rectangle") {
                from.getChildren().remove(rectangle);
                to.getChildren().add(rectangle);
                setDragFromTo(to, from);
            } else {
                Timeline errorAnimation = errorAnimation(to, (Color) to.getBackground().getFills().get(0).getFill());
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
}