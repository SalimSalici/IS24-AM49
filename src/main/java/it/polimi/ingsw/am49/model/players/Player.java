package it.polimi.ingsw.am49.model.players;

import it.polimi.ingsw.am49.model.cards.placeables.ColouredCard;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.model.enumerations.Color;

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
}
