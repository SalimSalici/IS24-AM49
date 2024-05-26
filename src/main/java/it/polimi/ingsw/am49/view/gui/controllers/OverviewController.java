package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualCard;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualDrawable;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.model.actions.DrawCardAction;
import it.polimi.ingsw.am49.model.enumerations.DrawPosition;
import it.polimi.ingsw.am49.model.enumerations.Item;
import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotInGameException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.util.Observer;
import it.polimi.ingsw.am49.util.Pair;
import it.polimi.ingsw.am49.view.gui.PointsCoordinates;
import it.polimi.ingsw.am49.view.gui.SceneTitle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import java.util.stream.Stream;

import static it.polimi.ingsw.am49.model.enumerations.DrawPosition.*;

public class OverviewController extends GuiController implements Observer {
    @FXML
    private GridPane drawableGridpane, playersGridpane, objectivesGridpane, handGridpane, resourcesGridpane, itemsGridpane;
    @FXML
    private AnchorPane playerboardAnchorpane;
    @FXML
    private BoardController playerboardController;
    @FXML
    private Pane pointsPane;

    private VirtualGame game;
    private String myUsername;
    private List<VirtualPlayer> players;
    private VirtualDrawable drawableArea;
    private VirtualPlayer focusedPlayer;
    private List<VirtualCard> visibleHand;
    private Pair<VirtualCard, ImageView> selectedCard;

    @Override
    public void init() {
        this.game = app.getVirtualGame();
        this.myUsername = this.app.getUsername();
        this.players = this.game.getPlayers();
        this.game.addObserver(this);
        this.drawableArea = this.game.getDrawableArea();
        this.focusedPlayer = this.game.getPlayerByUsername(myUsername);
        this.visibleHand = new ArrayList<>(this.game.getPlayerByUsername(myUsername).getHand().stream()
                .map(elem -> new VirtualCard(elem, false))
                .collect(Collectors.toList()));

        loadPlayerBoard();
        drawHand(myUsername);
        drawObjectives();
        drawPlayers();
        drawDecks();
        drawSymbols(myUsername);
        drawPointsBoard();
        drawPointsTokens();
//        VirtualPlayer debugPlayer = new VirtualPlayer("nico", it.polimi.ingsw.am49.model.enumerations.Color.BLUE);

        if (playerboardController != null) {
            playerboardController.init(players, this);
//        playerboardController.init(Stream.of(debugPlayer).toList());
        }
    }

