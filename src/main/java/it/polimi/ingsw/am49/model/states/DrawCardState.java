package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.messages.mts.DrawCardMTS;
import it.polimi.ingsw.am49.messages.mts.MessageToServer;
import it.polimi.ingsw.am49.messages.mts.MessageToServerType;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.enumerations.DrawPosition;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.Set;

public class DrawCardState extends GameState {

    private final Player currentPlayer;

    protected DrawCardState(Game game) {
        super(GameStateType.DRAW_CARD, game, Set.of(MessageToServerType.DRAW_CARD));
        this.currentPlayer = game.getCurrentPlayer();
    }

    @Override
    public void setUp() {}

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

        this.game.incrementTurn();
        if (this.currentPlayer.equals(this.game.getLastPlayer()))
            this.game.incrementRound();
        this.game.setCurrentPlayer(this.game.getNextPlayer());
        this.game.setGameState(new PlaceCardState(this.game));
    }

    @Override
    protected boolean isYourTurn(MessageToServer msg) {
        return this.currentPlayer.getUsername().equals(msg.getUsername());
    }
}
