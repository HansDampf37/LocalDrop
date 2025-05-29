package org.deg.ui.components;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class TextFieldWithName extends VBox {

    public final TextField inputField;

    public TextFieldWithName(String name) {
        inputField = new TextField();
        getChildren().addAll(new Label(name), inputField);
    }

    public String getText() {
        return inputField.getText();
    }
}