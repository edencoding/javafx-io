package com.edencoding.models.openVision;

import java.util.List;

public class OpenVisionResponse {
    private final String description;
    private final List<Prediction> predictions;

    public OpenVisionResponse(String description, List<Prediction> predictions) {
        this.description = description;
        this.predictions = predictions;
    }

    public List<Prediction> getPredictions() {
        return predictions;
    }

    @Override
    public String toString() {
        StringBuilder results = new StringBuilder(description + System.lineSeparator());

        for (Prediction prediction : predictions) {
            results.append("   ")
                    .append(prediction.getScore() * 100)
                    .append("% sure it's a ")
                    .append(prediction.getLabel())
                    .append(" at ").append(prediction.getBbox());
        }

        return results.toString();
    }
}
