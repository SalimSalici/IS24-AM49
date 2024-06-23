package it.polimi.ingsw.am49.server.model.states;

import it.polimi.ingsw.am49.common.actions.GameAction;
import it.polimi.ingsw.am49.common.actions.GameActionType;
import it.polimi.ingsw.am49.common.actions.PlaceCardAction;
import it.polimi.ingsw.am49.server.model.Game;
import it.polimi.ingsw.am49.server.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.common.enumerations.CornerPosition;
import it.polimi.ingsw.am49.common.enumerations.GameStateType;
import it.polimi.ingsw.am49.server.model.events.CardPlacedEvent;
import it.polimi.ingsw.am49.server.model.players.BoardTile;
import it.polimi.ingsw.am49.server.model.players.Player;
import it.polimi.ingsw.am49.common.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.common.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.common.util.Log;
import it.polimi.ingsw.am49.server.model.events.HandEvent;

import java.util.Set;

/**
 * Represents the game state where a player places a card on the game board.
 * This state handles the placement of a card with the corresponding exeptions oraginating if the card can not be
 * placad in the desired spot or there aren't enough resources.
 */
public class PlaceCardState extends GameState {

    private final Player currentPlayer;

    /**
     * Constructor for the PlaceCardState.
     * @param game istance of the {@link Game} class.
     */
    public PlaceCardState(Game game) {
        super(GameStateType.PLACE_CARD, game, Set.of(GameActionType.PLACE_CARD));
        this.currentPlayer = game.getCurrentPlayer();
    }

    @Override
    public void setUp() {
        super.setUp();
        if (!this.currentPlayer.isOnline() || this.currentPlayer.getBoard().isDeadlocked())
            this.skipTurn();
    }

    /**
     * Handles the placement of a card.
     * @param action tells witch type of {@link GameAction} neds to be handled.
     * @throws InvalidActionException if the action is not supported by this state.
     * @throws NotYourTurnException if the player making the action is not the current player.
     */
    @Override
    public void execute(GameAction action) throws InvalidActionException, NotYourTurnException {
        this.checkActionValidity(action);

        Log.getLogger().info("Executing action " + action.toString());

        PlaceCardAction placeCardAction = (PlaceCardAction) action;

        PlaceableCard card = this.currentPlayer.getHandCardById(placeCardAction.getCardId());
        if (card == null)
            throw new InvalidActionException("Invalid action. You tried to place a card that is not in your hand.");

        card.setFlipped(placeCardAction.getFlipped());
        int parentRow = placeCardAction.getParentRow();
        int parentCol = placeCardAction.getParentCol();
        CornerPosition cornerPosition = placeCardAction.getCornerPosition();

        BoardTile newTile = this.currentPlayer.placeCard(card, parentRow, parentCol, cornerPosition);

        this.game.triggerEvent(new HandEvent(currentPlayer, currentPlayer.getHand().stream().toList()));
        this.game.triggerEvent(new CardPlacedEvent(currentPlayer, newTile));
        this.goToNextState(new DrawCardState(this.game));
    }

    /**
     * Handles the disconnection of a player.
     * @param username the username of the player to be disconnected.
     */
    @Override
    public void disconnectPlayer(String username) {
        Player player = this.game.getPlayerByUsername(username);
        if (player == null || !player.isOnline()) return;

        player.setIsOnline(false);

        if (this.currentPlayer.equals(player))
            this.skipTurn();
    }

    /**
     * Skips the turn of the current player.
     */
    private void skipTurn() {
        this.game.handleSwitchToNextTurn();
    }
}
