package it.polimi.ingsw.am49.model.actions;

import it.polimi.ingsw.am49.model.enumerations.DrawPosition;

public class DrawCardAction extends GameAction {
    private final DrawPosition drawPosition;
    private final int idOfRevealedDrawn;

    public DrawCardAction(String username, DrawPosition drawPosition, int idOfRevealedDrawn) {
        super(GameActionType.DRAW_CARD, username);
        this.drawPosition = drawPosition;
        this.idOfRevealedDrawn = idOfRevealedDrawn;
    }

    public DrawPosition getDrawPosition() {
        return drawPosition;
    }

    public int getIdOfRevealedDrawn() {
        return idOfRevealedDrawn;
    }
}
