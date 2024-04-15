package it.polimi.ingsw.am49.messages.mts;

import it.polimi.ingsw.am49.model.actions.GameAction;

public class GameActionMTS extends MessageToServer {

    private final GameAction action;

    public GameActionMTS(String username, GameAction action) {
        super(MessageToServerType.GAME_ACTION, username);
        this.action = action;
    }

    public GameAction getAction() {
        return this.action;
    }
}
