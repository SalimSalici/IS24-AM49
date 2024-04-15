package it.polimi.ingsw.am49.messages.mts;

import it.polimi.ingsw.am49.Client;
import it.polimi.ingsw.am49.model.actions.GameAction;

public class GameActionMTS extends MessageToServer {

    private final GameAction action;

    public GameActionMTS(Client client, GameAction action) {
        super(MessageToServerType.GAME_ACTION, client);
        this.action = action;
    }

    public GameAction getAction() {
        return this.action;
    }
}
