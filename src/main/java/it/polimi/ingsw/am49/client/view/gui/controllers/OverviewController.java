package it.polimi.ingsw.am49.client.view.gui.controllers;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualDrawable;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.common.enumerations.DrawPosition;
import it.polimi.ingsw.am49.common.enumerations.GameStateType;
import it.polimi.ingsw.am49.common.enumerations.Symbol;
import it.polimi.ingsw.am49.common.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.common.exceptions.NotInGameException;
import it.polimi.ingsw.am49.common.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.client.view.gui.PointsCoordinates;
import it.polimi.ingsw.am49.client.view.gui.SceneTitle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.am49.common.enumerations.DrawPosition.*;

public class OverviewController extends GuiController {
    @FXML
    private GridPane drawableGridpane, playersGridpane, objectivesGridpane, handGridpane, resourcesGridpane, itemsGridpane;
    @FXML
    private AnchorPane playerboardAnchorpane, chatframeAnchorpane;
    @FXML
    private BoardController playerboardController;
    @FXML
    private Pane pointsPane;
    @FXML
    private ChatController chatController;
    @FXML
    private Button leaveButton;
    @FXML
    private Label roomnameLabel;

    private VirtualGame game;
    private String myUsername;
    private List<VirtualPlayer> players;
    private VirtualDrawable drawableArea;
    private VirtualPlayer focusedPlayer;
    private List<MyCard> myHand = new ArrayList<>();
    private final Map<VirtualPlayer, List<ImageView>> playersHands = new HashMap<>();
    private final Map<VirtualPlayer, ImageView> playersPersonalObjectives = new HashMap<>();
    private MyCard selectedCard;
    private Image rotationImage;
    private final List<Button> rotationButtonList = new ArrayList<>();
    private boolean endGame = false;
    private boolean finalRoundAlreadyShown = false;
    private Button activeViewButton;

    /**
     * Initializes the controller and sets up the game state and UI components.
     */
    @Override
    public void init() {
        this.game = manager.getVirtualGame();
        this.myUsername = ClientApp.getUsername();
        this.players = this.game.getPlayers();
        this.drawableArea = this.game.getDrawableArea();
        this.focusedPlayer = this.game.getPlayerByUsername(myUsername);
        rotationImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/am49/images/elements/rotate_icon.png")));
        for(VirtualPlayer player : players)
            updateHand(player.getUsername());

        this.endGame = manager.getVirtualGame().getEndGame();

        // draws every element of the scene
        roomnameLabel.setText(this.manager.getRoomInfo().roomName());
        leaveButton.setText("LEAVE");

        loadPlayerBoard();
        loadChat();
        drawRotationButtons();
        rotationButtonList.forEach(button -> button.setVisible(false));
        drawHand(myUsername);
        drawSymbols();
        drawObjectives();
        drawPlayers();
        drawDrawableArea();
        drawPointsBoard();
        drawPointsTokens();

        this.leaveButton.setOnMouseClicked(mouseEvent -> leaveGame());

        if (playerboardController != null) {
            playerboardController.init();
        }
        if (chatController != null){
            chatController.init();
        }

        this.playerboardController.setBoardRound(this.game.getRound());

        // links all the observers
        this.game.addObserver(() -> {
            drawCurrentPlayerIndicator();
            drawPointsTokens();
            if(this.game.getFinalRound() && !finalRoundAlreadyShown){
                Platform.runLater(() -> this.playerboardController.setFinalRound());
                Platform.runLater(this::showFinalRoundPopUp);
            }
            // sets the scene for when the game has ended
            if (this.game.getGameState() == GameStateType.END_GAME) {
                endGame = true;
                this.endGameSettings();
                this.manager.changeScene(SceneTitle.END_GAME, true);
            }
            this.playerboardController.setBoardRound(this.game.getRound());
        });
        this.game.getDrawableArea().addObserver(this::drawDrawableArea);
        for(VirtualPlayer player : this.game.getPlayers()){
            player.getBoard().addObserver(() -> this.playerboardController.updateSpecificBoard(player));
            if(player.getUsername().equals(myUsername)) {
                player.addObserver(() -> {
                    this.updateHand(myUsername);
                    if(this.focusedPlayer.getUsername().equals(myUsername)){
                        this.drawHand(myUsername);
                        this.drawSymbols();
                    }
                });
            }
            else {
                player.addObserver(() -> {
                    this.updateHand(player.getUsername());
                    if (this.focusedPlayer.equals(player)) {
                        this.drawHand(player.getUsername());
                        this.drawSymbols();
                    }
                });
            }
        }
    }

