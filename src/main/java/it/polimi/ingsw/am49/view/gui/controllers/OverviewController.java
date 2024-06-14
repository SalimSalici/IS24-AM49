package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualDrawable;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.model.actions.DrawCardAction;
import it.polimi.ingsw.am49.model.enumerations.*;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotInGameException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.server.exceptions.RoomException;
import it.polimi.ingsw.am49.util.BiMap;
import it.polimi.ingsw.am49.view.gui.PointsCoordinates;
import it.polimi.ingsw.am49.view.gui.SceneTitle;
import it.polimi.ingsw.am49.view.tui.scenes.InvalidSceneException;
import it.polimi.ingsw.am49.view.tui.scenes.SceneType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.am49.model.enumerations.DrawPosition.*;

public class OverviewController extends GuiController {
    @FXML
    private GridPane drawableGridpane, playersGridpane, objectivesGridpane, handGridpane, resourcesGridpane, itemsGridpane;
    @FXML
    private AnchorPane playerboardAnchorpane;

    @FXML
    private BoardController playerboardController;
    @FXML
    private Pane pointsPane;
    @FXML
    private ChatController chatController;
    @FXML
    private Button leaveButton;


    private VirtualGame game;
    private String myUsername;
    private List<VirtualPlayer> players;
    private VirtualDrawable drawableArea;
    private VirtualPlayer focusedPlayer;
    private List<MyCard> myHand = new ArrayList<>();
    private final Map<VirtualPlayer, List<ImageView>> playersHands = new HashMap<>();
    private MyCard selectedCard;
    private Image rotationImage;
    private final List<Button> rotationButtonList = new ArrayList<>();
    private boolean buttonDisabled = false;

    @Override
    public void init() {
        this.game = app.getVirtualGame();
        this.myUsername = this.app.getUsername();
        this.players = this.game.getPlayers();
        this.drawableArea = this.game.getDrawableArea();
        this.focusedPlayer = this.game.getPlayerByUsername(myUsername);
        rotationImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/am49/images/elements/rotate_Icon.png")));
        for(VirtualPlayer player : players)
            updateHand(player.getUsername());

        // draws every element of the scene
        loadPlayerBoard();
        drawRotationButtons();
        drawHand(myUsername);
        drawSymbols(myUsername);
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

        // links all the observers
        this.game.addObserver(() -> {
            drawCurrentPlayerIndicator();
            drawPointsTokens();
            if (this.game.getGameState() == GameStateType.END_GAME) {
                try {
                    disableButtons();
                    this.manager.changeScene(SceneTitle.END_GAME, true);
                } catch (InvalidSceneException e) {
                    showErrorPopup(e.getMessage());
                }
            }
        });
        this.game.getDrawableArea().addObserver(this::drawDrawableArea);
        for(VirtualPlayer player : this.game.getPlayers()){
            player.getBoard().addObserver(() -> this.playerboardController.updateSpecificBoard(player));
            if(player.getUsername().equals(myUsername)) {
                player.addObserver(() -> {
                    this.updateHand(myUsername);
                    if(this.focusedPlayer.getUsername().equals(myUsername)){
                        this.drawHand(myUsername);
                        this.drawSymbols(myUsername);
                    }
                });
            }
            else {
                player.addObserver(() -> {
                    if (this.focusedPlayer.equals(player)) {
                        this.updateHand(player.getUsername());
                        this.drawHand(player.getUsername());
                        this.drawSymbols(player.getUsername());
                    }
                });
            }
        }
    }

