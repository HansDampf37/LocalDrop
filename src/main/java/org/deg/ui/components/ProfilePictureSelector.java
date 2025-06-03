package org.deg.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.deg.backend.UserConfigurations;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

public class ProfilePictureSelector extends VBox {

    private static final int IMAGE_SIZE = 50;
    private final TilePane tilePane = new TilePane();
    private StackPane selectedPane = null;
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

    /**
     * Loads all images from the resources/profile-pictures directory and render them in the
     * tilePane.
     * <br>
     * <span style="font-weight: bold">WARNING:</span> In order for these images to be
     * loaded successfully they must appear in the file resource/profile-pictures/index.txt.
     * This somewhat complicated approach was chosen for jlink to work: In order for Jlink to include
     * a resource it cannot be loaded via simple iteration over files in a folder. So instead we explicitly
     * load all files enumerated in index.txt for jlink to actually include them in the packaged application.
     */
    private void loadImages() {
        try (InputStream indexStream = getClass().getResourceAsStream("/profile-pictures/index.txt")) {
            if (indexStream == null) {
                System.err.println("index.txt not found in /profile-pictures.");
                return;
            }

            List<String> imageFilenames = new BufferedReader(new InputStreamReader(indexStream))
                    .lines()
                    .filter(name -> name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg"))
                    .toList();

            for (String filename : imageFilenames) {
                URL imageUrl = getClass().getResource("/profile-pictures/" + filename);
                if (imageUrl == null) {
                    System.err.println("Could not find image: " + filename);
                    continue;
                }

                Image image = new Image(imageUrl.toExternalForm());
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
        pane.getStyleClass().add("selected");
        UserConfigurations.saveConfigurations();
        if (onSelectedCallback != null) onSelectedCallback.accept(imageName);
    }

    public void onImageSelected(Consumer<String> callback) {
        this.onSelectedCallback = callback;
    }
}
