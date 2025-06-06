package org.deg.ui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.deg.backend.Backend;
import org.deg.core.Peer;
import org.deg.ui.components.IconButton;
import org.deg.ui.components.ProfilePictureSelector;
import org.deg.ui.components.Toast;
import org.deg.ui.components.ToastMode;

import java.util.Objects;

import static org.deg.backend.UserConfigurations.*;

public class ReceiveView extends VBox {

    private final Label profilePictureContainer = new Label();
    private final TextField nameLabel;
    private final HBox buttonContainer;
    private final HBox horizontalWrapper = new HBox();
    private final ProfilePictureSelector profilePictureSelector = new ProfilePictureSelector();
    private String selectedProfilePicture;
    private final Backend backend;

    public ReceiveView(Backend backend) {
        super(10);
        this.backend = backend;
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER);

        Peer localPeer = backend.getLocalPeer();
        nameLabel = createNameField(backend.isOnline() ? localPeer.name() : USERNAME);
        buttonContainer = createButtonContainer();
        buttonContainer.setVisible(false);

        profilePictureContainer.getStyleClass().add("profile-picture-container");
        profilePictureContainer.setOnMouseClicked(event -> toggleProfileSelector());

        setImage(backend.isOnline() ? localPeer.profilePicName() : PROFILE_PICTURE_NAME);

        profilePictureSelector.onImageSelected(imageName -> {
            setImage(imageName);
            buttonContainer.setVisible(true);
        });

        horizontalWrapper.setAlignment(Pos.CENTER);
        VBox.setVgrow(horizontalWrapper, Priority.ALWAYS);
        HBox.setHgrow(profilePictureSelector, Priority.ALWAYS);

        VBox mainView = new VBox(10,
                new Label("You are visible as:"),
                profilePictureContainer,
                nameLabel,
                new Label("IP: " + (backend.isOnline() ? localPeer.ip() : "-")),
                new Label("Port: " + (backend.isOnline() ? localPeer.fileTransferPort() : "-"))
        );
        mainView.setAlignment(Pos.CENTER);

        horizontalWrapper.getChildren().add(mainView);
        getChildren().addAll(horizontalWrapper, buttonContainer);
    }

    private TextField createNameField(String initialName) {
        TextField field = new TextField(initialName);
        field.setAlignment(Pos.CENTER);
        field.getStyleClass().addAll("hiddenTextfield", "nameLabel");
        field.textProperty().addListener((obs, oldText, newText) -> buttonContainer.setVisible(true));
        field.setOnAction(event -> {
            Parent parent = field.getParent();
            if (parent != null) parent.requestFocus();
        });
        return field;
    }

    private void setImage(String imageName) {
        Image image = new Image(Objects.requireNonNull(getClass().getResource("/profile-pictures/" + imageName)).toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        imageView.setClip(new Circle(100, 100, 100));
        profilePictureContainer.setGraphic(imageView);
        selectedProfilePicture = imageName;
    }

    private void toggleProfileSelector() {
        if (horizontalWrapper.getChildren().contains(profilePictureSelector)) {
            horizontalWrapper.getChildren().remove(profilePictureSelector);
        } else {
            horizontalWrapper.getChildren().add(profilePictureSelector);
        }
    }

    private HBox createButtonContainer() {
        HBox box = new HBox(15);
        box.setAlignment(Pos.CENTER_RIGHT);

        IconButton revertButton = new IconButton("Revert Changes", createIcon("/icons/revert.png"), 20, 20);
        IconButton saveButton = new IconButton("Save", createIcon("/icons/save.png"), 20, 20);

        revertButton.setContentDisplay(ContentDisplay.LEFT);
        saveButton.setContentDisplay(ContentDisplay.LEFT);

        revertButton.setOnAction(e -> onRevert());
        saveButton.setOnAction(e -> onSave());

        box.getChildren().addAll(revertButton, saveButton);
        return box;
    }

    private ImageView createIcon(String path) {
        return new ImageView(new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm()));
    }

    private void onRevert() {
        nameLabel.setText(USERNAME);
        setImage(PROFILE_PICTURE_NAME);
        buttonContainer.setVisible(false);
        horizontalWrapper.getChildren().remove(profilePictureSelector);
        Toast.show((Stage) getScene().getWindow(), "Changes have been reverted", 3000, ToastMode.SUCCESS);
    }

    private void onSave() {
        String name = nameLabel.getText().trim();
        if (!usernameValid(name)) {
            Toast.show((Stage) getScene().getWindow(), "Username is invalid: A valid username can only contain letters, numbers and spaces.", 3000, ToastMode.ERROR);
            return;
        }
        USERNAME = name;
        PROFILE_PICTURE_NAME = selectedProfilePicture;
        saveConfigurations();
        if (backend.isOnline()) {
            backend.setLocalPeer(new Peer(
                    name,
                    backend.getLocalPeer().ip(),
                    backend.getLocalPeer().fileTransferPort(),
                    selectedProfilePicture
            ));
        }
        buttonContainer.setVisible(false);
        horizontalWrapper.getChildren().remove(profilePictureSelector);
        Toast.show((Stage) getScene().getWindow(), "Changes have been saved.", 3000, ToastMode.SUCCESS);
    }

    private boolean usernameValid(String username) {
        return username != null && !username.isBlank() && username.matches("[a-zA-Z][a-zA-Z0-9_äöü ]*");
    }
}
