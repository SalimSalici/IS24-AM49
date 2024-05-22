package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualBoard;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualCard;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class BoardController extends GuiController {
    @FXML
    private Button upButton, downButton, leftButton, rightButton;
    @FXML
    private Pane containerPane; // Pane più grande per contenere il riquadro e i pulsanti

    private List<VirtualPlayer> players;

    private Pane innerPane; // Pane più grande come riquadro
    private Pane imagePane; // Pane per contenere le ImageView

    private ImageView starterImageview;
    private ImageView selectedImageView;

    private final double cardWidth = 135;
    private final double cardHeight = 82; // Dimensioni standard delle carte
    private final double innerPaneWidth = 650; // Dimensioni più grandi per innerPane
    private final double innerPaneHeight = 340; // Dimensioni più grandi per innerPane
    private final double overlapOffset = 30; // Offset per sovrapposizione degli angoli
    private final Random random = new Random();
    private final Map<VirtualPlayer, List<ImageView>> playerBoards = new HashMap<>(); //map to save in memory the players' boards
    private VirtualPlayer currentPlayer;

    public void init(List<VirtualPlayer> players, VirtualPlayer myPlayer) {
        this.players = players;

        innerPane = new Pane();
        innerPane.setPrefSize(innerPaneWidth, innerPaneHeight); // Imposta le dimensioni più grandi del riquadro
        innerPane.setStyle("-fx-border-color: black;"); // Aggiunge un bordo nero per visualizzare il riquadro

        // Set the clipping for innerPane
        Rectangle clip = new Rectangle(innerPaneWidth, innerPaneHeight);
        innerPane.setClip(clip);

        // Pane used to contain the imageViews
        imagePane = new Pane();

        // Calcola le coordinate iniziali per centrare il rettangolo in innerPane
        double initialX = (innerPaneWidth - cardWidth) / 2;
        double initialY = (innerPaneHeight - cardHeight) / 2;

        // does the setup of the starting card for each player
        for(VirtualPlayer player : this.players){
            setupStartingCards(player);
        }

        innerPane.getChildren().add(imagePane);

        containerPane.getChildren().add(innerPane); // Aggiunge il riquadro al pane più grande

        // lisetener to dinamically change dimensions when resizing 
        containerPane.widthProperty().addListener((obs, oldVal, newVal) -> centerInnerPane());
        containerPane.heightProperty().addListener((obs, oldVal, newVal) -> centerInnerPane());

        upButton.setOnAction(e -> moveUp());
        downButton.setOnAction(e -> moveDown());
        leftButton.setOnAction(e -> moveLeft());
        rightButton.setOnAction(e -> moveRight());

        // Pulsanti per i quattro angoli della schermata
        Button topLeft = new Button("<TL>");
        Button topRight = new Button("<TR>");
        Button bottomLeft = new Button("<BL>");
        Button bottomRight = new Button("<BR>");

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
        topLeft.setOnAction(e -> addImageViewToSelected(-cardWidth + overlapOffset, -cardHeight + overlapOffset));
        topRight.setOnAction(e -> addImageViewToSelected(cardWidth - overlapOffset, -cardHeight + overlapOffset));
        bottomLeft.setOnAction(e -> addImageViewToSelected(-cardWidth + overlapOffset, cardHeight - overlapOffset));
        bottomRight.setOnAction(e -> addImageViewToSelected(cardWidth - overlapOffset, cardHeight - overlapOffset));

        // Pulsante reset
        Button resetButton = new Button("Reset");
        resetButton.setLayoutX(10);
        resetButton.setLayoutY(50);
        resetButton.setOnAction(e -> resetImageViews());

        containerPane.getChildren().add(resetButton);

        currentPlayer = myPlayer;

        // Seleziona il pannello iniziale
        selectImageView(starterImageview);
        saveCurrentBoardState(currentPlayer);
    }

    private void setupStartingCards(VirtualPlayer player){
        double initialX = (innerPaneWidth - cardWidth) / 2;
        double initialY = (innerPaneHeight - cardHeight) / 2;
        VirtualCard startingCard = new VirtualCard(81 + random.nextInt(6), true);
        starterImageview = createSelectableImageView(initialX, initialY, player.getUsername(), startingCard); // the id is the username of the client
        imagePane.getChildren().add(starterImageview);
        saveCurrentBoardState(player);
    }

    private void saveCurrentBoardState(VirtualPlayer player) {
        List<ImageView> currentBoard = new ArrayList<>();
        for (Node node : imagePane.getChildren()) {
            if (node instanceof ImageView) {
                currentBoard.add(cloneImageView((ImageView) node));
            }
        }
        playerBoards.put(player, currentBoard);
    }

    private void loadBoardState(VirtualPlayer player) {
        List<ImageView> board = playerBoards.get(player);
        imagePane.getChildren().clear();
        if (board != null) {
            imagePane.getChildren().addAll(board);
        } else { // used to load the starting cards
            setupStartingCards(player);
        }
    }

    private ImageView cloneImageView(ImageView original) {
        ImageView clone = new ImageView();
        clone.setLayoutX(original.getLayoutX());
        clone.setLayoutY(original.getLayoutY());
        clone.setFitWidth(original.getFitWidth());
        clone.setFitHeight(original.getFitHeight());
        clone.setImage(original.getImage());
        clone.setId(original.getId());
        clone.setOnMouseClicked(e -> selectImageView(clone));
        return clone;
    }

    private ImageView createSelectableImageView(double x, double y, String id, VirtualCard card) {
        ImageView imageView = new ImageView();
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);
        imageView.setFitWidth(cardWidth);
        imageView.setFitHeight(cardHeight);
        imageView.setImage(getImageByVirtualCard(card));
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
            VirtualCard card = new VirtualCard((1 + random.nextInt(80)), true); // generates a random card
            addImageView(newX, newY, card);
        }
    }

    private void addImageView(double x, double y, VirtualCard card) {
        ImageView newImageView = createSelectableImageView(x, y, "NEW", card);
        imagePane.getChildren().add(newImageView);
    }

    private void resetImageViews() {
        double initialX = (innerPaneWidth - cardWidth) / 2;
        double initialY = (innerPaneHeight - cardHeight) / 2;

        double deltaX = starterImageview.getLayoutX() - initialX;
        double deltaY = starterImageview.getLayoutY() - initialY;

        starterImageview.setLayoutX(initialX);
        starterImageview.setLayoutY(initialY);

        for (Node node : imagePane.getChildren()) {
            if (node instanceof ImageView imageView && ! currentPlayer.getUsername().equals(node.getId())) {
                imageView.setLayoutX(imageView.getLayoutX() - deltaX);
                imageView.setLayoutY(imageView.getLayoutY() - deltaY);
            }
        }

        imagePane.setLayoutX(0);
        imagePane.setLayoutY(0);
    }

    public void switchPlayerBoard(VirtualPlayer player) {
        saveCurrentBoardState(currentPlayer);
        currentPlayer = player;
        centerInnerPane();
        loadBoardState(player);
    }
}
