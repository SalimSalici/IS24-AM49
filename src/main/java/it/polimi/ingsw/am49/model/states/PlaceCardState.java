package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.actions.GameActionType;
import it.polimi.ingsw.am49.model.actions.PlaceCard;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.model.enumerations.CornerPosition;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.Set;

public class PlaceCardState extends GameState {

    private final Player currentPlayer;
    protected PlaceCardState(Game game) {
        super(GameStateType.PLACE_CARD, game, Set.of(GameActionType.PLACE_CARD));
        this.currentPlayer = game.getCurrentPlayer();
    }

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

        try {
            this.currentPlayer.placeCard(card, parentRow, parentCol, cornerPosition);
        } catch (Exception ex) {
            throw new Exception("Could not place tile");
        }

        this.goToNextState(new DrawCardState(this.game));
    }

    @Override
    protected boolean isYourTurn(GameAction action) {
        return this.currentPlayer.getUsername().equals(action.getUsername());
    }
}
