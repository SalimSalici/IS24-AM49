package it.polimi.ingsw.am49.messages;

import it.polimi.ingsw.am49.model.actions.GameAction;

public record ExecuteActionMTS(
        int id,
        GameAction gameAction
) implements SocketMessage {}