package com.edencoding.models.openVision;

import javafx.scene.paint.Color;

import java.util.Random;

public class Prediction {

    private static final Random r = new Random();
    private final BoundingBox bbox;
    private final String label;
    private final Double score;
    private String highlightColor = null;

    public Prediction(BoundingBox bbox, String label, Double score) {
        this.bbox = bbox;
        this.label = label;
        this.score = score;
    }

    public BoundingBox getBbox() {
        return bbox;
    }

    public String getLabel() {
        return label;
    }

    public Double getScore() {
        return score;
    }

    private String randomColor() {
        double h = randomInt(0, 360);
        double s = randomInt(0.78, 0.98);
        double l = randomInt(0.78, 0.90);
        return Color.hsb(h, s, l).toString();
    }

    private double randomInt(double min, double max) {
        return Math.floor(Math.random() * (max - min)) + min;
    }

    public Color getHighlightColor() {
        if (highlightColor == null) {
            highlightColor = randomColor();
        }
        return Color.valueOf(highlightColor);
    }
}
