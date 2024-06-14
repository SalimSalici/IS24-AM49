package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualBoard;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualCard;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualTile;
import it.polimi.ingsw.am49.model.actions.PlaceCardAction;
import it.polimi.ingsw.am49.model.enumerations.CornerPosition;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotInGameException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.util.Pair;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.lang.invoke.MutableCallSite;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BoardController extends GuiController {

    @FXML
    private Pane containerPane;
    private OverviewController overviewController;
    private List<VirtualPlayer> players;
    private Pane innerPane;
    private Pane imagePane;
    private ImageView starterImageview;
    private ImageView selectedImageView;
    private final double cardWidth = 135;
    private final double cardHeight = 82;
    private final double cornerWidth = cardWidth * 0.25;
    private final double cornerHeight = cardHeight * 0.44;
    private final double innerPaneWidth = 887;
    private final double innerPaneHeight = 340;
    private final double initialX = (innerPaneWidth - cardWidth) / 2;
    private final double initialY = (innerPaneHeight - cardHeight) / 2;
    private final Random random = new Random();
    private final Map<VirtualPlayer, List<ImageView>> playerBoards = new HashMap<>();
    private final List<CardPane> myBoard = new ArrayList<>();
    private final Map<VirtualPlayer, Pair<Double, Double>> playerToBoardCoords = new HashMap<>();
    private Label boardName;
    String myUsername;
    private VirtualPlayer currentPlayer;
    private VirtualPlayer myPlayer;


    public void init(List<VirtualPlayer> players, OverviewController overviewController) {
        this.overviewController = overviewController;
        this.players = players;
        this.myUsername = this.app.getUsername();
        this.myPlayer = players.stream()
                .filter(player -> player.getUsername().equals(myUsername))
                .findFirst()
                .orElse(null);
        this.currentPlayer = myPlayer;

        setupPanes();
        setUpBoardName();
        setupButtons();
        setupStartingCards();
    }

    private void setupPanes() {
        innerPane = new Pane();
        String css = this.getClass().getResource("/it/polimi/ingsw/am49/css/Overview..css").toExternalForm();
        innerPane.setPrefSize(innerPaneWidth, innerPaneHeight);
        //innerPane.setStyle("-fx-background-image: url('../resources/it/polimi/ingsw/am49/images/elements/symbol_background.jpg');");
        innerPane.getStyleClass().add("innerPaneBackground");
        innerPane.getStylesheets().add(css);

        innerPane.applyCss();
        innerPane.setClip(new Rectangle(innerPaneWidth, innerPaneHeight));

        imagePane = new Pane();
        innerPane.getChildren().add(imagePane);
        containerPane.getChildren().add(innerPane);
        centerInnerPane();

        containerPane.widthProperty().addListener((obs, oldVal, newVal) -> centerInnerPane());
        containerPane.heightProperty().addListener((obs, oldVal, newVal) -> centerInnerPane());
    }

    private void setupButtons() {
        Button resetButton = new Button("Reset");
        resetButton.setLayoutX(5);
        resetButton.setLayoutY(20);
        resetButton.setOnAction(e -> {
            imagePane.setLayoutX(0);
            imagePane.setLayoutY(0);
            playerToBoardCoords.get(currentPlayer).first = 0.0;
            playerToBoardCoords.get(currentPlayer).second = 0.0;
        });
        resetButton.applyCss();
        containerPane.getChildren().add(resetButton);
    }

    private void setUpBoardName(){
        Label boardNameInfo = new Label("Now seeing: ");
        boardNameInfo.setLayoutX(90);
        boardNameInfo.setLayoutY(25);

        boardName = new Label();
        boardName.setLayoutX(180);
        boardName.setLayoutY(25);
        boardName.setText(currentPlayer.getUsername());

        containerPane.getChildren().addAll(boardNameInfo, boardName);
    }

    private void setupStartingCards() {
        //sets up the starting card of each player that is not me
        for (VirtualPlayer player : players) {
            if(isNotMe(player)){
                List<ImageView> board = new ArrayList<>();
                VirtualCard startingCard = new VirtualCard(player.getStarterCard().id(), player.getStarterCard().flipped());
                starterImageview = createCardImageView(initialX, initialY, player.getUsername(), startingCard);

                board.add(starterImageview);
                playerBoards.put(player, board);

                imagePane.getChildren().add(starterImageview);
            }
        }
        
        //sets up my starting card
        VirtualCard startingCard = new VirtualCard(myPlayer.getStarterCard().id(), myPlayer.getStarterCard().flipped());
        myBoard.add(createCardPane(initialX, initialY, myUsername, startingCard, 25, 25));
        starterImageview = (ImageView) myBoard.getFirst().stackPane().getChildren().getFirst();
        imagePane.getChildren().add(myBoard.getFirst().stackPane());

        for(VirtualPlayer player : players){
            // sets up the starting coordinates for all the boards
            playerToBoardCoords.put(player, new Pair<>(0.0, 0.0));
        }
    }

    public void updateSpecificBoard(VirtualPlayer player){
        loadBoardFromVirtualBoard(player);
        if(player.equals(currentPlayer))
            drawBoard(currentPlayer);
    }
    
    public void updateBoards(){
        for(VirtualPlayer player : players){
            loadBoardFromVirtualBoard(player);
        }
        if(isNotMe(currentPlayer))
            drawBoard(currentPlayer);
    }

    private void drawBoard(VirtualPlayer player) {
        Platform.runLater(() -> {
            imagePane.getChildren().clear();
            imagePane.setLayoutX(this.playerToBoardCoords.get(player).first);
            imagePane.setLayoutY(this.playerToBoardCoords.get(player).second);

            if(isNotMe(player)) {
                imagePane.getChildren().addAll(playerBoards.get(player));
            } else{
                for(CardPane cardpane : myBoard)
                    imagePane.getChildren().add(cardpane.stackPane());
            }

            boardName.setText(player.getUsername());
        });
    }

    private void loadBoardFromVirtualBoard(VirtualPlayer player){
        if(isNotMe(player)){
            List<ImageView> board = new ArrayList<ImageView>();
            int startingRow = player.getBoard().getStarterTile().getExpandedRow();
            int startingCol = player.getBoard().getStarterTile().getExpandedCol();

            for(VirtualTile tile : player.getBoard().getOrderedTilesList()){
                int offX = startingCol - tile.getExpandedCol() ;
                int offY = startingRow - tile.getExpandedRow() ;

                double posX = initialX - offX * (cardWidth - cornerWidth);
                double posY = initialY - offY * (cardHeight - cornerHeight);
                ImageView tileImageView = createCardImageView(posX, posY, String.valueOf(tile.getCard().id()), tile.getCard());
                tileImageView.setOnMouseClicked((mouseEvent) -> {
                    imagePane.setLayoutX(initialX - posX);
                    imagePane.setLayoutY(initialY - posY);
                    playerToBoardCoords.get(currentPlayer).first = initialX - posX;
                    playerToBoardCoords.get(currentPlayer).second = initialY - posY;
                });
                board.add(tileImageView);
            }

            playerBoards.put(player, board);
        }
    }

    private ImageView createCardImageView(double x, double y, String id, VirtualCard card){
        ImageView imageView = new ImageView();
        imageView.setFitWidth(cardWidth);
        imageView.setFitHeight(cardHeight);
        imageView.setImage(this.guiTextureManager.getCardImageByVirtualCard(card));
        imageView.setId(id);
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);

        return imageView;
    }

    private CardPane createCardPane(double x, double y, String  id, VirtualCard card, int row, int col){
        ImageView imageView = new ImageView();
        imageView.setFitWidth(cardWidth);
        imageView.setFitHeight(cardHeight);
        imageView.setImage(this.guiTextureManager.getCardImageByVirtualCard(card));
        imageView.setId(id);

        StackPane cardStackpane = new StackPane();
        cardStackpane.setLayoutX(x);
        cardStackpane.setLayoutY(y);
        cardStackpane.setPrefSize(cardWidth, cardHeight);
        cardStackpane.getChildren().add(imageView);

        cardStackpane.onMouseClickedProperty().set((mouseEvent) -> {
            imagePane.setLayoutX(initialX - x);
            imagePane.setLayoutY(initialY - y);
            playerToBoardCoords.get(currentPlayer).first = initialX - x;
            playerToBoardCoords.get(currentPlayer).second = initialY - y;
        });

        CardPane cardpane = new CardPane(cardStackpane, card, row, col);
        addCornerButtons(cardpane);

        return cardpane;
    }

    private void addCornerButtons(CardPane cardpane) {
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

        cardpane.stackPane().getChildren().addAll(topLeftButton, topRightButton, bottomLeftButton, bottomRightButton);

        topLeftButton.setOnAction(e -> handleCornerButtonAction(cardpane, -cardWidth + cornerWidth, -cardHeight + cornerHeight, CornerPosition.TOP_LEFT));
        topRightButton.setOnAction(e -> handleCornerButtonAction(cardpane, cardWidth - cornerWidth, -cardHeight + cornerHeight, CornerPosition.TOP_RIGHT));
        bottomLeftButton.setOnAction(e -> handleCornerButtonAction(cardpane, -cardWidth + cornerWidth, cardHeight - cornerHeight, CornerPosition.BOTTOM_LEFT));
        bottomRightButton.setOnAction(e -> handleCornerButtonAction(cardpane, cardWidth - cornerWidth, cardHeight - cornerHeight, CornerPosition.BOTTOM_RIGHT));

        // used to display the low opacity preview of the card that will be placed when hovering a corner button
        topLeftButton.setOnMouseEntered(e -> handleCornerButtonHover(cardpane.stackPane(), -cardWidth + cornerWidth, -cardHeight + cornerHeight));
        topRightButton.setOnMouseEntered(e -> handleCornerButtonHover(cardpane.stackPane(), cardWidth - cornerWidth, -cardHeight + cornerHeight));
        bottomRightButton.setOnMouseEntered(e -> handleCornerButtonHover(cardpane.stackPane(), cardWidth - cornerWidth, cardHeight - cornerHeight));
        bottomLeftButton.setOnMouseEntered(e -> handleCornerButtonHover(cardpane.stackPane(), -cardWidth + cornerWidth, cardHeight - cornerHeight));

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

    private void handleCornerButtonAction(CardPane cardpane, double offsetX, double offsetY, CornerPosition cornerPosition) {
        MyCard myCard = overviewController.getSelectedCard();
        if(myCard != null) {
            VirtualCard card = new VirtualCard(myCard.getId(), myCard.isFlipped());

            double newX = cardpane.stackPane().getLayoutX() + offsetX;
            double newY = cardpane.stackPane().getLayoutY() + offsetY;

            this.manager.executorService.submit( () -> {
                try {
                    this.app.getServer().executeAction(this.app, new PlaceCardAction(this.app.getUsername(), card.id(), cardpane.row(), cardpane.col(), cornerPosition, card.flipped()));
                    Pair<Integer, Integer> newCoords = VirtualBoard.getCoords(cornerPosition.toRelativePosition(), cardpane.row(), cardpane.col());

                    Platform.runLater(() -> {
                        addCardPane(newX, newY, card, String.valueOf(card.id()), newCoords.first, newCoords.second);
                        this.overviewController.unselectCard();
                    });
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException | NotInGameException |
                         NotYourTurnException | RemoteException | InvalidActionException e) {
                    Platform.runLater(() -> showErrorPopup(e.getMessage()));
                }
            });
        }
    }

    private void handleCornerButtonHover(StackPane cardStackpane, double offsetX, double offsetY) {
        MyCard myCard = overviewController.getSelectedCard();
        if(myCard != null) {
            VirtualCard card = new VirtualCard(myCard.getId(), myCard.isFlipped());
            double newX = cardStackpane.getLayoutX() + offsetX;
            double newY = cardStackpane.getLayoutY() + offsetY;

            previewImageView(newX, newY, card);
        }
    }

    private void centerInnerPane() {
        double paneWidth = containerPane.getPrefWidth();
        double paneHeight = containerPane.getPrefHeight();
        double innerWidth = innerPane.getPrefWidth();
        double innerHeight = innerPane.getPrefHeight();
        innerPane.setLayoutX((paneWidth - innerWidth) / 2);
        innerPane.setLayoutY(paneHeight - innerHeight);
    }

    private void addCardPane(double x, double y, VirtualCard card, String id, int row, int col) {
        CardPane cardPane = createCardPane(x, y, "NEW", card, row, col);
        myBoard.add(cardPane);
        imagePane.getChildren().add(cardPane.stackPane());
    }

    private void previewImageView(double x, double y, VirtualCard card) {
        clearPreviewImageView();
        Platform.runLater(() -> {
            ImageView previewImage = new ImageView();
            previewImage.setFitWidth(cardWidth);
            previewImage.setFitHeight(cardHeight);
            previewImage.setImage(this.guiTextureManager.getCardImageByVirtualCard(card));
            previewImage.setOpacity(0.5); // Imposta l'opacità per l'anteprima
            previewImage.setMouseTransparent(true);

            previewImage.setLayoutX(x);
            previewImage.setLayoutY(y);
            previewImage.setId("preview");

            imagePane.getChildren().add(previewImage);
        });
    }

    private void clearPreviewImageView() {
        Platform.runLater(() -> {
            imagePane.getChildren().removeIf(node -> "preview".equals(node.getId()));
        });
    }

    public void switchPlayerBoard(VirtualPlayer player) {
        currentPlayer = player;
        drawBoard(player);
        centerInnerPane();
    }

    private boolean isNotMe(VirtualPlayer player){
        return !player.getUsername().equals(myUsername);
    }

}
