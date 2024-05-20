package it.polimi.ingsw.am49.view.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.Random;
import java.util.Objects;

public class ProvaController extends GuiController {
    @FXML
    private Button up, down, left, right;

    @FXML
    private Pane containerPane; // Pane più grande per contenere il riquadro e i pulsanti

    private Pane innerPane; // Pane più grande come riquadro
    private Pane imagePane; // Pane per contenere le ImageView

    private ImageView provaImageView;
    private ImageView selectedImageView;

    private final double rectangleWidth = 135;
    private final double rectangleHeight = 82; // Dimensioni standard delle carte
    private final double innerPaneWidth = 600; // Dimensioni più grandi per innerPane
    private final double innerPaneHeight = 600; // Dimensioni più grandi per innerPane
    private final double overlapOffset = 30; // Offset per sovrapposizione degli angoli
    private Random random = new Random();

    @Override
    public void init() {
        innerPane = new Pane();
        innerPane.setPrefSize(innerPaneWidth, innerPaneHeight); // Imposta le dimensioni più grandi del riquadro
        innerPane.setStyle("-fx-border-color: black;"); // Aggiunge un bordo nero per visualizzare il riquadro

        // Imposta il clipping per innerPane
        Rectangle clip = new Rectangle(innerPaneWidth, innerPaneHeight);
        innerPane.setClip(clip);

        // Pane per contenere le ImageView
        imagePane = new Pane();

        // Calcola le coordinate iniziali per centrare il rettangolo in innerPane
        double initialX = (innerPaneWidth - rectangleWidth) / 2;
        double initialY = (innerPaneHeight - rectangleHeight) / 2;

        // Creazione della ImageView con ID carta compreso tra 81 e 86
        int initialCardId = 81 + random.nextInt(6);
        provaImageView = createSelectableImageView(initialX, initialY, rectangleWidth, rectangleHeight, "CIAO", initialCardId);

        // Aggiungi la ImageView a imagePane
        imagePane.getChildren().add(provaImageView);

        innerPane.getChildren().add(imagePane);
        containerPane.getChildren().add(innerPane); // Aggiunge il riquadro al pane più grande

        // Listener per centrare il riquadro quando containerPane cambia dimensioni
        containerPane.widthProperty().addListener((obs, oldVal, newVal) -> centerInnerPane());
        containerPane.heightProperty().addListener((obs, oldVal, newVal) -> centerInnerPane());

        up.setOnAction(e -> moveUp());
        down.setOnAction(e -> moveDown());
        left.setOnAction(e -> moveLeft());
        right.setOnAction(e -> moveRight());

        // Pulsanti per i quattro angoli della schermata
        Button topLeft = new Button("Top Left");
        Button topRight = new Button("Top Right");
        Button bottomLeft = new Button("Bottom Left");
        Button bottomRight = new Button("Bottom Right");

        // Posiziona i pulsanti nei quattro angoli della schermata
        topLeft.setLayoutX(10);
        topLeft.setLayoutY(10);

        topRight.layoutXProperty().bind(containerPane.widthProperty().subtract(topRight.widthProperty()).subtract(10));
        topRight.setLayoutY(10);

        bottomLeft.setLayoutX(10);
        bottomLeft.layoutYProperty().bind(containerPane.heightProperty().subtract(bottomLeft.heightProperty()).subtract(10));

        bottomRight.layoutXProperty().bind(containerPane.widthProperty().subtract(bottomRight.widthProperty()).subtract(10));
        bottomRight.layoutYProperty().bind(containerPane.heightProperty().subtract(bottomRight.heightProperty()).subtract(10));

        containerPane.getChildren().addAll(topLeft, topRight, bottomLeft, bottomRight);

        // Gestione degli eventi dei pulsanti degli angoli
        topLeft.setOnAction(e -> addImageViewToSelected(-135 + overlapOffset, -83 + overlapOffset));
        topRight.setOnAction(e -> addImageViewToSelected(rectangleWidth - overlapOffset, -83 + overlapOffset));
        bottomLeft.setOnAction(e -> addImageViewToSelected(-135 + overlapOffset, rectangleHeight - overlapOffset));
        bottomRight.setOnAction(e -> addImageViewToSelected(rectangleWidth - overlapOffset, rectangleHeight - overlapOffset));

        // Pulsante reset
        Button resetButton = new Button("Reset");
        resetButton.setLayoutX(10);
        resetButton.setLayoutY(50);
        resetButton.setOnAction(e -> resetImageViews());

        containerPane.getChildren().add(resetButton);

        // Seleziona il pannello iniziale
        selectImageView(provaImageView);
    }

