package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualBoard;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualCard;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualTile;
import it.polimi.ingsw.am49.model.enumerations.CornerPosition;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotInGameException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.util.Pair;
import it.polimi.ingsw.am49.view.gui.SceneTitle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Controller class for the game board GUI.
 */
public class BoardController extends GuiController {

    @FXML
    private Pane containerPane;
    private OverviewController overviewController;
    private List<VirtualPlayer> players;
    private Pane innerPane, imagePane, borderPane;
    private final double cardWidth = 135;
    private final double cardHeight = 82;
    private final double cornerWidth = cardWidth * 0.25;
    private final double cornerHeight = cardHeight * 0.44;
    private final double innerPaneWidth = 877;
    private final double innerPaneHeight = 330;
    private final double initialX = (innerPaneWidth - cardWidth) / 2;
    private final double initialY = (innerPaneHeight - cardHeight) / 2;
    private final Map<VirtualPlayer, List<ImageView>> playerBoards = new HashMap<>();
    private final List<CardPane> myBoard = new ArrayList<>();
    private final Map<VirtualPlayer, Pair<Double, Double>> playerToBoardCoords = new HashMap<>();
    private Label boardName, boardRound;
    String myUsername;
    private VirtualPlayer currentPlayer;

    /**
     * Initializes the board controller.
     */
    public void init() {
        this.overviewController = (OverviewController) this.manager.getControllerBySceneTitle(SceneTitle.OVERVIEW);
        this.players = this.manager.getVirtualGame().getPlayers();
        this.myUsername = ClientApp.getUsername();
        this.currentPlayer = players.stream()
                .filter(player1 -> player1.getUsername().equals(myUsername))
                .findFirst()
                .orElse(null);

        // sets the starting positions for the boards
        for(VirtualPlayer player : players){
            // sets up the starting coordinates for all the boards
            playerToBoardCoords.put(player, new Pair<>(0.0, 0.0));
        }

        setupPanes();
        setUpBoardInfo();
        setupButtons();
        loadAllBoards();

        drawBoard(this.currentPlayer);
    }

    /**
     * Sets up the panes for the board.
     */
    private void setupPanes() {
        borderPane = new Pane();
        borderPane.setPrefSize(innerPaneWidth + 10, innerPaneHeight + 10);
        String css = this.getClass().getResource("/it/polimi/ingsw/am49/css/Overview..css").toExternalForm();
        borderPane.getStyleClass().add("containerPane");
        borderPane.getStylesheets().add(css);
        borderPane.applyCss();

        innerPane = new Pane();
        innerPane.getStyleClass().add("innerPaneBackground");
        innerPane.getStylesheets().add(css);
        innerPane.applyCss();
        innerPane.setPrefSize(innerPaneWidth, innerPaneHeight);
        innerPane.setClip(new Rectangle(innerPaneWidth, innerPaneHeight));

        imagePane = new Pane();
        innerPane.getChildren().add(imagePane);
        borderPane.getChildren().add(innerPane);
        containerPane.getChildren().add(borderPane);
        centerBorderPane();
        centerInnerPane();

        containerPane.widthProperty().addListener((obs, oldVal, newVal) -> centerBorderPane());
        containerPane.heightProperty().addListener((obs, oldVal, newVal) -> centerBorderPane());
    }

    /**
     * Sets up the buttons for the board.
     */
    private void setupButtons() {
        Button resetButton = new Button("RESET");
        resetButton.setLayoutX(5);
        resetButton.setLayoutY(10);
        resetButton.setOnAction(e -> {
            imagePane.setLayoutX(0);
            imagePane.setLayoutY(0);
            playerToBoardCoords.get(currentPlayer).first = 0.0;
            playerToBoardCoords.get(currentPlayer).second = 0.0;
        });
        resetButton.applyCss();
        containerPane.getChildren().add(resetButton);
    }

