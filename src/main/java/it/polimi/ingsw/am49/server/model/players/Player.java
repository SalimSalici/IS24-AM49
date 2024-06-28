package it.polimi.ingsw.am49.server.model.players;

import it.polimi.ingsw.am49.common.reconnectioninfo.CompletePlayerInfo;
import it.polimi.ingsw.am49.common.gameupdates.HandUpdate;
import it.polimi.ingsw.am49.common.gameupdates.HiddenHandUpdate;
import it.polimi.ingsw.am49.common.gameupdates.TileInfo;
import it.polimi.ingsw.am49.server.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.server.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.server.model.cards.placeables.ResourceCard;
import it.polimi.ingsw.am49.server.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.common.enumerations.Color;
import it.polimi.ingsw.am49.common.enumerations.CornerPosition;
import it.polimi.ingsw.am49.common.enumerations.Resource;
import it.polimi.ingsw.am49.common.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.common.util.Pair;
import it.polimi.ingsw.am49.server.model.cards.placeables.GoldCard;
import it.polimi.ingsw.am49.server.model.states.ChooseObjectiveState;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class implements the logic for the players.
 */
public class Player implements Serializable {
    /**
     * Username of the player.
     */
    private final String username;

    /**
     * Stores the color chosen by the player.
     */
    private Color color;

    /**
     * The personal objective chosen by the player in the {@link ChooseObjectiveState}.
     */
    private ObjectiveCard personalObjective;

    /**
     * The {@link StarterCard} of the player.
     */
    private StarterCard starterCard;

    /**
     * The points that a player has achieved so far.
     */
    private int points;

    /**
     * Stores the hand of the player.
     */
    private final List<PlaceableCard> hand;

    /**
     * The maximum amount of cards in a player's hand.
     */
    private final int maxCards;

    /**
     * Tracks if a player is online or offline.
     */
    private boolean isOnline;

    /**
     * Stores the board of the player.
     * @see PlayerBoard
     */
    private PlayerBoard board;

    /**
     * Constructor of the Player class.
     * @param username the username of the player
     */
    public Player(String username){
        this.username = username;
        this.points = 0;
        this.hand = new LinkedList<>();
        this.maxCards = 3;
        this.isOnline = true;
    }

    /**
     * Implements the logic to place a card regarding its cost and the availability of cards in the hand.
     * For the logic to check if the card is playable in a specific tile see {@link BoardTile}.
     * @param card represents a {@link PlaceableCard}.
     * @param parentRow the row of the tile on which the card tries to be placed.
     * @param parentCol the col of the tile on which the card tries to be placed.
     * @param corner represents the corner on which the card tries to be placed see {@link CornerPosition}.
     * @throws InvalidActionException signals that there aren't enough cards in the hand, card cost is not met, or tile position is not valid.
     */
    public BoardTile placeCard(PlaceableCard card, int parentRow, int parentCol, CornerPosition corner) throws InvalidActionException {
        if (!hand.contains(card)) throw new InvalidActionException("You don't have the card you're trying to place");

        if(!card.isFlipped()){
            if(!board.isCardCostMet(card)) throw new InvalidActionException("You don't have enough resources to play this card.");
        }

        BoardTile newTile = board.placeTile(card, parentRow, parentCol, corner.toRelativePosition());
        points += card.calculatePoints(board, newTile);
        hand.remove(card);
        return newTile;
    }

    /**
     * Adds the card passed as parameter to the hand.
     * @param card is a {@link GoldCard} or a {@link ResourceCard}.
     * @see PlaceableCard
     * @throws InvalidActionException signals that the hand is full.
     */
    public void drawCard(PlaceableCard card) throws InvalidActionException {
        if(hand.size() >= maxCards) throw new InvalidActionException("You cannot draw a card now. Your hand is full.");
        hand.add(card);
    }

    /**
     * Gets a card from the hand.
     * @param id distinctively identifies every card in the deck.
     * @return the selected desired card as a {@link PlaceableCard}.
     */
    public PlaceableCard getHandCardById(int id) {
        return this.hand.stream().filter(c -> c.getId() == id).findAny().orElse(null);
    }

    /**
     * At the beginning the player chooses a personal objective between two cards.
     * @param personalObjective the objective chosen by the player
     */
    public void setPersonalObjective(ObjectiveCard personalObjective) {
        this.personalObjective = personalObjective;
    }

