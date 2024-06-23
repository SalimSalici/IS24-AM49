package it.polimi.ingsw.am49.client.virtualmodel;

import it.polimi.ingsw.am49.controller.CompleteGameInfo;
import it.polimi.ingsw.am49.controller.CompletePlayerInfo;
import it.polimi.ingsw.am49.controller.gameupdates.*;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.util.Log;
import it.polimi.ingsw.am49.util.Observable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a virtual game environment that mirrors the state of an actual game.
 * It manages game state, player information, and game updates.
 */
public class VirtualGame extends Observable {
    private final List<VirtualPlayer> players;
    private int round;
    private int turn;
    private boolean endGame;
    private boolean finalRound;
    private GameStateType gameState;
    private VirtualPlayer currentPlayer;
    private List<Integer> commonObjectives;
    private VirtualDrawable drawableArea;
    private VirtualPlayer forfeitWinner;

    /**
     * Private constructor to prevent external instantiation.
     */
    private VirtualGame() {
        this.players = new ArrayList<>();
    }

    /**
     * Creates a new VirtualGame instance initialized with the provided game start update.
     * @param update The initial game state update.
     * @return A new instance of VirtualGame.
     */
    public static VirtualGame newGame(GameStartedUpdate update) {
        Map<String, Color> players = update.playersToColors();
        VirtualGame game = new VirtualGame();
        players.forEach((username, color) -> game.players.add(new VirtualPlayer(username, color)));
        game.handleGameStartedUpdate(update);
        Log.getLogger().info("Creating new VirtualGame - " + update.toString());
        return game;
    }

    /**
     * Loads a game from a complete game information snapshot.
     * @param gameInfo The complete game information.
     * @return A new instance of VirtualGame loaded with the specified game information.
     */
    public static VirtualGame loadGame(CompleteGameInfo gameInfo) {
        VirtualGame game = new VirtualGame();
        gameInfo.players().forEach(completePlayerInfo -> {
            VirtualPlayer player = initializePlayer(completePlayerInfo);
            completePlayerInfo.tiles().forEach(
                    tile -> player.getBoard().placeCard(
                            new VirtualCard(tile.cardId(), tile.flipped()),
                            tile.row(),
                            tile.col()
                    )
            );
            game.players.add(player);
        });
        game.commonObjectives = gameInfo.commonObjectiveIds();
        game.drawableArea = new VirtualDrawable(
                gameInfo.drawArea().remainingResources(),
                gameInfo.drawArea().remainingGolds(),
                gameInfo.drawArea().deckTopResource(),
                gameInfo.drawArea().deckTopGold(),
                gameInfo.drawArea().revealedResources(),
                gameInfo.drawArea().revealedGolds()
        );
        game.handleGameStateUpdate(gameInfo.gameState());
        return game;
     }

    /**
     * Initializes a VirtualPlayer from complete player information.
     * @param completePlayerInfo The complete player information.
     * @return A new instance of VirtualPlayer.
     */
    private static VirtualPlayer initializePlayer(CompletePlayerInfo completePlayerInfo) {
        VirtualPlayer player = new VirtualPlayer(completePlayerInfo.username(), completePlayerInfo.color());
        player.setPoints(completePlayerInfo.points());
        player.setPersonalObjectiveId(completePlayerInfo.personalObjectiveId());
        player.setHand(completePlayerInfo.hand().handIds());
        player.setHiddenHand(completePlayerInfo.hiddenHand().hiddenHand());
        player.setActiveSymbols(completePlayerInfo.activeSymbols());
        player.setPlaiyng(completePlayerInfo.playing());
        return player;
    }

    /**
     * Retrieves the list of players in the game.
     * @return A list of VirtualPlayer.
     */
    public List<VirtualPlayer> getPlayers() {
        return players;
    }

    /**
     * Retrieves a player by their username.
     * @param username The username of the player.
     * @return The VirtualPlayer with the specified username, or null if not found.
     */
    public VirtualPlayer getPlayerByUsername(String username) {
        for (VirtualPlayer p : this.players)
            if (p.getUsername().equals(username)) return p;
        return null;
    }

