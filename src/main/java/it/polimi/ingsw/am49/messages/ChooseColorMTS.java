package it.polimi.ingsw.am49.messages;

import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.enumerations.Color;

public record ChooseColorMTS(
        int id,
        Color color
) implements SocketMessage {}