    /**
     * Setter for the color attribute of the player.
     * @param color the color chosen by the player.
     */
    public void setColor(Color color){this.color = color;}

    /**
     * Setter for the starter card attribute of the player.
     * @param starterCard the starter card chosen by the player.
     */
    public void setStarterCard(StarterCard starterCard) {
        this.starterCard = starterCard;
        this.board = new PlayerBoard(starterCard);
    }

    /**
     * Chooses the starter side of the starter card.
     * @param flipped true if the starter card should be flipped, false otherwise
     */
    public void chooseStarterSide(boolean flipped){
        if (flipped)
            starterCard.setFlipped(true);
        this.board.getStarterTile().updateActiveSymbols();
        this.board.updateAvailableResources();
    }

    /**
     * Calculates the final points of a player summing their current points to the ones obtained
     * with the objective cards (both common objectives and personal objective).
     *
     * @param commonObjectives common objectives of the game
     * @return the number of achieved objectives (one objective card is achieved only once)
     */
    public int calculateFinalPoints(List<ObjectiveCard> commonObjectives){
        int objectivesAchieved = 0;
        int objPoints = personalObjective.calculatePoints(board);
        points += objPoints;
        objectivesAchieved = objPoints > 0 ? 1 : 0;
        for (ObjectiveCard objectiveCard : commonObjectives) {
            objPoints = objectiveCard.calculatePoints(board);
            points += objPoints;
            if (objPoints > 0) objectivesAchieved++;
        }
        return objectivesAchieved;
    }

    /**
     * Getter for the username.
     * @return the username
     */
    public String getUsername(){
        return this.username;
    }

    /**
     * Getter for the color of the player.
     * @return the color
     */
    public Color getColor(){
        return color;
    }

    /**
     * Getter for the player points.
     * @return the points as int
     */
    public int getPoints(){
        return points;
    }

    /**
     * Getter for the personal objective.
     * @return the personal objective
     */
    public ObjectiveCard getPersonalObjective(){
        return personalObjective;
    }

    /**
     * Getter for the hand.
     * @return the hand
     */
    public List<PlaceableCard> getHand() {
        return hand;
    }

    /**
     * Getter for isOnline.
     * @return true if online, false if offline
     */
    public boolean isOnline() {
        return isOnline;
    }

    /**
     * Setter for isOnline.
     * @param online true if the player is online, false otherwise
     */
    public void setIsOnline(boolean online) {
        this.isOnline = online;
    }

    /**
     * Getter for the player's board.
     * @return the board
     */
    public PlayerBoard getBoard() {
        return board;
    }

    @Override
    public String toString() {
        return "Player: " + this.username + "\n" +
                "\tpoints: " + this.points;
    }

    /**
     * Converts player information to CompletePlayerInfo.
     * @param hidden true if the information should be hidden, false otherwise
     * @return the complete player information
     */
    public CompletePlayerInfo toCompletePlayerInfo(boolean hidden) {
        LinkedList<TileInfo> tiles = this.board.getPlacementOrder().stream()
                .map(boardTile ->  new TileInfo(boardTile.getCard().getId(), boardTile.getRow(), boardTile.getCol(), boardTile.getCard().isFlipped()))
                .collect(Collectors.toCollection(java.util.LinkedList::new));

        HandUpdate handUpdate;
        HiddenHandUpdate hiddenHandUpdate;
        LinkedList<Integer> handIds = new LinkedList<>();
        List<Pair<Resource, Boolean>> hiddenHand = new LinkedList<>();

        if (hidden)
            for (PlaceableCard card : this.hand)
                hiddenHand.add(new Pair<>(card.getResource(), card.isGoldCard()));
        else
            for (PlaceableCard card : this.hand)
                handIds.add(card.getId());

        handUpdate = new HandUpdate(this.username, handIds);
        hiddenHandUpdate = new HiddenHandUpdate(this.username, hiddenHand);
        int personalObjectiveId = hidden ? 0 : this.personalObjective.getId();

        return new CompletePlayerInfo(
                hidden,
                username,
                points,
                color,
                personalObjectiveId,
                tiles,
                handUpdate,
                hiddenHandUpdate,
                this.board.getAvailableResources(),
                this.isOnline
        );
    }
}

