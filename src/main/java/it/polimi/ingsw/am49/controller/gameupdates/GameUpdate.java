package it.polimi.ingsw.am49.controller.gameupdates;

import java.io.Serializable;

public interface GameUpdate extends Serializable {
    GameUpdateType getType();
}
