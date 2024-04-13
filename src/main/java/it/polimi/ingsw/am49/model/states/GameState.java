package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.messages.mts.MessageToServer;
import it.polimi.ingsw.am49.messages.mts.MessageToServerType;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;

import java.util.HashSet;
import java.util.Set;

public abstract class GameState {

    protected final GameStateType type;
    protected final Game game;
    protected final Set<MessageToServerType> acceptableMessageTypes;

    protected GameState(GameStateType type, Game game, Set<MessageToServerType> acceptableMessageTypes) {
        this.type = type;
        this.game = game;
        this.acceptableMessageTypes = new HashSet<>(acceptableMessageTypes);
    }

    public abstract void execute(MessageToServer msg) throws Exception;

    protected void checkMsgValidity(MessageToServer msg) throws Exception {
        if (!this.acceptableMessageTypes.contains(msg.getType())) throw new Exception("You cannot do that now");
        if (!this.isYourTurn(msg)) throw new Exception("You must wait for your turn");
    }

    protected abstract boolean isYourTurn(MessageToServer msg);
}