    private ImageView createSelectableImageView(double x, double y, double width, double height, String id, int cardId) {
        ImageView imageView = new ImageView();
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setImage(getImageByCardId(cardId, true));
        imageView.setId(id);
        imageView.setOnMouseClicked(e -> selectImageView(imageView));
        return imageView;
    }


    private void selectImageView(ImageView imageView) {
        if (selectedImageView != null) {
            selectedImageView.setStyle(selectedImageView.getStyle().replace("-fx-border-color: blue;", "-fx-border-color: gray;"));
        }
        selectedImageView = imageView;
        selectedImageView.setStyle(selectedImageView.getStyle().replace("-fx-border-color: gray;", "-fx-border-color: blue;"));
    }

    private void centerInnerPane() {
        double paneWidth = containerPane.getWidth();
        double paneHeight = containerPane.getHeight();
        double innerWidth = innerPane.getPrefWidth();
        double innerHeight = innerPane.getPrefHeight();
        innerPane.setLayoutX((paneWidth - innerWidth) / 2);
        innerPane.setLayoutY((paneHeight - innerHeight) / 2);
    }

    private void moveUp() {
        double y = imagePane.getLayoutY();
        imagePane.setLayoutY(y - 10); // Muove l'intero imagePane verso l'alto di 10 unità
    }

    private void moveDown() {
        double y = imagePane.getLayoutY();
        imagePane.setLayoutY(y + 10); // Muove l'intero imagePane verso il basso di 10 unità
    }

    private void moveLeft() {
        double x = imagePane.getLayoutX();
        imagePane.setLayoutX(x - 10); // Muove l'intero imagePane verso sinistra di 10 unità
    }

    private void moveRight() {
        double x = imagePane.getLayoutX();
        imagePane.setLayoutX(x + 10); // Muove l'intero imagePane verso destra di 10 unità
    }

    private void addImageViewToSelected(double offsetX, double offsetY) {
        if (selectedImageView != null) {
            double newX = selectedImageView.getLayoutX() + offsetX;
            double newY = selectedImageView.getLayoutY() + offsetY;
            int cardId = 1 + random.nextInt(80); // ID carta compreso tra 1 e 80
            addImageView(newX, newY, cardId);
        }
    }

    private void addImageView(double x, double y, int cardId) {
        ImageView newImageView = createSelectableImageView(x, y, rectangleWidth, rectangleHeight, "NEW", cardId);
        imagePane.getChildren().add(newImageView);
    }

    private void resetImageViews() {
        double initialX = (innerPaneWidth - rectangleWidth) / 2;
        double initialY = (innerPaneHeight - rectangleHeight) / 2;

        double deltaX = provaImageView.getLayoutX() - initialX;
        double deltaY = provaImageView.getLayoutY() - initialY;

        provaImageView.setLayoutX(initialX);
        provaImageView.setLayoutY(initialY);

        for (Node node : imagePane.getChildren()) {
            if (node instanceof ImageView && !"CIAO".equals(node.getId())) {
                ImageView imageView = (ImageView) node;
                imageView.setLayoutX(imageView.getLayoutX() - deltaX);
                imageView.setLayoutY(imageView.getLayoutY() - deltaY);
            }
        }

        imagePane.setLayoutX(0);
        imagePane.setLayoutY(0);
    }
}
