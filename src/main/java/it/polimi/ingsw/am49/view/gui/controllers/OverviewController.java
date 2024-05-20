package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualDrawable;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.util.Observer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OverviewController extends GuiController implements Observer {
    @FXML
    private GridPane drawableGridpane, playersGridpane, objectivesGridpane, handGridpane, resourcesGridpane, itemsGridpane;

    private VirtualGame game;
    private String myUsername;
    private List<VirtualPlayer> players;
    private VirtualDrawable drawableArea;

    @Override
    public void init() {
        this.game = app.getVirtualGame();
        this.myUsername = this.app.getUsername();
        this.players = this.game.getPlayers();
        this.game.addObserver(this);
        this.drawableArea = this.game.getDrawableArea();

        drawHand(myUsername);
        drawObjectives();
        drawPlayers();
        drawDecks();
    }

    @Override
    public void update() {

    }

    private void drawObjectives(){

    }

    private void drawDecks(){
        //TODO: IMPLEMENTA CASO DI MAZZO VUOTO
        ImageView resourceDeckImageview = new ImageView(getImageBackByResource(drawableArea.getDeckTopResource(), false));
        ImageView goldDeckImageview = new ImageView(getImageBackByResource(drawableArea.getDeckTopGold(), true));

        resourceDeckImageview.setFitWidth(162);
        resourceDeckImageview.setFitHeight(92);

        drawableGridpane.add(resourceDeckImageview, 0, 0);
        drawableGridpane.add(goldDeckImageview, 0, 1);

        // in this code the displaying of the resource and gold cards are managed apart, so that a different number for each type of card can be shown
        ImageView cardImageview = new ImageView();
        int index = 0;
        for(int cardId : drawableArea.getRevealedResourcesIds()){
            cardImageview.setImage(getImageByCardId(cardId, true));
            cardImageview.setFitWidth(143);
            cardImageview.setFitHeight(88);

            drawableGridpane.add(cardImageview, 0, index);
            index++;
        }
        index = 0;
        for(int cardId : drawableArea.getRevealedGoldsIds()){

            cardImageview.setImage(getImageByCardId(cardId, true));
            cardImageview.setFitWidth(143);
            cardImageview.setFitHeight(88);

            drawableGridpane.add(cardImageview, 1, index);
            index++;
        }
    }

    private void drawPlayers(){
        int index = 0;
        for (VirtualPlayer player : this.players) {
            Button viewboardButton = new Button("View board");
            ImageView totemImageview = new ImageView(getImageByTotemColor(player.getColor()));
            Label usernameLabel = new Label(player.getUsername());

            totemImageview.setFitWidth(33);
            totemImageview.setFitHeight(36);

            playersGridpane.add(viewboardButton, 0, index);
            playersGridpane.add(totemImageview, 2, index);
            playersGridpane.add(usernameLabel,3,  index);
            index++;
        }
        System.out.println(myUsername);
        drawCurrentplayerindicator();
    }

    private void drawCurrentplayerindicator(){
        int index = 0;
        for (VirtualPlayer player : this.players) {
            ImageView indicatorImmageview = new ImageView(new Image(getClass().getResourceAsStream("/it/polimi/ingsw/am49/images/elements/turnIndicator.png")));

            indicatorImmageview.setFitWidth(130);
            indicatorImmageview.setFitHeight(83);

            if(this.game.getCurrentPlayer().getUsername().equals(player.getUsername())){ //se player è il current player
                playersGridpane.add(indicatorImmageview, 1, index);
                return;
            }
            index++;
        }
    }

    private void drawHand(String username){
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
            //mostra hidden hand
        }
    }
}