    private void loadPlayerBoard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneTitle.BOARD.getFilePath()));
            AnchorPane playerBoard = loader.load();

            playerboardAnchorpane.getChildren().setAll(playerBoard);

            // Gets the controller of board.fxml
            playerboardController = loader.getController();
            playerboardController.setGui(this.app, this.manager);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawObjectives(){
        int index = 0;
        for(int cardId : this.game.getCommonObjectives()){
            ImageView cardImageview = new ImageView();
            cardImageview.setImage(this.guiTextureManager.getCardImage(cardId, false));
            cardImageview.setFitWidth(143);
            cardImageview.setFitHeight(88);

            objectivesGridpane.add(cardImageview, index, 0);
            GridPane.setHalignment(cardImageview, HPos.CENTER);
            GridPane.setValignment(cardImageview, VPos.CENTER);
            index++;
        }

        ImageView cardImageview = new ImageView();
        cardImageview.setImage(this.guiTextureManager.getCardImage(this.game.getPlayerByUsername(myUsername).getPersonalObjectiveId(), false));
        cardImageview.setFitWidth(143);
        cardImageview.setFitHeight(88);
        objectivesGridpane.add(cardImageview, index, 0);
        GridPane.setHalignment(cardImageview, HPos.CENTER);
        GridPane.setValignment(cardImageview, VPos.CENTER);
    }

    private void drawDrawableArea(){
        Platform.runLater(() -> {
            if (!drawableGridpane.getChildren().isEmpty())
                drawableGridpane.getChildren().clear();
            //TODO: IMPLEMENTA CASO DI MAZZO VUOTO
        });

        Platform.runLater(() -> {
            if (this.drawableArea.getDeckTopResource() != null){
                ImageView resourceDeckImageview = new ImageView(this.guiTextureManager.getCardBackByResource(drawableArea.getDeckTopResource(), false));
                resourceDeckImageview.setOnMouseClicked(mouseEvent -> drawCard(0,RESOURCE_DECK));

                resourceDeckImageview.setFitWidth(143);
                resourceDeckImageview.setFitHeight(88);

                drawableGridpane.add(resourceDeckImageview, 0, 0);
                GridPane.setHalignment(resourceDeckImageview, HPos.CENTER);
                GridPane.setValignment(resourceDeckImageview, VPos.CENTER);
            }
        });

        Platform.runLater(() -> {
            if (this.drawableArea.getDeckTopGold() != null){
                ImageView goldDeckImageview = new ImageView(this.guiTextureManager.getCardBackByResource(drawableArea.getDeckTopGold(), true));
                goldDeckImageview.setOnMouseClicked(mouseEvent -> drawCard(0,GOLD_DECK));

                goldDeckImageview.setFitWidth(143);
                goldDeckImageview.setFitHeight(88);

                drawableGridpane.add(goldDeckImageview, 1, 0);
                GridPane.setHalignment(goldDeckImageview, HPos.CENTER);
                GridPane.setValignment(goldDeckImageview, VPos.CENTER);
            }
        });

        // in this code the displaying of the resource and gold cards are managed apart, so that a different number for each type of card can be shown
        Platform.runLater(() -> {
            for(int index = 0; index < drawableArea.getRevealedResourcesIds().size(); index++)
                if(this.drawableArea.getRevealedResourcesIds().get(index) != null) {
                    int cardId = this.drawableArea.getRevealedResourcesIds().get(index);
                    ImageView cardImageview = new ImageView();
                    cardImageview.setImage(this.guiTextureManager.getCardImage(cardId, false));
                    cardImageview.setFitWidth(130);
                    cardImageview.setFitHeight(80);

                    cardImageview.setOnMouseClicked(mouseEvent -> drawCard(cardId, REVEALED));

                    drawableGridpane.add(cardImageview, 0, index + 1);
                    GridPane.setHalignment(cardImageview, HPos.CENTER);
                    GridPane.setValignment(cardImageview, VPos.CENTER);
                }
        });

        Platform.runLater(() -> {
            for(int index = 0; index < drawableArea.getRevealedGoldsIds().size(); index++)
                if(this.drawableArea.getRevealedGoldsIds().get(index) != null) {
                    int cardId = this.drawableArea.getRevealedGoldsIds().get(index);
                    ImageView cardImageview = new ImageView();
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

    private void drawPlayers(){
        playersGridpane.getChildren().clear();
        int index = 0;
        for (VirtualPlayer player : this.players) {
            Button viewboardButton = new Button("VIEW");
            ImageView totemImageview = new ImageView(this.guiTextureManager.getImageByTotemColor(player.getColor()));
            Label usernameLabel = new Label(player.getUsername());

            totemImageview.setFitWidth(33);
            totemImageview.setFitHeight(36);

            if(player.getUsername().equals(myUsername)) usernameLabel.setStyle("-fx-font-weight: bold;");;

            playersGridpane.add(viewboardButton, 0, index);
            playersGridpane.add(totemImageview, 2, index);
            playersGridpane.add(usernameLabel,3,  index);

            viewboardButton.setOnAction(actionEvent ->  setFocusedPlayer(player));

            index++;
        }
        System.out.println(myUsername);
        drawCurrentPlayerIndicator();
    }

    private void drawCurrentPlayerIndicator() {
        Platform.runLater(() -> {
            int index = 0;
            //ImageView indicatorImmageView = new ImageView(this.guiTextureManager.getTurnIndicator()); //TODO: MAKE THIS WORK
            ImageView indicatorImageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/am49/images/elements/turnIndicator.png"))));
            indicatorImageView.setFitWidth(130);
            indicatorImageView.setFitHeight(83);

            GridPane.setHalignment(indicatorImageView, HPos.CENTER); // Allineamento orizzontale
            GridPane.setValignment(indicatorImageView, VPos.CENTER); // Allineamento verticale

            clearTurnIndicator();
            for (VirtualPlayer player : this.players) {
                if (this.game.getCurrentPlayer().getUsername().equals(player.getUsername())) { //if is the turn of player
                    playersGridpane.add(indicatorImageView, 1, index); //TODO: SOMEHOW THE INDICATOR IS SHOWN ON TOP OF THE TOTEM
                    return;
                }
                index++;
            }
        });
    }

    private void clearTurnIndicator() {
        // only remove nodes that are in the second column of playersGridpane
        playersGridpane.getChildren().removeIf(node -> {Integer columnIndex = GridPane.getColumnIndex(node); return columnIndex != null && columnIndex == 1;});
    }

    public void drawRotationButtons() {
        Platform.runLater(() -> {
            for (int index = 0; index < this.myHand.size(); index++) {
                ImageView rotationImageview = new ImageView(rotationImage);
                Button rotationButton = new Button();
                rotationImageview.setFitWidth(16);
                rotationImageview.setFitHeight(16);
                rotationButton.setPrefSize(24, 24);

                rotationButton.setGraphic(rotationImageview);

                this.rotationButtonList.add(rotationButton);
                handGridpane.add(rotationButton, 0, index);
            }
        });
    }

    public void hideRotationButtons(){
            for(Button button : rotationButtonList)
                button.setVisible(false);
    }

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
                if(!player.getUsername().equals(myUsername))
                    this.playersHands.put(player, this.game.getPlayerByUsername(player.getUsername()).getHiddenHand().stream().map((resourceBooleanPair -> {
                        ImageView hiddenCard = new ImageView(this.guiTextureManager.getCardBackByResource(resourceBooleanPair.first, resourceBooleanPair.second));
                        hiddenCard.setFitWidth(132);
                        hiddenCard.setFitHeight(87);
                        return hiddenCard;
                    })).toList());
            }
        });
    }

    public MyCard getMyCardById(int id){
        for(MyCard card : myHand){
            if(card.getId() == id)
                return card;
        }
        return null;
    }

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
                    if (!buttonDisabled){
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

    private void drawSymbols(String username){
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

    private void drawPointsBoard(){
        // Carica l'immagine
        Image pointsBoardImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/am49/images/plateau_score/plateau.png")));
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
     * Method to handle the process of drawing a card frome one of the decks
     * @param cardId
     * @param drawPosition
     */
    private void drawCard(int cardId, DrawPosition drawPosition) {
        this.manager.executorService.submit(() -> {
            try {
                this.app.getServer().executeAction(this.app, new DrawCardAction(this.myUsername, drawPosition, cardId));
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

    public void selectCard(MyCard card){
        unselectCard();
        selectedCard = card;
        selectedCard.getImageView().setOpacity(0.6);
        System.out.println("Carta selezionata: " + selectedCard.getId());
    }

    private void setFocusedPlayer(VirtualPlayer player){
        if(!focusedPlayer.equals(player)) {
            this.focusedPlayer = player;
            this.playerboardController.switchPlayerBoard(focusedPlayer);
            this.drawHand(focusedPlayer.getUsername());
            this.drawSymbols(focusedPlayer.getUsername());
        }
    }

    public MyCard getSelectedCard() {
        if(selectedCard == null)
            return null;
        else
            return selectedCard;
    }

    public void unselectCard() {
        if(selectedCard != null){
            selectedCard.getImageView().setOpacity(1);
            this.selectedCard = null;
        }
    }

    public void disableButtons(){
        this.buttonDisabled = true;

        rotationButtonList.forEach(button -> button.setVisible(false));

        myHand.forEach(card -> {card.getImageView().setOnMouseClicked(event -> {});});

        drawableGridpane.getChildren().stream()
                .filter(node -> node instanceof ImageView)
                .forEach(node -> ((ImageView) node).setOnMouseClicked(event -> {}));

        playerboardController.disableCornerButtons();
    }

    public void leaveGame(){
        this.manager.executorService.submit(() -> {
            try {
                app.getServer().leaveRoom(this.app);
                this.manager.changeScene(SceneTitle.MAIN_MENU, true);
            } catch (RemoteException | RoomException | InvalidSceneException e) {
                Platform.runLater(() -> showErrorPopup(e.getMessage()));
                throw new RuntimeException(e);
            }
        });
    }
}
