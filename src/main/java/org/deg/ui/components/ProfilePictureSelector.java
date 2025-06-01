package org.deg.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.deg.backend.UserConfigurations;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class ProfilePictureSelector extends VBox {

    private static final int IMAGE_SIZE = 50;
    private final TilePane tilePane = new TilePane();
    private StackPane selectedPane = null;
    private String selectedImageName = null;
    private Consumer<String> onSelectedCallback = null;

    public ProfilePictureSelector() {
        setSpacing(10);
        setPadding(new Insets(10));

        tilePane.setHgap(10);
        tilePane.setVgap(10);
        tilePane.setPrefColumns(4);
        tilePane.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(tilePane);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        getChildren().add(scrollPane);
        loadImages();
    }

    private void loadImages() {
        try {
            URL resource = getClass().getResource("/profile-pictures");
            if (resource == null) {
                System.err.println("/profile-pictures resource not found.");
                return;
            }
            Path folderPath = Path.of(resource.toURI());
            List<Path> imagePaths = Files.list(folderPath)
                    .filter(p -> p.toString().endsWith(".png") || p.toString().endsWith(".jpg") || p.toString().endsWith(".jpeg"))
                    .toList();

            for (Path path : imagePaths) {
                String filename = path.getFileName().toString();
                Image image = new Image(path.toUri().toString());
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(IMAGE_SIZE);
                imageView.setFitHeight(IMAGE_SIZE);

                StackPane imageContainer = new StackPane(imageView);
                imageContainer.setPadding(new Insets(0));
                imageContainer.getStyleClass().add("profilePicture");

                imageContainer.setOnMouseClicked(e -> selectImage(imageContainer, filename));

                tilePane.getChildren().add(imageContainer);

                if (filename.equals(UserConfigurations.PROFILE_PICTURE_NAME)) {
                    selectImage(imageContainer, filename);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectImage(StackPane pane, String imageName) {
        if (selectedPane != null) {
            selectedPane.getStyleClass().remove("selected");
        }
        selectedPane = pane;
        selectedImageName = imageName;
        pane.getStyleClass().add("selected");
        UserConfigurations.saveConfigurations();
        if (onSelectedCallback != null) onSelectedCallback.accept(selectedImageName);
    }

    public void onImageSelected(Consumer<String> callback) {
        this.onSelectedCallback = callback;
    }
}
