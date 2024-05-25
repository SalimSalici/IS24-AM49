package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualCard;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;

public class BoardController extends GuiController {
    @FXML
    private Button upButton, downButton, leftButton, rightButton;
    @FXML
    private Pane containerPane;

    private List<VirtualPlayer> players;
    private Pane innerPane;
    private Pane imagePane;
    private ImageView starterImageview;
    private ImageView selectedImageView;
    private final double cardWidth = 135;
    private final double cardHeight = 82;

    private final double cornerWidth = cardWidth * 0.25;
    private final double cornerHeight = cardHeight * 0.44;
    private final double innerPaneWidth = 650;
    private final double innerPaneHeight = 340;
    private final Random random = new Random();
    private final Map<VirtualPlayer, List<ImageView>> playerBoards = new HashMap<>();
    private VirtualPlayer currentPlayer;

    public void init(List<VirtualPlayer> players, VirtualPlayer myPlayer) {
        this.players = players;

        setupPanes();
        setupButtons();
        setupPlayersStartingCards();
        currentPlayer = myPlayer;

        selectImageView(starterImageview);
        saveCurrentBoardState(currentPlayer);
    }

    private void setupPanes() {
        innerPane = new Pane();
        innerPane.setPrefSize(innerPaneWidth, innerPaneHeight);
        innerPane.setStyle("-fx-border-color: black;");
        innerPane.setClip(new Rectangle(innerPaneWidth, innerPaneHeight));

        imagePane = new Pane();
        innerPane.getChildren().add(imagePane);
        containerPane.getChildren().add(innerPane);

        containerPane.widthProperty().addListener((obs, oldVal, newVal) -> centerInnerPane());
        containerPane.heightProperty().addListener((obs, oldVal, newVal) -> centerInnerPane());
    }

    private void setupButtons() {
        upButton.setOnAction(e -> moveUp());
        downButton.setOnAction(e -> moveDown());
        leftButton.setOnAction(e -> moveLeft());
        rightButton.setOnAction(e -> moveRight());

        Button resetButton = new Button("Reset");
        resetButton.setLayoutX(10);
        resetButton.setLayoutY(50);
        resetButton.setOnAction(e -> resetImageViews());
        containerPane.getChildren().add(resetButton);
    }

    private void setupPlayersStartingCards() {
        for (VirtualPlayer player : players) {
            setupStartingCards(player);
        }
    }

    private void setupStartingCards(VirtualPlayer player) {
        double initialX = (innerPaneWidth - cardWidth) / 2;
        double initialY = (innerPaneHeight - cardHeight) / 2;
        VirtualCard startingCard = new VirtualCard(81 + random.nextInt(6), true);
        StackPane cardPane = createCardPane(initialX, initialY, player.getUsername(), startingCard);
        starterImageview = (ImageView) cardPane.getChildren().get(0);
        imagePane.getChildren().add(cardPane);
        saveCurrentBoardState(player);
    }

    private void saveCurrentBoardState(VirtualPlayer player) {
        List<ImageView> currentBoard = new ArrayList<>();
        for (Node node : imagePane.getChildren()) {
            if (node instanceof StackPane) {
                for (Node child : ((StackPane) node).getChildren()) {
                    if (child instanceof ImageView) {
                        currentBoard.add(cloneImageView((ImageView) child));
                    }
                }
            }
        }
        playerBoards.put(player, currentBoard);
    }

