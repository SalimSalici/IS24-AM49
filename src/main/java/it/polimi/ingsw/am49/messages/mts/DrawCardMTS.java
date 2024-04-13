package it.polimi.ingsw.am49.messages.mts;

import it.polimi.ingsw.am49.model.enumerations.DrawPosition;

public class DrawCardMTS extends MessageToServer {
    private final DrawPosition drawPosition;
    private final int idOfRevealedDrawn;

    public DrawCardMTS(String username, DrawPosition drawPosition, int idOfRevealedDrawn) {
        super(MessageToServerType.DRAW_CARD, username);
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