    /**
     * Processes a game update and applies the necessary changes to the game state.
     * @param gameUpdate The game update to process.
     */
    public void processGameUpdate(GameUpdate gameUpdate) {
        Log.getLogger().info("Received GameUpdate - " + gameUpdate.toString());
        switch (gameUpdate.getType()) {
            case GAME_STARTED_UPDATE -> this.handleGameStartedUpdate((GameStartedUpdate) gameUpdate);
            case GAME_STATE_UPDATE -> this.handleGameStateUpdate((GameStateChangedUpdate) gameUpdate);
            case CARD_PLACED_UPDATE -> this.handleCardPlacedUpdate((CardPlacedUpdate) gameUpdate);
            case HAND_UPDATE -> this.handleHandUpdate((HandUpdate) gameUpdate);
            case HIDDEN_HAND_UPDATE -> this.handleHiddenHandUpdate((HiddenHandUpdate) gameUpdate);
            case DRAW_AREA_UPDATE -> this.handleDrawAreaUpdate((DrawAreaUpdate) gameUpdate);
            case END_GAME_UPDATE -> this.handleEndGameUpdate((EndGameUpdate) gameUpdate);
            case IS_PLAYING_UPDATE -> this.handleIsPlayingUpdate((IsPlayingUpdate) gameUpdate);
        }
    }

    /**
     * Handles updates to a player's playing status.
     * @param update The update containing the new playing status.
     */
    private void handleIsPlayingUpdate(IsPlayingUpdate update){
        VirtualPlayer player = this.getPlayerByUsername(update.username());
        player.setPlaiyng(update.status());
        player.notifyObservers();
        this.notifyObservers();
    }

    /**
     * Handles updates to the game state.
     * @param update The game state change update.
     */
    private void handleGameStateUpdate(GameStateChangedUpdate update) {
        this.gameState = update.gameStateType();
        this.turn = update.turn();
        this.round = update.round();
        this.endGame = update.endGame();
        this.finalRound = update.finalRound();
        this.currentPlayer = this.getPlayerByUsername(update.currentPlayer());
        this.notifyObservers();
    }

    /**
     * Handles updates when a card is placed on a player's board.
     * @param update The update containing the card placement information.
     */
    public void handleCardPlacedUpdate(CardPlacedUpdate update) {
        VirtualPlayer player = this.getPlayerByUsername(update.username());
        VirtualCard card = new VirtualCard(update.cardId(), update.flipped());
        VirtualBoard board = player.getBoard();
        board.placeCard(card, update.row(), update.col());
        player.setPoints(update.points());
        player.setActiveSymbols(update.activeSymbols());
        player.notifyObservers();
        board.notifyObservers();
    }

    /**
     * Handles updates to a player's hand of cards.
     * @param update The update containing the new hand information.
     */
    private void handleHandUpdate(HandUpdate update) {
        VirtualPlayer player = this.getPlayerByUsername(update.username());
        player.setHand(update.handIds());
        player.notifyObservers();
    }

    /**
     * Handles updates to a player's hidden hand of cards.
     * @param update The update containing the new hidden hand information.
     */
    private void handleHiddenHandUpdate(HiddenHandUpdate update) {
        VirtualPlayer player = this.getPlayerByUsername(update.username());
        player.setHiddenHand(update.hiddenHand());
        player.notifyObservers();
    }

    /**
     * Handles the initial game start update.
     * @param update The game start update.
     */
    private void handleGameStartedUpdate(GameStartedUpdate update){
        this.drawableArea = new VirtualDrawable(update.remainingResources(), update.remainingGolds(), update.deckTopResource(), update.deckTopGold(), update.revealedResourcesIds(), update.revealedGoldsIds());
        this.commonObjectives = update.commonObjectivesIds();
        this.notifyObservers();
        //this.drawableArea.notifyObservers();
    }

    /**
     * Handles updates to the drawable area of the game.
     * @param update The update containing changes to the drawable area.
     */
    private void handleDrawAreaUpdate(DrawAreaUpdate update){
        this.drawableArea.setRemainingResources(update.remainingResources());
        this.drawableArea.setRemainingGolds(update.remainingGolds());
        this.drawableArea.setDeckTopResource(update.deckTopResource());
        this.drawableArea.setDeckTopGold(update.deckTopGold());
        this.drawableArea.setRevealedResourcesIds(update.revealedResources());
        this.drawableArea.setRevealedGoldsIds(update.revealedGolds());
        this.drawableArea.notifyObservers();
    }