    private void loadBoardState(VirtualPlayer player) {
        List<ImageView> board = playerBoards.get(player);
        imagePane.getChildren().clear();
        if (board != null) {
            for (ImageView imageView : board) {
                imagePane.getChildren().add(createCardPane(imageView.getLayoutX(), imageView.getLayoutY(), imageView.getId(), new VirtualCard((1 + random.nextInt(80)), true)));
            }
        } else {
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

    private StackPane createCardPane(double x, double y, String id, VirtualCard card) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(cardWidth);
        imageView.setFitHeight(cardHeight);
        imageView.setImage(getImageByVirtualCard(card));
        imageView.setId(id);
        imageView.setOnMouseClicked(e -> selectImageView(imageView));

        StackPane cardPane = new StackPane();
        cardPane.setLayoutX(x);
        cardPane.setLayoutY(y);
        cardPane.setPrefSize(cardWidth, cardHeight);
        cardPane.getChildren().add(imageView);

        addCornerButtons(cardPane);
        return cardPane;
    }

    private void addCornerButtons(StackPane cardPane) {
        Button topLeftButton = createCornerButton("TL");
        Button topRightButton = createCornerButton("TR");
        Button bottomLeftButton = createCornerButton("BL");
        Button bottomRightButton = createCornerButton("BR");

        topRightButton.setPrefSize(cornerWidth, cornerHeight);
        topLeftButton.setPrefSize(cornerWidth, cornerHeight);
        bottomRightButton.setPrefSize(cornerWidth, cornerHeight);

        bottomLeftButton.setPrefSize(cornerWidth, cornerHeight);

        StackPane.setAlignment(topLeftButton, Pos.TOP_LEFT);
        StackPane.setAlignment(topRightButton, Pos.TOP_RIGHT);
        StackPane.setAlignment(bottomLeftButton, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(bottomRightButton, Pos.BOTTOM_RIGHT);

        cardPane.getChildren().addAll(topLeftButton, topRightButton, bottomLeftButton, bottomRightButton);

        topLeftButton.setOnAction(e -> handleCornerButtonAction(cardPane, -cardWidth + cornerWidth, -cardHeight + cornerHeight));
        topRightButton.setOnAction(e -> handleCornerButtonAction(cardPane, cardWidth - cornerWidth, -cardHeight + cornerHeight));
        bottomLeftButton.setOnAction(e -> handleCornerButtonAction(cardPane, -cardWidth + cornerWidth, cardHeight - cornerHeight));
        bottomRightButton.setOnAction(e -> handleCornerButtonAction(cardPane, cardWidth - cornerWidth, cardHeight - cornerHeight));

        // used to display the low opacity preview of the card that will be placed when hovering a corner button
        topLeftButton.setOnMouseEntered(e -> handleCornerButtonHover(cardPane, -cardWidth + cornerWidth, -cardHeight + cornerHeight));
        topRightButton.setOnMouseEntered(e -> handleCornerButtonHover(cardPane, cardWidth - cornerWidth, -cardHeight + cornerHeight));
        bottomRightButton.setOnMouseEntered(e -> handleCornerButtonHover(cardPane, cardWidth - cornerWidth, cardHeight - cornerHeight));
        bottomLeftButton.setOnMouseEntered(e -> handleCornerButtonHover(cardPane, -cardWidth + cornerWidth, cardHeight - cornerHeight));

        topLeftButton.setOnMouseExited(e -> clearPreviewImageView());
        topRightButton.setOnMouseExited(e -> clearPreviewImageView());
        bottomRightButton.setOnMouseExited(e -> clearPreviewImageView());
        bottomLeftButton.setOnMouseExited(e -> clearPreviewImageView());


        topLeftButton.setOpacity(0);
        topRightButton.setOpacity(0);
        bottomLeftButton.setOpacity(0);
        bottomRightButton.setOpacity(0);
    }

    private Button createCornerButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(20, 20);
        return button;
    }

    private void handleCornerButtonAction(StackPane cardPane, double offsetX, double offsetY) {
        double newX = cardPane.getLayoutX() + offsetX;
        double newY = cardPane.getLayoutY() + offsetY;
        VirtualCard card = new VirtualCard((1 + random.nextInt(80)), true);
        addImageView(newX, newY, card);
    }

    private void handleCornerButtonHover(StackPane cardPane, double offsetX, double offsetY) {
        double newX = cardPane.getLayoutX() + offsetX;
        double newY = cardPane.getLayoutY() + offsetY;
        VirtualCard card = new VirtualCard((1 + random.nextInt(80)), true);
        previewImageView(newX, newY, card);
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
        imagePane.setLayoutY(imagePane.getLayoutY() - 10);
    }

    private void moveDown() {
        imagePane.setLayoutY(imagePane.getLayoutY() + 10);
    }

    private void moveLeft() {
        imagePane.setLayoutX(imagePane.getLayoutX() - 10);
    }

    private void moveRight() {
        imagePane.setLayoutX(imagePane.getLayoutX() + 10);
    }

    private void addImageView(double x, double y, VirtualCard card) {
        StackPane cardPane = createCardPane(x, y, "NEW", card);
        imagePane.getChildren().add(cardPane);
    }

    private void previewImageView(double x, double y, VirtualCard card) {

        clearPreviewImageView();

        ImageView previewImage = new ImageView();
        previewImage.setFitWidth(cardWidth);
        previewImage.setFitHeight(cardHeight);
        previewImage.setImage(getImageByVirtualCard(card));
        previewImage.setOpacity(0.5); // Imposta l'opacità per l'anteprima
        previewImage.setMouseTransparent(true);

        previewImage.setLayoutX(x);
        previewImage.setLayoutY(y);
        previewImage.setId("preview");

        imagePane.getChildren().add(previewImage);
    }

    private void clearPreviewImageView() {
            imagePane.getChildren().removeIf(node -> "preview".equals(node.getId()));
    }
    private void resetImageViews() {
        double initialX = (innerPaneWidth - cardWidth) / 2;
        double initialY = (innerPaneHeight - cardHeight) / 2;

        double deltaX = starterImageview.getLayoutX() - initialX;
        double deltaY = starterImageview.getLayoutY() - initialY;

        starterImageview.setLayoutX(initialX);
        starterImageview.setLayoutY(initialY);

        for (Node node : imagePane.getChildren()) {
            if (node instanceof StackPane cardPane) {
                for (Node child : cardPane.getChildren()) {
                    if (child instanceof ImageView imageView && !currentPlayer.getUsername().equals(imageView.getId())) {
                        imageView.setLayoutX(imageView.getLayoutX() - deltaX);
                        imageView.setLayoutY(imageView.getLayoutY() - deltaY);
                    }
                }
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
