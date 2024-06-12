package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.actions.GameActionType;
import it.polimi.ingsw.am49.model.actions.PlaceCardAction;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.model.enumerations.CornerPosition;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.events.*;
import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.Player;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.util.Log;

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
    protected PlaceCardState(Game game) {
        super(GameStateType.PLACE_CARD, game, Set.of(GameActionType.PLACE_CARD));
        this.currentPlayer = game.getCurrentPlayer();
    }

    @Override
    public void setUp() {
        super.setUp();
        if (!this.currentPlayer.isOnline())
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

    @Override
    protected boolean isYourTurn(GameAction action) {
        return this.currentPlayer.getUsername().equals(action.getUsername());
    }

    @Override
    public void disconnectPlayer(String username) {
        Player player = this.game.getPlayerByUsername(username);
        if (player == null || !player.isOnline()) return;

        player.setIsOnline(false);

        if (this.currentPlayer.equals(player))
            this.skipTurn();
    }

    private void skipTurn() {
        DrawCardState drawCardState = new DrawCardState(this.game);
        drawCardState.handleSwitchToNextTurn();
    }
}