    private void endGameSettings(){
        unselectCard();
        setPersonalObjectives();
        disableButtons();
        playerboardController.clearFinalRound();
        Platform.runLater(() -> {
            leaveButton.setText("RESULTS");
            leaveButton.setOnMouseClicked(actionEvent -> {
                this.manager.changeScene(SceneTitle.END_GAME, false);
            });
        });
    }

    /**
     * Loads the player board UI component.
     */
    private void loadPlayerBoard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneTitle.BOARD.getFilePath()));
            AnchorPane playerBoard = loader.load();

            playerboardAnchorpane.getChildren().setAll(playerBoard);

            // Gets the controller of board.fxml
            playerboardController = loader.getController();
            playerboardController.setGui(this.manager, this.menuController, this.roomController, this.gameController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the chat UI component.
     */
    private void loadChat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneTitle.CHAT.getFilePath()));
            AnchorPane chatFrame = loader.load();

            chatFrame.setLayoutX(7);
            chatFrame.setLayoutY(10);

            chatframeAnchorpane.getChildren().setAll(chatFrame);

            // Gets the controller of board.fxml
            chatController = loader.getController();
            chatController.setGui(this.manager, this.menuController, this.roomController, this.gameController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Draws the objectives on the UI.
     */
    private void drawObjectives(){
        int index = 2;
        for(int cardId : this.game.getCommonObjectives()){
            ImageView cardImageview = new ImageView();
            cardImageview.setImage(this.guiTextureManager.getCardImage(cardId, false));
            setCardStyle(cardImageview);
            objectivesGridpane.add(cardImageview, index, 0);
            index++;
        }

        ImageView cardImageview = new ImageView();
        cardImageview.setImage(this.guiTextureManager.getCardImage(this.game.getPlayerByUsername(myUsername).getPersonalObjectiveId(), false));
        setCardStyle(cardImageview);
        objectivesGridpane.add(cardImageview, 0, 0);
    }

    /**
     * Draws the personal objective on the UI.
     */
    private void drawPersonalObjective(){
        Platform.runLater(()->{
            objectivesGridpane.getChildren().removeLast();
            ImageView personalObImageview = this.playersPersonalObjectives.get(this.focusedPlayer);
            setCardStyle(personalObImageview);
            objectivesGridpane.add(personalObImageview, 0, 0);
        });
    }

    /**
     * Draws the drawable area on the UI.
     */
    private void drawDrawableArea(){
        Platform.runLater(() -> {
            if (!drawableGridpane.getChildren().isEmpty())
                drawableGridpane.getChildren().clear();
        });

        Platform.runLater(() -> {
            if (this.drawableArea.getDeckTopResource() != null){
                ImageView resourceDeckImageview = new ImageView(this.guiTextureManager.getCardBackByResource(drawableArea.getDeckTopResource(), false));
                resourceDeckImageview.getStyleClass().add("clickableImage");
                resourceDeckImageview.setOnMouseClicked(mouseEvent -> drawCard(0,RESOURCE_DECK));
                setCardStyle(resourceDeckImageview);
                drawableGridpane.add(resourceDeckImageview, 0, 0);
            }
        });

        Platform.runLater(() -> {
            if (this.drawableArea.getDeckTopGold() != null){
                ImageView goldDeckImageview = new ImageView(this.guiTextureManager.getCardBackByResource(drawableArea.getDeckTopGold(), true));
                goldDeckImageview.getStyleClass().add("clickableImage");
                goldDeckImageview.setOnMouseClicked(mouseEvent -> drawCard(0,GOLD_DECK));
                this.setCardStyle(goldDeckImageview);
                drawableGridpane.add(goldDeckImageview, 1, 0);
            }
        });

        // in this code the displaying of the resource and gold cards are managed apart, so that a different number for each type of card can be shown
        Platform.runLater(() -> {
            for(int index = 0; index < drawableArea.getRevealedResourcesIds().size(); index++)
                if(this.drawableArea.getRevealedResourcesIds().get(index) != null) {
                    int cardId = this.drawableArea.getRevealedResourcesIds().get(index);
                    ImageView cardImageview = new ImageView();

                    cardImageview.getStyleClass().add("clickableImage");
                    cardImageview.setImage(this.guiTextureManager.getCardImage(cardId, false));
                    setCardStyle(cardImageview);

                    cardImageview.setOnMouseClicked(mouseEvent -> drawCard(cardId, REVEALED));

                    drawableGridpane.add(cardImageview, 0, index + 1);
                }
        });

        Platform.runLater(() -> {
            for(int index = 0; index < drawableArea.getRevealedGoldsIds().size(); index++)
                if(this.drawableArea.getRevealedGoldsIds().get(index) != null) {
                    int cardId = this.drawableArea.getRevealedGoldsIds().get(index);
                    ImageView cardImageview = new ImageView();
                    cardImageview.getStyleClass().add("clickableImage");
                    cardImageview.setImage(this.guiTextureManager.getCardImage(cardId, false));
                    cardImageview.setFitWidth(130);
                    cardImageview.setFitHeight(80);

                    cardImageview.setOnMouseClicked(mouseEvent -> drawCard(cardId, REVEALED));

                    drawableGridpane.add(cardImageview, 1, index + 1);
                    GridPane.setHalignment(cardImageview, HPos.CENTER);
                    GridPane.setValignment(cardImageview, VPos.CENTER);
                }
        });
    }

    /**
     * Draws the players on the UI.
     */
    private void drawPlayers() {
        playersGridpane.getChildren().clear();
        Image offlineTotemImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/am49/images/elements/totems/disabled_totem.png")));
        int index = 0;
        for (VirtualPlayer player : this.players) {
            Button viewboardButton = new Button("VIEW");
            String existingStyle = viewboardButton.getStyle();
            String newBackgroundStyle = "-fx-background-color: transparent;";
            viewboardButton.setStyle(existingStyle + " " + newBackgroundStyle);
            GridPane.setHalignment(viewboardButton, HPos.CENTER);
            Image totemImage = this.guiTextureManager.getImageByTotemColor(player.getColor());
            ImageView totemImageview = player.getPlaying() ?
                    new ImageView(totemImage) :
                    new ImageView(offlineTotemImage);
            Label usernameLabel = new Label(player.getUsername());

            int finalIndex = index;
            player.addObserver(() -> Platform.runLater(() -> {
                playersGridpane.getChildren().removeIf(node -> {Integer columnIndex = GridPane.getColumnIndex(node); Integer rowIndex = GridPane.getRowIndex(node); return columnIndex != null && rowIndex != null && columnIndex == 1 && rowIndex == finalIndex;});
                //TODO: replace black totem with gray one
                totemImageview.setImage(player.getPlaying() ? totemImage : offlineTotemImage);
                totemImageview.setFitWidth(33);
                totemImageview.setFitHeight(36);
            }));
            totemImageview.setFitWidth(33);
            totemImageview.setFitHeight(36);

            if (player.getUsername().equals(myUsername)) {
                usernameLabel.setStyle("-fx-font-weight: bold;");
                setActiveButton(viewboardButton);
            }

            playersGridpane.add(viewboardButton, 0, index);
            playersGridpane.add(totemImageview, 2, index);
            playersGridpane.add(usernameLabel, 3, index);

            viewboardButton.setOnAction(actionEvent -> {
                setFocusedPlayer(player);
                setActiveButton(viewboardButton);
            });

            index++;
        }
        System.out.println(myUsername);
        drawCurrentPlayerIndicator();
    }

    /**
     * Sets the active button for viewing the player board.
     * @param viewboardButton The button to set as active.
     */
    private void setActiveButton(Button viewboardButton) {
        // Reset the style of the previously active button
        if (activeViewButton != null) {
            activeViewButton.setStyle("-fx-background-color: transparent;");
        }

        // Set the new active button and apply the active style
        activeViewButton = viewboardButton;
        activeViewButton.setStyle("-fx-background-color: A39E20; -fx-border-color: #21130b; -fx-border-width: 2; -fx-text-fill: #21130b; -fx-font-family: 'DejaVu Sans Mono'; -fx-font-size: 13px; -fx-background-radius: 10; -fx-border-radius: 10;");
    }

    /**
     * Draws the current player indicator on the UI.
     */
    private void drawCurrentPlayerIndicator() {
        Platform.runLater(this::clearTurnIndicator);
        Platform.runLater(() -> {
            int index = 0;
            ImageView indicatorImageView = new ImageView(this.guiTextureManager.getTurnIndicator());
            indicatorImageView.setFitWidth(130);
            indicatorImageView.setFitHeight(83);

            GridPane.setHalignment(indicatorImageView, HPos.CENTER);
            GridPane.setValignment(indicatorImageView, VPos.CENTER);

            for (VirtualPlayer player : this.players) {
                if (this.game.getCurrentPlayer().getUsername().equals(player.getUsername())) { //if is the turn of player
                    playersGridpane.add(indicatorImageView, 1, index);
                    return;
                }
                index++;
            }
        });
    }

    /**
     * Clears the current player indicator from the UI.
     */
    private void clearTurnIndicator() {
        // only remove nodes that are in the second column of playersGridpane
        playersGridpane.getChildren().removeIf(node -> {Integer columnIndex = GridPane.getColumnIndex(node); return columnIndex != null && columnIndex == 1;});
    }

    /**
     * Draws the rotation buttons on the UI.
     */
    public void drawRotationButtons() {
        Platform.runLater(() -> {
            for (int index = 0; index < this.myHand.size(); index++) {
                ImageView rotationImageview = new ImageView(rotationImage);
                Button rotationButton = new Button();
                GridPane.setHalignment(rotationButton, HPos.CENTER);
                rotationImageview.setFitWidth(16);
                rotationImageview.setFitHeight(16);
                rotationButton.setPrefSize(24, 24);

                rotationButton.setGraphic(rotationImageview);

                this.rotationButtonList.add(rotationButton);
                handGridpane.add(rotationButton, 0, index);
            }
        });
    }

    /**
     * Hides the rotation buttons on the UI.
     */
    public void hideRotationButtons(){
            for(Button button : rotationButtonList)
                button.setVisible(false);
    }

    /**
     * Updates the hand of the specified player.
     * @param username The username of the player whose hand is to be updated.
     */
    public void updateHand(String username){
        Platform.runLater(() -> {
            if(username.equals(myUsername)){
                this.myHand = this.game.getPlayerByUsername(myUsername).getHand().stream()
                        .map(elem -> {
                            MyCard existingCard = getMyCardById(elem);
                            boolean isFlipped = (existingCard != null) ? existingCard.isFlipped() : false;
                            return new MyCard(elem, isFlipped, this);
                        })
                        .collect(Collectors.toList());
            }
            else{
                VirtualPlayer player = this.game.getPlayerByUsername(username);
                this.playersHands.put(player, this.game.getPlayerByUsername(player.getUsername()).getHiddenHand().stream().map((resourceBooleanPair -> {
                    ImageView hiddenCard = new ImageView(this.guiTextureManager.getCardBackByResource(resourceBooleanPair.first, resourceBooleanPair.second));
                    hiddenCard.setFitWidth(132);
                    hiddenCard.setFitHeight(87);
                    return hiddenCard;
                })).toList());
            }
        });
    }

    /**
     * Retrieves the card with the specified ID from the player's hand.
     * @param id The ID of the card to retrieve.
     * @return The card with the specified ID, or null if not found.
     */
    public MyCard getMyCardById(int id){
        for(MyCard card : myHand){
            if(card.getId() == id)
                return card;
        }
        return null;
    }

    /**
     * Draws the hand of the specified player on the UI.
     * @param username The username of the player whose hand is to be drawn.
     */
    public void drawHand(String username){
        Platform.runLater(()->{
            handGridpane.getChildren().removeIf(node -> {Integer columnIndex = GridPane.getColumnIndex(node); return columnIndex != null && columnIndex == 1;});
            hideRotationButtons();
            int index = 0;
            if(username.equals(myUsername)) {
                for (MyCard card : myHand) {
                    handGridpane.add(card.getImageView(), 1, index);

                    // sets the rotation action
                    final int i = index;
                    if (!endGame){
                        this.rotationButtonList.get(i).setVisible(true);
                        this.rotationButtonList.get(i).setOnAction(event -> {
                            if (card.equals(this.selectedCard))
                                unselectCard();
                            card.flip();
                            drawHand(myUsername);
                        });
                    }
                    index++;
                }
            }
            else{
                List<ImageView> hiddenHand = this.playersHands.get(this.game.getPlayerByUsername(username));
                for (ImageView hiddenCard : hiddenHand) {
                    handGridpane.add(hiddenCard, 1, index);
                    index++;
                }
            }
        });
    }

    /**
     * Draws the symbols on the UI.
     */
    private void drawSymbols(){
        Platform.runLater(()->{
            //populates the resource list with the strings rapresenting the values of the enum
            List<Integer> resourcesCounts = this.focusedPlayer.getActiveSymbols().entrySet().stream().filter(symbolIntegerEntry -> symbolIntegerEntry.getKey().toResource() != null).sorted(Map.Entry.comparingByKey(Comparator.comparing(Symbol::toString))).map(Map.Entry::getValue).toList();

            //populates the items list with the strings rapresenting the values of the enum
            List<Integer> itemsCounts = this.focusedPlayer.getActiveSymbols().entrySet().stream().filter(symbolIntegerEntry -> symbolIntegerEntry.getKey().toItem() != null).sorted(Map.Entry.comparingByKey(Comparator.comparing(Symbol::toString))).map(Map.Entry::getValue).toList();

            resourcesGridpane.getChildren().removeIf(node ->
                    GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) == 2 && node instanceof Label);

            itemsGridpane.getChildren().removeIf(node ->
                    GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) == 2 && node instanceof Label);

            int index = 0;
            for(int resourceNumber : resourcesCounts){
                Label numberLabel = new Label(Integer.toString(resourceNumber));
                resourcesGridpane.add(numberLabel, 2, index);
                index++;
            }
            index = 0;
            for(int itemNumber : itemsCounts){
                Label numberLabel = new Label(Integer.toString(itemNumber));
                itemsGridpane.add(numberLabel, 2, index);
                index++;
            }
        });
    }

    /**
     * Draws the points board on the UI.
     */
    private void drawPointsBoard(){
        // Carica l'immagine
        Image pointsBoardImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/am49/images/elements/plateau.png")));
        ImageView pointsBoardImageview = new ImageView(pointsBoardImage);

        // Imposta la larghezza e l'altezza dell'ImageView in base al Pane
        pointsBoardImageview.setFitWidth(pointsPane.getWidth());
        pointsBoardImageview.setFitHeight(pointsPane.getHeight());

        // Aggiungi l'ImageView al Pane
        pointsPane.getChildren().add(pointsBoardImageview);

        // Assicurati che l'ImageView ridimensioni insieme al Pane
        pointsBoardImageview.fitWidthProperty().bind(pointsPane.widthProperty());
        pointsBoardImageview.fitHeightProperty().bind(pointsPane.heightProperty());
    }

    /**
     * Draws the points tokens on the UI.
     */
    private void drawPointsTokens(){
        Platform.runLater(()->{
            pointsPane.getChildren().removeIf(node -> pointsPane.getChildren().indexOf(node) != 0);
            for (VirtualPlayer player : this.players) {
                Circle circle = new Circle(14 + this.players.indexOf(player)*4, Color.TRANSPARENT);
                circle.setStroke(player.getJavaFXColor());
                circle.setStrokeWidth(4);
                PointsCoordinates point = PointsCoordinates.fromNumber(Math.min(player.getPoints(), 29));
                circle.setCenterX(point.getX());
                circle.setCenterY(point.getY());
                pointsPane.getChildren().add(circle);
            }
        });
    }

    /**
     * Method to handle the process of drawing a card from one of the decks.
     * @param cardId The ID of the card to draw.
     * @param drawPosition The position from which to draw the card.
     */
    private void drawCard(int cardId, DrawPosition drawPosition) {
        this.manager.executorService.submit(() -> {
            try {
                gameController.drawCard(drawPosition, cardId);
                unselectCard();
            } catch (NotYourTurnException | InvalidActionException e) {
                Platform.runLater(() -> showErrorPopup(e.getMessage()));
            } catch (NotInGameException e) {
                Platform.runLater(() -> showErrorPopup("It seems like you are not in a game. Please restart the application."));
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Selects the specified card.
     * @param card The card to select.
     */
    public void selectCard(MyCard card){
        unselectCard();
        selectedCard = card;
        selectedCard.getImageView().setOpacity(0.6);
        System.out.println("Carta selezionata: " + selectedCard.getId());
    }

    /**
     * Sets the focused player.
     * @param player The player to set as focused.
     */
    private void setFocusedPlayer(VirtualPlayer player){
        if(!focusedPlayer.equals(player)) {
            this.focusedPlayer = player;
            this.playerboardController.switchPlayerBoard(focusedPlayer);
            this.drawHand(focusedPlayer.getUsername());
            this.drawSymbols();
            if(endGame){
                this.drawPersonalObjective();
            }
        }
    }

    /**
     * Retrieves the selected card.
     * @return The selected card, or null if no card is selected.
     */
    public MyCard getSelectedCard() {
        if(selectedCard == null)
            return null;
        else
            return selectedCard;
    }

    /**
     * Unselects the currently selected card.
     */
    public void unselectCard() {
        if(selectedCard != null){
            selectedCard.getImageView().setOpacity(1);
            this.selectedCard = null;
        }
    }

    /**
     * Sets the personal objectives for all players.
     */
    public void setPersonalObjectives(){
        for(VirtualPlayer player : players){
            try{
                this.playersPersonalObjectives.put(player, new ImageView(this.guiTextureManager.getCardImage(player.getPersonalObjectiveId(), false)));
            }catch (RuntimeException e){
                System.out.println("Error loading the personal objectives of one or more players");
                this.playersPersonalObjectives.put(player, new ImageView(this.guiTextureManager.getObjectiveBack()));
            }
        }
    }

    /**
     * Disables all buttons on the UI.
     */
    public void disableButtons(){
        rotationButtonList.forEach(button -> button.setVisible(false));
        myHand.forEach(card -> {card.getImageView().setOnMouseClicked(event -> {});});
        drawableGridpane.getChildren().stream()
                .filter(node -> node instanceof ImageView)
                .forEach(node -> ((ImageView) node).setOnMouseClicked(event -> {}));
        playerboardController.disableCornerButtons();
    }

    /**
     * Leaves the game.
     */
    public void leaveGame(){
        this.manager.executorService.submit(() -> gameController.leave());
    }

    /**
     * Shows a popup indicating the final round.
     */
    public void showFinalRoundPopUp(){
        System.out.println("debug");
        finalRoundAlreadyShown = true;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Final Round");
        alert.setHeaderText(null);
        alert.setContentText("After the end of the current round there will be one final round!");

        String css = this.getClass().getResource("/it/polimi/ingsw/am49/css/alert.css").toExternalForm();
        alert.getDialogPane().getStylesheets().add(css);
        alert.getDialogPane().getStyleClass().add("alert");

        alert.showAndWait();
    }

    /**
     * Sets the style for the specified card image.
     * @param cardImage The card image to set the style for.
     */
    private void setCardStyle(ImageView cardImage){
        cardImage.setFitWidth(135);
        cardImage.setFitHeight(90);
        GridPane.setHalignment(cardImage, HPos.CENTER);
        GridPane.setValignment(cardImage, VPos.CENTER);
    }

}