    /**
     * Sets up the board information labels.
     */
    private void setUpBoardInfo(){
        Label boardNameInfo = new Label("BOARD OF: ");
        Label boardRoundInfo = new Label("ROUND: ");
        int yDistance = 10;

        boardNameInfo.setLayoutX(85);
        boardNameInfo.setFont(Font.font("DejaVu Sans Mono", FontWeight.NORMAL, 25));
        boardNameInfo.setLayoutY(yDistance);

        boardRoundInfo.setLayoutX(750);
        boardRoundInfo.setFont(Font.font("DejaVu Sans Mono", FontWeight.NORMAL, 25));
        boardRoundInfo.setLayoutY(yDistance);

        boardName = new Label();
        boardName.setLayoutX(230);
        boardName.setFont(Font.font("DejaVu Sans Mono", FontWeight.EXTRA_BOLD, 25));
        boardName.setLayoutY(yDistance);
        boardName.setText(currentPlayer.getUsername());

        boardRound = new Label();
        boardRound.setLayoutX(850);
        boardRound.setFont(Font.font("DejaVu Sans Mono", FontWeight.EXTRA_BOLD, 25));
        boardRound.setLayoutY(yDistance);
        boardRound.setText(Integer.toString(0));

        containerPane.getChildren().addAll(boardNameInfo, boardName, boardRoundInfo, boardRound);
    }

    /**
     * Sets the board round number.
     * @param round the round number to set
     */
    public void setBoardRound(int round) {
        Platform.runLater(() -> this.boardRound.setText(Integer.toString(round)));
    }

    /**
     * Updates the specific board for a player.
     * @param player the player whose board to update
     */
    public void updateSpecificBoard(VirtualPlayer player){
        loadBoardFromVirtualBoard(player);
        if(player.equals(currentPlayer))
            drawBoard(currentPlayer);
    }

    /**
     * Draws the board for a player.
     * @param player the player whose board to draw
     */
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

    /**
     * Loads all boards for all players.
     */
    private void loadAllBoards(){
        for(VirtualPlayer player : players){
            loadBoardFromVirtualBoard(player);
        }
    }

    /**
     * Loads the board from the virtual board for a player.
     * @param player the player whose board to load
     */
    private void loadBoardFromVirtualBoard(VirtualPlayer player){
        //for all the other players (the corners are not clickable)
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
        //for the player running the gui (all the corners must be clickable)
        else {
            int startingRow = player.getBoard().getStarterTile().getExpandedRow();
            int startingCol = player.getBoard().getStarterTile().getExpandedCol();

            for(VirtualTile tile : player.getBoard().getOrderedTilesList()){
                int offX = startingCol - tile.getExpandedCol() ;
                int offY = startingRow - tile.getExpandedRow() ;

                double posX = initialX - offX * (cardWidth - cornerWidth);
                double posY = initialY - offY * (cardHeight - cornerHeight);

                myBoard.add(createCardPane(posX, posY, myUsername, tile.getCard(), tile.getRow(), tile.getCol()));
            }
        }
    }

    /**
     * Creates an ImageView for a card.
     * @param x the x-coordinate of the ImageView
     * @param y the y-coordinate of the ImageView
     * @param id the id of the card
     * @param card the virtual card
     * @return the created ImageView
     */
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

    /**
     * Creates a CardPane for a card.
     * @param x the x-coordinate of the CardPane
     * @param y the y-coordinate of the CardPane
     * @param id the id of the card
     * @param card the virtual card
     * @param row the row of the card
     * @param col the column of the card
     * @return the created CardPane
     */
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

    /**
     * Adds corner buttons to a CardPane.
     * @param cardpane the CardPane to add corner buttons to
     */
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