    private void loadPlayerBoard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneTitle.BOARD.getFilePath()));
            AnchorPane playerBoard = loader.load();

            playerboardAnchorpane.getChildren().setAll(playerBoard);

            // Ottieni il controller di board.fxml
            playerboardController = loader.getController();
            playerboardController.setGui(this.app, this.manager);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        drawHand(myUsername);
        drawCurrentplayerindicator();
        drawDecks();
        drawSymbols(myUsername);
        drawPointsBoard();
        drawPointsTokens();
        updateVisibleHand();
    }

    private void drawObjectives(){
        int index = 0;
        for(int cardId : this.game.getCommonObjectives()){
            ImageView cardImageview = new ImageView();
            cardImageview.setImage(getImageByCardId(cardId, false));
            cardImageview.setFitWidth(143);
            cardImageview.setFitHeight(88);

            objectivesGridpane.add(cardImageview, index, 0);
            index++;
        }

        ImageView cardImageview = new ImageView();
        cardImageview.setImage(getImageByCardId(this.game.getPlayerByUsername(myUsername).getPersonalObjectiveId(), false));
        cardImageview.setFitWidth(143);
        cardImageview.setFitHeight(88);
        objectivesGridpane.add(cardImageview, index, 0);
        index++;
    }

    private void drawDecks(){
        drawableGridpane.getChildren().clear();
        //TODO: IMPLEMENTA CASO DI MAZZO VUOTO
        ImageView resourceDeckImageview = new ImageView(getImageBackByResource(drawableArea.getDeckTopResource(), false));
        resourceDeckImageview.setOnMouseClicked(mouseEvent -> drawCard(0,RESOURCE_DECK));

        ImageView goldDeckImageview = new ImageView(getImageBackByResource(drawableArea.getDeckTopGold(), true));
        goldDeckImageview.setOnMouseClicked(mouseEvent -> drawCard(0,GOLD_DECK));

        resourceDeckImageview.setFitWidth(143);
        resourceDeckImageview.setFitHeight(88);
        goldDeckImageview.setFitWidth(143);
        goldDeckImageview.setFitHeight(88);

        drawableGridpane.add(resourceDeckImageview, 0, 0);
        drawableGridpane.add(goldDeckImageview, 1, 0);

        // in this code the displaying of the resource and gold cards are managed apart, so that a different number for each type of card can be shown
        int index = 1;
        for(int cardId : drawableArea.getRevealedResourcesIds()){
            ImageView cardImageview = new ImageView();
            cardImageview.setImage(getImageByCardId(cardId, false));
            cardImageview.setFitWidth(143);
            cardImageview.setFitHeight(88);

            cardImageview.setOnMouseClicked(mouseEvent -> drawCard(cardId, REVEALED));

            drawableGridpane.add(cardImageview, 0, index);
            index++;
        }
        index = 1;
        for(int cardId : drawableArea.getRevealedGoldsIds()){
            ImageView cardImageview = new ImageView();
            cardImageview.setImage(getImageByCardId(cardId, false));
            cardImageview.setFitWidth(143);
            cardImageview.setFitHeight(88);

            cardImageview.setOnMouseClicked(mouseEvent -> drawCard(cardId, REVEALED));

            drawableGridpane.add(cardImageview, 1, index);
            index++;
        }
    }

    private void drawPlayers(){
        playersGridpane.getChildren().clear();
        int index = 0;
        for (VirtualPlayer player : this.players) {
            Button viewboardButton = new Button("View board");
            ImageView totemImageview = new ImageView(getImageByTotemColor(player.getColor()));
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
        drawCurrentplayerindicator();
    }

    private void drawCurrentplayerindicator(){
        int index = 0;
        for (VirtualPlayer player : this.players) {
            ImageView indicatorImmageview = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/am49/images/elements/turnIndicator.png"))));

            indicatorImmageview.setFitWidth(130);
            indicatorImmageview.setFitHeight(83);

            if(this.game.getCurrentPlayer().getUsername().equals(player.getUsername())){ //se player è il current player
                playersGridpane.add(indicatorImmageview, 1, index); //TODO: SOMEHOW THE INDICATOR IS SHOWN ON TOP OF THE TOTEM
                return;
            }
            index++;
        }
    }

    public void drawHand(String username){
       handGridpane.getChildren().clear();
        if(username.equals(myUsername)) {
            int index = 0;

            Image rotationImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/am49/images/elements/rotate_Icon.png")));

            for (VirtualCard card : visibleHand) {
                ImageView cardImageview = new ImageView(getImageByCardId(card.id(), card.flipped()));
                ImageView rotationImageview = new ImageView(rotationImage);
                Button rotationButton = new Button();

                cardImageview.setFitWidth(132);
                cardImageview.setFitHeight(87);
                rotationImageview.setFitWidth(16);
                rotationImageview.setFitHeight(16);
                rotationButton.setPrefSize(24, 24);

                rotationButton.setGraphic(rotationImageview);

                rotationButton.setOnAction(event -> {
                    boolean side = !card.flipped();
                    visibleHand.set(visibleHand.indexOf(card), new VirtualCard(card.id(), side));
                    drawHand(myUsername);
                    selectedCard = null;
                });

                cardImageview.setOnMouseClicked(mouseEvent -> selectCard(cardImageview, card));

                handGridpane.add(cardImageview, 1, index);
                handGridpane.add(rotationButton, 0, index);
                index++;
            }
        }
        else{
            int index = 0;
            List<Pair<Resource, Boolean>> hand = this.game.getPlayerByUsername(username).getHiddenHand();
            for (Pair<Resource, Boolean> pair : hand) {
                ImageView cardImageview = new ImageView(getImageBackByResource(pair.first, pair.second)); //TODO: distingui gold e resource

                cardImageview.setFitWidth(132);
                cardImageview.setFitHeight(87);

                handGridpane.add(cardImageview, 1, index);
                index++;
            }
        }
    }

    /**
     * Method to handle the process of drawing a card frome one of the decks
     * @param cardId
     * @param drawPosition
     */
    private void drawCard(int cardId, DrawPosition drawPosition) {
        try {
            this.app.getServer().executeAction(this.app, new DrawCardAction(this.myUsername, drawPosition, cardId));
            this.update();
        } catch (NotYourTurnException | InvalidActionException e) {
            showErrorPopup(e.getMessage());
        } catch (NotInGameException e) {
            showErrorPopup("It seems like you are not in a game. Please restart the application.");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }


    private void selectCard(ImageView selectedCardImageview, VirtualCard card){
        if (selectedCard != null) {
            selectedCard.second.setOpacity(1);
        }
        selectedCard = new Pair<>(card, selectedCardImageview);
        selectedCard.second.setOpacity(0.6);
        System.out.println("Carta selezionata: " + selectedCard.first);
    }

    private void drawSymbols(String username){
        //populates the resource list with the strings rapresenting the values of the enum
        List<Integer> resourcesCounts = this.focusedPlayer.getActiveSymbols().values().stream()
                .limit(Resource.values().length).toList();

        //populates the items list with the strings rapresenting the values of the enum
        List<Integer> itemsCounts = this.focusedPlayer.getActiveSymbols().values().stream()
                .skip(Resource.values().length)
                .limit(Item.values().length).toList();

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
        for (VirtualPlayer player : this.players) {
            Circle circle = new Circle(14 + this.players.indexOf(player)*4, Color.TRANSPARENT);
            circle.setStroke(player.getJavaFXColor());
            circle.setStrokeWidth(4);
            PointsCoordinates point = PointsCoordinates.fromNumber(player.getPoints());
            circle.setCenterX(point.getX());
            circle.setCenterY(point.getY());
            pointsPane.getChildren().add(circle);
        }
    }


    private void setFocusedPlayer(VirtualPlayer player){
        if(!focusedPlayer.equals(player)) {
            this.focusedPlayer = player;
            this.playerboardController.switchPlayerBoard(focusedPlayer);
            this.drawHand(focusedPlayer.getUsername());
            this.drawSymbols(focusedPlayer.getUsername());
        }
    }

    public VirtualCard getSelectedCard() {
        if(selectedCard == null)
            return null;
        else
            return selectedCard.first;
    }

    public void updateVisibleHand() {
        List<VirtualCard> newVisibleHand = new ArrayList<>(this.game.getPlayerByUsername(myUsername).getHand().stream()
                .map(elem -> new VirtualCard(elem, this.visibleHand.stream().filter(virtualCard -> virtualCard.id() == elem).findFirst().map(VirtualCard::flipped).orElse(false)))
                .collect(Collectors.toList()));

        this.visibleHand = newVisibleHand;

        if(focusedPlayer.getUsername().equals(myUsername))
            drawHand(myUsername);
    }
}
