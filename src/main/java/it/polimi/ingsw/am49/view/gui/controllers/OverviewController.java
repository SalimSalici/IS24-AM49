package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualDrawable;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.model.enumerations.Item;
import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.util.Observer;
import it.polimi.ingsw.am49.view.gui.SceneTitle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class OverviewController extends GuiController implements Observer {
    @FXML
    private GridPane drawableGridpane, playersGridpane, objectivesGridpane, handGridpane, resourcesGridpane, itemsGridpane;
    @FXML
    private AnchorPane playerboardAnchorpane;
    @FXML
    private BoardController playerboardController;

    private VirtualGame game;
    private String myUsername;
    private List<VirtualPlayer> players;
    private VirtualDrawable drawableArea;
    private VirtualPlayer focusedPlayer;

    @Override
    public void init() {
        this.game = app.getVirtualGame();
        this.myUsername = this.app.getUsername();
        this.players = this.game.getPlayers();
        this.game.addObserver(this);
        this.drawableArea = this.game.getDrawableArea();
        this.focusedPlayer = this.game.getPlayerByUsername(myUsername);

        loadPlayerBoard();
        drawHand(myUsername);
        drawObjectives();
        drawPlayers();
        drawDecks();
        drawSymbols(myUsername);

        if (playerboardController != null) {
        playerboardController.init(this.game.getPlayers(), this.game.getPlayerByUsername(myUsername));
        }
    }

    private void loadPlayerBoard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneTitle.BOARD.getFilePath()));
            AnchorPane playerBoard = loader.load();
            playerboardAnchorpane.getChildren().setAll(playerBoard);

            // Ottieni il controller di board.fxml
            playerboardController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {

    }

    private void drawObjectives(){
        int index = 0;
        for(int cardId : this.game.getCommonObjectives()){
            ImageView cardImageview = new ImageView();
            cardImageview.setImage(getImageByCardId(cardId, true));
            cardImageview.setFitWidth(143);
            cardImageview.setFitHeight(88);

            objectivesGridpane.add(cardImageview, index, 0);
            index++;
        }

        ImageView cardImageview = new ImageView();
        cardImageview.setImage(getImageByCardId(this.game.getPlayerByUsername(myUsername).getPersonalObjectiveId(), true));
        cardImageview.setFitWidth(143);
        cardImageview.setFitHeight(88);
        objectivesGridpane.add(cardImageview, index, 0);
        index++;
    }

    private void drawDecks(){
        drawableGridpane.getChildren().clear();
        //TODO: IMPLEMENTA CASO DI MAZZO VUOTO
        ImageView resourceDeckImageview = new ImageView(getImageBackByResource(drawableArea.getDeckTopResource(), false));
        ImageView goldDeckImageview = new ImageView(getImageBackByResource(drawableArea.getDeckTopGold(), true));

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
            cardImageview.setImage(getImageByCardId(cardId, true));
            cardImageview.setFitWidth(143);
            cardImageview.setFitHeight(88);

            drawableGridpane.add(cardImageview, 0, index);
            index++;
        }
        index = 1;
        for(int cardId : drawableArea.getRevealedGoldsIds()){
            ImageView cardImageview = new ImageView();
            cardImageview.setImage(getImageByCardId(cardId, true));
            cardImageview.setFitWidth(143);
            cardImageview.setFitHeight(88);

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

    private void drawHand(String username){
        handGridpane.getChildren().clear();
        if(username.equals(myUsername)) {
            int index = 0;
            List<Integer> hand = this.game.getPlayerByUsername(username).getHand();
            for (int card : hand) {
                ImageView cardImageview = new ImageView(getImageByCardId(card, true));

                cardImageview.setFitWidth(132);
                cardImageview.setFitHeight(87);

                handGridpane.add(cardImageview, 0, index);
                index++;
            }
        }
        else{
            int index = 0;
            List<Resource> hand = this.game.getPlayerByUsername(username).getHiddenHand();
            for (Resource resource : hand) {
                ImageView cardImageview = new ImageView(getImageBackByResource(resource, false)); //TODO: distingui gold e resource

                cardImageview.setFitWidth(132);
                cardImageview.setFitHeight(87);

                handGridpane.add(cardImageview, 0, index);
                index++;
            }
        }
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

    private void setFocusedPlayer(VirtualPlayer player){
        if(!focusedPlayer.equals(player)) {
            this.focusedPlayer = player;
            this.playerboardController.switchPlayerBoard(focusedPlayer);
            this.drawHand(focusedPlayer.getUsername());
            this.drawSymbols(focusedPlayer.getUsername());
        }
    }
}
