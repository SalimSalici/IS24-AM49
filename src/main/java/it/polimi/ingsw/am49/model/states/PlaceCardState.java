package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.messages.mts.MessageToServer;
import it.polimi.ingsw.am49.messages.mts.MessageToServerType;
import it.polimi.ingsw.am49.messages.mts.PlaceCardMTS;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.model.enumerations.CornerPosition;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.Set;

public class PlaceCardState extends GameState {

    private final Player currentPlayer;
    protected PlaceCardState(Game game) {
        super(GameStateType.PLACE_CARD, game, Set.of(MessageToServerType.PLACE_CARD));
        this.currentPlayer = game.getCurrentPlayer();
    }

    @Override
    public void execute(MessageToServer msg) throws Exception {
        this.checkMsgValidity(msg);
        PlaceCardMTS placeCardMsg = (PlaceCardMTS) msg;

        PlaceableCard card = this.currentPlayer.getHandCardById(placeCardMsg.getCardId());
        if (card == null)
            throw new Exception("You are trying to place a card that is not in your hand");

        card.setFlipped(placeCardMsg.getFlipped());
        int parentRow = placeCardMsg.getParentRow();
        int parentCol = placeCardMsg.getParentCol();
        CornerPosition cornerPosition = placeCardMsg.getCornerPosition();

        try {
            this.currentPlayer.placeCard(card, parentRow, parentCol, cornerPosition);
        } catch (Exception ex) {
            throw new Exception("Could not place tile");
        }

        this.goToNextState(new DrawCardState(this.game));
    }

    @Override
    protected boolean isYourTurn(MessageToServer msg) {
        return this.currentPlayer.getUsername().equals(msg.getUsername());
    }
}
