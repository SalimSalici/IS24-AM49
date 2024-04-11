package it.polimi.ingsw.am49.model.players;

import it.polimi.ingsw.am49.model.cards.placeables.ColouredCard;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.enumerations.CornerPosition;
import it.polimi.ingsw.am49.model.enumerations.RelativePosition;

import java.util.ArrayList;
import java.util.Arrays;

public class Player {
    private int username;
    private Color color;
    private ObjectiveCard personalObjective;
    private StarterCard starterCard;
    private int points;
    private ColouredCard[] hand;
    private boolean myTurn;
    private boolean isOnline;
    private PlayerBoard board;

    public Player(int username){
        this.username = username;
        this.points = 0;
        this.hand = new ColouredCard[3];
        this.myTurn = false;
        this.isOnline = false;
    }

    public void  placeCard(PlaceableCard card, BoardTile boardTile, CornerPosition corner) throws Exception {
        if(hand.length < 3) throw new Exception("You need to draw more cards");

        ArrayList<Integer> iterationsList = new ArrayList<Integer>();

        //TODO: controllare se una carta oro è piazzabile controllando price

        int parentX = boardTile.getCoords(corner.toRelativePosition()).first;
        int parentY = boardTile.getCoords(corner.toRelativePosition()).second;

        if(!board.isPlaceableTile(parentX, parentY)) throw new Exception("The selected tile is not avaiable");

        board.placeTile(card, parentX, parentY, corner.toRelativePosition());

        points += ((ColouredCard)boardTile.getCard()).calculatePoints(board, boardTile);

        Arrays.stream(hand).toList().remove(card);
    }
    public void drawCard(ColouredCard card) throws Exception{
        if(hand.length >= 3) throw new Exception("You have too many cards");

        Arrays.stream(hand).toList().add(card);
    }

    /**
     * At the beginning the player chose a personal objective between two cards
     * @param personalObjective is the objective chosen by the player
     */
    public void setPersonalObjective(ObjectiveCard personalObjective) {
        this.personalObjective = personalObjective;
    }

    public void setColor(Color color){this.color = color;}

    public void setStarterCard(StarterCard starterCard) {
        this.starterCard = starterCard;
        this.board = new PlayerBoard(starterCard);
    }

    public void chooseStarterSide(boolean flipped){
        if(flipped)
            starterCard.setFlipped(true);
    }

    public int getPoints(){
        return points;
    }
}

