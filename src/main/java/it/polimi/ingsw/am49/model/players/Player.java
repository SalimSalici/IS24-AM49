package it.polimi.ingsw.am49.model.players;

import it.polimi.ingsw.am49.model.cards.placeables.ColouredCard;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.cards.placeables.GoldCard;
import it.polimi.ingsw.am49.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.enumerations.CornerPosition;

import java.io.Serializable;
import java.util.*;

public class Player implements Serializable {
    private final String username;
    private Color color;
    private ObjectiveCard personalObjective;
    private StarterCard starterCard;
    private int points;
    private final Set<ColouredCard> hand;
    private final int maxCards;
    private boolean isOnline;
    private PlayerBoard board;

    public Player(String username){
        this.username = username;
        this.points = 0;
        this.hand = new HashSet<>();
        this.maxCards = 3;
        this.isOnline = false;
    }

    public void  placeCard(ColouredCard card, int parentRow, int parentCol, CornerPosition corner) throws Exception {
        if(hand.isEmpty()) throw new Exception("You don't have cards to place");
        if (!hand.contains(card)) throw new Exception("You don't have the card you're trying to place");

        // TODO: add method to PlaceableCard to check if the card can be placed (and avoid casting)...
        // TODO: ... and ColouredCard class can be deleted alltogether
        if(card instanceof GoldCard && !card.isFlipped()){
            if(!board.isCardCostMet((GoldCard) card)) throw new Exception("There aren't enough resources to play this car");
        }

        BoardTile newTile = board.placeTile(card, parentRow, parentCol, corner.toRelativePosition());

        points += card.calculatePoints(board, newTile);

        hand.remove(card);
    }

    public void placeCard(ColouredCard card, BoardTile boardTile, CornerPosition corner) throws Exception {
        this.placeCard(card, boardTile.getRow(), boardTile.getCol(), corner);
    }

    public void drawCard(ColouredCard card) throws Exception{
        if(hand.size() >= maxCards) throw new Exception("You have too many cards");

        hand.add(card);
    }

    public ColouredCard getHandCardById(int id) {
        return this.hand.stream().filter(c -> c.getId() == id).findAny().orElse(null);
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

    /**
     * @param commonObjectives common objective of the game
     * @return the number of achieved objectives (one objective card is achieved only once)
     */
    public int calculateFinalPoints(List<ObjectiveCard> commonObjectives){
        int achieved = 0;
        points += personalObjective.calculatePoints(board);
        achieved = points > 0 ? 1 : 0;
        for (ObjectiveCard objectiveCard : commonObjectives) {
            int objPoints = objectiveCard.calculatePoints(board);
            points += objPoints;
            if (objPoints > 0) achieved++;
        }
        return achieved;
    }

    public String getUsername(){
        return username;
    }

    public Color getColor(){
        return color;
    }

    public int getPoints(){
        return points;
    }

    public ObjectiveCard getPersonalObjective(){
        return personalObjective;
    }

    public Set<ColouredCard> getHand() {
        return hand;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public PlayerBoard getBoard() {
        return board;
    }

    @Override
    public String toString() {
        return "Player: " + this.username + "\n" +
                "\tpoints: " + this.points;

    }
}