    /**
     * Handles the end game update, setting final scores and determining the winner.
     * @param update The end game update.
     */
    private void handleEndGameUpdate(EndGameUpdate update){
        this.gameState = GameStateType.END_GAME;
        for (Map.Entry<String, Integer[]> entry : update.playerToPoints().entrySet()) {
            String username = entry.getKey();
            Integer[] pointsAndObjectives = entry.getValue();
            VirtualPlayer player = this.getPlayerFromUsername(username);

            if (player == null) {
                Log.getLogger().severe("Received invalid EndGameUpdate: player username mismatch");
                continue;
            }

            if (pointsAndObjectives.length < 3) {
                Log.getLogger().severe("Received invalid EndGameUpdate: missing points and/or completed objectives");
                player.setPoints(0);
                player.setCompletedObjectives(0);
                player.setPersonalObjectiveId(0);
            } else {
                player.setPoints(pointsAndObjectives[0]);
                player.setCompletedObjectives(pointsAndObjectives[1]);
                player.setPersonalObjectiveId(pointsAndObjectives[2]);
                forfeitWinner = this.getPlayerFromUsername(update.forfeitWinner());
            }
        }
        this.notifyObservers();
    }

    /**
     * Retrieves the current game round.
     * @return The current round number.
     */
    public int getRound() {
        return round;
    }

    /**
     * Retrieves the current turn within the round.
     * @return The current turn number.
     */
    public int getTurn() {
        return turn;
    }

    /**
     * Checks if the game has ended.
     * @return True if the game has ended, otherwise false.
     */
    public boolean getEndGame() {
        return endGame;
    }

    /**
     * Checks if the game is in the final round.
     * @return True if it is the final round, otherwise false.
     */
    public boolean getFinalRound() {
        return finalRound;
    }

    /**
     * Retrieves the current game state.
     * @return The current GameStateType.
     */
    public GameStateType getGameState() {
        return gameState;
    }

    /**
     * Retrieves a player by their username.
     * @param username The username to search for.
     * @return The VirtualPlayer with the specified username, or null if not found.
     */
    private VirtualPlayer getPlayerFromUsername(String username) {
        for (int i = 0; i < this.players.size(); i++)
            if (this.players.get(i).getUsername().equals(username))
                return this.players.get(i);
        return null;
    }

    /**
     * Retrieves the current player.
     * @return The current VirtualPlayer.
     */
    public VirtualPlayer getCurrentPlayer() { return currentPlayer; }

    /**
     * Retrieves the list of common objectives.
     * @return A list of common objective IDs.
     */
    public List<Integer> getCommonObjectives() { return commonObjectives; }

    /**
     * Retrieves the drawable area of the game.
     * @return The current VirtualDrawable.
     */
    public VirtualDrawable getDrawableArea() { return drawableArea; }

    /**
     * Retrieves the ranking of players based on points and objectives completed.
     * @return A sorted list of VirtualPlayer based on their ranking.
     */
    public List<VirtualPlayer> getRanking() {
        return this.players.stream()
                .sorted((p1, p2) -> {
                    if (p1.getPoints() > p2.getPoints())
                        return -1;
                    else if (p1.getPoints() < p2.getPoints())
                        return 1;
                    else if (p1.getCompletedObjectives() > p2.getCompletedObjectives())
                        return -1;
                    else if (p1.getCompletedObjectives() < p2.getCompletedObjectives())
                        return 1;
                    return 0;
                })
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Retrieves a player by their color.
     * @param color The color to search for.
     * @return The VirtualPlayer with the specified color, or null if not found.
     */
    public VirtualPlayer getVirtualPlayerByColor( Color color ){
        return this.players.stream().filter(p -> p.getColor().equals(color)).findFirst().orElse(null);
    }

    /**
     * Retrieves the winner by forfeit.
     * @return The VirtualPlayer who won by forfeit.
     */
    public VirtualPlayer getforfeitWinner() {
        return forfeitWinner;
    }

    /**
     * Clears all observers from the game and its components.
     */
    public void clearAllObservers() {
        this.clearObservers();
        this.players.forEach(Observable::clearObservers);
        this.drawableArea.clearObservers();
    }
}