    /**
     * Creates a corner button.
     * @param text the text to display on the button
     * @return the created button
     */
    private Button createCornerButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(20, 20);
        return button;
    }

    /**
     * Handles the action of a corner button.
     * @param cardpane the CardPane associated with the corner button
     * @param offsetX the x-offset for the new card
     * @param offsetY the y-offset for the new card
     * @param cornerPosition the corner position of the new card
     */
    private void handleCornerButtonAction(CardPane cardpane, double offsetX, double offsetY, CornerPosition cornerPosition) {
        MyCard myCard = overviewController.getSelectedCard();
        if(myCard != null) {
            VirtualCard card = new VirtualCard(myCard.getId(), myCard.isFlipped());

            double newX = cardpane.stackPane().getLayoutX() + offsetX;
            double newY = cardpane.stackPane().getLayoutY() + offsetY;

            this.manager.executorService.submit( () -> {
                try {
                    gameController.placeCard(card.id(), cardpane.row(), cardpane.col(), cornerPosition, card.flipped());
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

    /**
     * Handles the hover action of a corner button.
     * @param cardStackpane the StackPane associated with the corner button
     * @param offsetX the x-offset for the preview card
     * @param offsetY the y-offset for the preview card
     */
    private void handleCornerButtonHover(StackPane cardStackpane, double offsetX, double offsetY) {
        MyCard myCard = overviewController.getSelectedCard();
        if(myCard != null) {
            VirtualCard card = new VirtualCard(myCard.getId(), myCard.isFlipped());
            double newX = cardStackpane.getLayoutX() + offsetX;
            double newY = cardStackpane.getLayoutY() + offsetY;

            previewImageView(newX, newY, card);
        }
    }

    /**
     * Centers the inner pane within the border pane.
     */
    private void centerInnerPane() {
        double paneWidth = borderPane.getPrefWidth();
        double paneHeight = borderPane.getPrefHeight();
        double innerWidth = innerPane.getPrefWidth();
        double innerHeight = innerPane.getPrefHeight();
        innerPane.setLayoutX((paneWidth - innerWidth) / 2);
        innerPane.setLayoutY(5);
    }

    /**
     * Centers the border pane within the container pane.
     */
    private void centerBorderPane() {
        double paneWidth = containerPane.getPrefWidth();
        double paneHeight = containerPane.getPrefHeight();
        double innerWidth = borderPane.getPrefWidth();
        double innerHeight = borderPane.getPrefHeight();
        borderPane.setLayoutX((paneWidth - innerWidth) / 2);
        borderPane.setLayoutY(paneHeight - innerHeight);
    }

    /**
     * Adds a CardPane to the board.
     * @param x the x-coordinate of the CardPane
     * @param y the y-coordinate of the CardPane
     * @param card the virtual card
     * @param id the id of the card
     * @param row the row of the card
     * @param col the column of the card
     */
    private void addCardPane(double x, double y, VirtualCard card, String id, int row, int col) {
        CardPane cardPane = createCardPane(x, y, "NEW", card, row, col);
        myBoard.add(cardPane);
        imagePane.getChildren().add(cardPane.stackPane());
    }

    /**
     * Previews an ImageView for a card.
     * @param x the x-coordinate of the ImageView
     * @param y the y-coordinate of the ImageView
     * @param card the virtual card
     */
    private void previewImageView(double x, double y, VirtualCard card) {
        clearPreviewImageView();
        Platform.runLater(() -> {
            ImageView previewImage = new ImageView();
            previewImage.setFitWidth(cardWidth);
            previewImage.setFitHeight(cardHeight);
            previewImage.setImage(this.guiTextureManager.getCardImageByVirtualCard(card));
            previewImage.setOpacity(0.5); // Set opacity for preview
            previewImage.setMouseTransparent(true);

            previewImage.setLayoutX(x);
            previewImage.setLayoutY(y);
            previewImage.setId("preview");

            imagePane.getChildren().add(previewImage);
        });
    }

    /**
     * Clears the preview ImageView.
     */
    private void clearPreviewImageView() {
        Platform.runLater(() -> {
            imagePane.getChildren().removeIf(node -> "preview".equals(node.getId()));
        });
    }

    /**
     * Switches the board to display a different player's board.
     * @param player the player whose board to display
     */
    public void switchPlayerBoard(VirtualPlayer player) {
        currentPlayer = player;
        drawBoard(player);
        centerBorderPane();
    }

    /**
     * Gets the inner pane.
     * @return the inner pane
     */
    public Pane getInnerPane() {
        return innerPane;
    }

    /**
     * Disables the corner buttons on the board.
     */
    public void disableCornerButtons(){
        myBoard.forEach(cardPane -> {
            cardPane.stackPane().getChildren().stream()
                    .filter(node -> node instanceof Button)
                    .forEach(node -> ((Button) node).setVisible(false));
        });
    }

    /**
     * Checks if the player is not the current player.
     * @param player the player to check
     * @return true if the player is not the current player, false otherwise
     */
    private boolean isNotMe(VirtualPlayer player){
        return !player.getUsername().equals(myUsername);
    }

}
