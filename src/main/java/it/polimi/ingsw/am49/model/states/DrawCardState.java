package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.messages.mts.DrawCardMTS;
import it.polimi.ingsw.am49.messages.mts.MessageToServer;
import it.polimi.ingsw.am49.messages.mts.MessageToServerType;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.model.enumerations.DrawPosition;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.events.HandUpdateEvent;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.Set;

public class DrawCardState extends GameState {

    private final Player currentPlayer;

    protected DrawCardState(Game game) {
        super(GameStateType.DRAW_CARD, game, Set.of(MessageToServerType.DRAW_CARD));
        this.currentPlayer = game.getCurrentPlayer();
    }

    @Override
    public void execute(MessageToServer msg) throws Exception {
        this.checkMsgValidity(msg);
        DrawCardMTS drawCardMsg = (DrawCardMTS) msg;
        DrawPosition drawPosition = drawCardMsg.getDrawPosition();
        switch (drawPosition) {
            case RESOURCE_DECK -> this.currentPlayer.drawCard(this.game.getResourceGameDeck().draw());
            case GOLD_DECK -> this.currentPlayer.drawCard(this.game.getGoldGameDeck().draw());
            // TODO: handle draw from revealed cards
        }

        // TODO: player.hand should become a list of PlaceableCards
        this.game.triggerEvent(
                new HandUpdateEvent(currentPlayer, currentPlayer.getHand().stream().map(c -> (PlaceableCard)c).toList())
        );

        // Check if game is over
        if (this.game.isFinalRound() && this.currentPlayer.equals(this.game.getLastPlayer())) {
            this.nextState = new EndGameState(this.game);
            this.goToNextState();
            return;
        }

        // Game is not over, move on to the next turn
        this.handleSwitchToNextTurn();

        this.nextState = new PlaceCardState(this.game);
        this.goToNextState();
    }

    @Override
    protected boolean isYourTurn(MessageToServer msg) {
        return this.currentPlayer.getUsername().equals(msg.getUsername());
    }

    private void handleSwitchToNextTurn() {
        this.game.incrementTurn();

        if (this.currentPlayer.getPoints() > 20)
            this.game.setEndGame(true);

        if (this.currentPlayer.equals(this.game.getLastPlayer())) {
            this.game.incrementRound();
            if (this.game.isEndGame())
                this.game.setFinalRound(true);
        }

        this.game.setCurrentPlayer(this.game.getNextPlayer());
    }
}
