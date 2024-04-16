package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.actions.GameActionType;
import it.polimi.ingsw.am49.model.actions.PlaceCard;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.model.enumerations.CornerPosition;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.events.CardPlacedEvent;
import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.Player;

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

    /**
     * Handles the placement of a card.
     * @param action tells witch type of {@link GameAction} neds to be handled.
     * @throws Exception if the card can not be placed.
     */
    @Override
    public void execute(GameAction action) throws Exception {
        this.checkActionValidity(action);
        PlaceCard placeCardAction = (PlaceCard) action;

        PlaceableCard card = this.currentPlayer.getHandCardById(placeCardAction.getCardId());
        if (card == null)
            throw new Exception("You are trying to place a card that is not in your hand");

        card.setFlipped(placeCardAction.getFlipped());
        int parentRow = placeCardAction.getParentRow();
        int parentCol = placeCardAction.getParentCol();
        CornerPosition cornerPosition = placeCardAction.getCornerPosition();

        BoardTile newTile;
        try {
            newTile = this.currentPlayer.placeCard(card, parentRow, parentCol, cornerPosition);
        } catch (Exception ex) {
            throw new Exception("Could not place tile");
        }

        this.game.triggerEvent(new CardPlacedEvent(currentPlayer, newTile, currentPlayer.getPoints()));
        this.goToNextState(new DrawCardState(this.game));
    }

    @Override
    protected boolean isYourTurn(GameAction action) {
        return this.currentPlayer.getUsername().equals(action.getUsername());
    }
}
