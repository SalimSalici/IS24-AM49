package it.polimi.ingsw.am49.common.messages;

import it.polimi.ingsw.am49.common.actions.GameAction;

public record ExecuteActionMTS(int id, GameAction gameAction) implements SocketMessage {}