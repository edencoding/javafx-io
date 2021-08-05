package com.edencoding.controllers;

import com.edencoding.models.domain.ImageInterpretationModel;
import com.edencoding.models.openVision.BoundingBox;
import com.edencoding.models.openVision.Prediction;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;

public class MainViewController {

    //layout defaults
    private static final int MAX_IMAGE_WIDTH = 360;
    private static final int MAX_IMAGE_HEIGHT = 240;

    //View nodes
    @FXML
    private ImageView imageDisplayNode;
    @FXML
    private Pane overlayPane;
    @FXML
    private ListView<Prediction> predictionsListView;
    @FXML
    private Label progressLabel;
    @FXML
    private ProgressBar progressBar;

    //Model
    private ImageInterpretationModel model;

    private void createModel() {
        model = new ImageInterpretationModel();
    }

    private void sizeAndPositionPanes() {
        imageDisplayNode.setFitWidth(MAX_IMAGE_WIDTH);
        imageDisplayNode.setFitHeight(MAX_IMAGE_HEIGHT);

        imageDisplayNode.imageProperty().addListener((observable, oldImage, newImage) -> {
            double aspectRatio = newImage.getWidth() / newImage.getHeight();

            if (aspectRatio > 1.5) {
                imageDisplayNode.setFitWidth(MAX_IMAGE_WIDTH);
                imageDisplayNode.setFitHeight(MAX_IMAGE_WIDTH / aspectRatio);
            } else {
                imageDisplayNode.setFitHeight(MAX_IMAGE_HEIGHT);
                imageDisplayNode.setFitWidth(MAX_IMAGE_HEIGHT * aspectRatio);
            }
        });

        overlayPane.prefWidthProperty().bind(imageDisplayNode.fitWidthProperty());
        overlayPane.prefHeightProperty().bind(imageDisplayNode.fitHeightProperty());
    }

    public void initialize() {
        createModel();
        sizeAndPositionPanes();
        bindImageToModelImage();
        bindProgressBarAndProgressLabel();
        addOverlayPaneListeners();
        bindListViewToPredictions();
    }

    private void bindImageToModelImage() {
        imageDisplayNode.imageProperty().bind(model.loadedImageProperty());
    }

    private void bindProgressBarAndProgressLabel() {
        progressBar.progressProperty().bind(model.progressProperty());
        progressLabel.textProperty().bind(model.statusTextProperty());
    }

    private void addOverlayPaneListeners() {
        model.predictions().addListener((ListChangeListener<Prediction>) c -> {
            clearHighlights();
            createHighlights(model.predictions());
        });
    }

    private void bindListViewToPredictions() {
        predictionsListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Prediction> call(ListView<Prediction> param) {
                return new ListCell<>() {
                    @Override
                    public void updateItem(Prediction item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null || item.getLabel() == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item.getLabel());
                            setStyle("-fx-border-color: " + formatColorToHex(item.getHighlightColor()) + ";" +
                                    " -fx-border-width: 4px;" +
                                    " -fx-border-radius: 10px;" +
                                    " -fx-padding: 2px;" +
                                    " -fx-alignment: center;");
                        }
                    }
                };

            }
        });
        predictionsListView.setItems(model.predictions());

    }

    private void clearHighlights(){
        overlayPane.getChildren().clear();
    }

    private void createHighlights(ObservableList<Prediction> predictions) {
        Double imagePixelWidth = model.getLoadedImage().getWidth();
        Double imageRealWidth = imageDisplayNode.getFitWidth();
        Double imagePixelHeight = model.getLoadedImage().getHeight();
        Double imageRealHeight = imageDisplayNode.getFitHeight();
        double scalingFactor = Math.min(
                imageRealWidth / imagePixelWidth,
                imageRealHeight / imagePixelHeight);

        for (Prediction prediction : predictions) {
            BoundingBox boundingBox = prediction.getBbox();
            Rectangle rectangle = new Rectangle(
                    boundingBox.getX1() * scalingFactor,
                    boundingBox.getY1() * scalingFactor,
                    boundingBox.getWidth() * scalingFactor,
                    boundingBox.getHeight() * scalingFactor);
            rectangle.setFill(thirtyPercentTransparent(prediction.getHighlightColor()));
            rectangle.setStroke(prediction.getHighlightColor());
            rectangle.setStrokeWidth(3);
            overlayPane.getChildren().add(rectangle);
        }
    }

    private String formatColorToHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private Color thirtyPercentTransparent(Color color) {
        return Color.hsb(
                color.getHue(),
                color.getSaturation(),
                color.getBrightness(),
                0.3
        );
    }

    public void loadNewImage(ActionEvent event) {
        model.loadNewImage();
        event.consume();
    }
}

