package com.edencoding.models.domain;

import com.edencoding.models.dogs.DogImages;
import com.edencoding.models.openVision.OpenVision;
import com.edencoding.models.openVision.OpenVisionResponse;
import com.edencoding.models.openVision.Prediction;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageInterpretationModel {

    private final ObjectProperty<Image> loadedImage = new SimpleObjectProperty<>();
    private final ObservableList<Prediction> predictions = FXCollections.observableArrayList();
    private final DoubleProperty progress = new SimpleDoubleProperty(0);
    private final StringProperty statusText = new SimpleStringProperty("-- Progress --");
    private final Service<Void> service = new Service<>() {
        @Override
        protected Task<Void> createTask() {
            return updateTask();
        }
    };

    {
        AtomicInteger fails = new AtomicInteger();
        service.setOnFailed(event -> {
            if (fails.get() <= 3) {
                updateProgressModel("Error - retrying (" + fails + ")", 0.25);
                fails.getAndIncrement();
                service.reset();
                service.start();
            } else {
                updateProgressModel("Fatal Error. Exit", 1);
            }
        });

        service.setOnSucceeded(event -> fails.set(0));
    }

    public Image getLoadedImage() {
        return loadedImage.get();
    }

    private void setLoadedImage(Image loadedImage) {
        this.loadedImage.set(loadedImage);
    }

    public ObjectProperty<Image> loadedImageProperty() {
        return loadedImage;
    }

    public void loadNewImage() {
        if (!service.isRunning()) {
            service.reset();
            service.start();
        }
    }

    private Task<Void> updateTask() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                updateProgressModel("Loading image...", 0.25);

                BufferedImage imageFromDogAPI = DogImages.getImage();
                if (imageFromDogAPI == null) {
                    updateProgressModel("Error loading image", 0);
                    throw new RuntimeException("Error loading image. This is usually due to an issue resolving HTTP connection with the Dog API. It's usually temporary, and re-running the task may yield better results");
                }
                Platform.runLater(() -> updateLoadedImage(imageFromDogAPI));

                updateProgressModel("Saving image...", 0.4);
                File file = writeToFile(imageFromDogAPI);
                if (file == null) {
                    updateProgressModel("Error saving image...", 0);
                    throw new RuntimeException("Error saving image. This may be an IO error. If you're running this program in an environment where you don't have write permissions, the program can't save a temp file to upload to the server.");
                }

                updateProgressModel("Analysing image...", 0.75);
                OpenVisionResponse openVisionResponse = null;
                openVisionResponse = OpenVision.submitImageToAPI(file);
                if (openVisionResponse == null) {
                    updateProgressModel("Error analysing image", 0);
                    throw new RuntimeException("Error analysing image. This is usually due to an issue resolving HTTP connection with the Unirest API. It's usually temporary, and re-running the task may yield better results");
                }

                updateProgressModel("Adding highlights...", 0.95);
                setPredictions(openVisionResponse.getPredictions());
                updateProgressModel("-- Done! --", 1.0);
                return null;
            }
        };

        task.setOnFailed(event -> {
            progress.set(0);
            statusText.set("-- Error --");
        });

        return task;
    }

    private void updateProgressModel(String message, double progress) {
        Platform.runLater(() -> {
            statusText.set(message);
            this.progress.set(progress);
        });
    }

    private void updateLoadedImage(BufferedImage image) {
        setPredictions(Collections.emptyList());
        setLoadedImage(SwingFXUtils.toFXImage(image, null));
    }

    public ObservableList<Prediction> predictions() {
        return this.predictions;
    }

    private void setPredictions(Collection<Prediction> predictions) {
        Platform.runLater(() -> {
            this.predictions.setAll(predictions);
        });
    }

    private File writeToFile(RenderedImage image) {
        File file = new File("downloaded.jpg");
        try {
            ImageIO.write(image, "jpg", file);
            return file;
        } catch (IOException e) {
            return null;
        }
    }

    public StringProperty statusTextProperty() {
        return statusText;
    }

    public DoubleProperty progressProperty() {
        return progress;
    }
}
