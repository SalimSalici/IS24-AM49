package it.polimi.ingsw.am49.common.actions;

import it.polimi.ingsw.am49.common.enumerations.DrawPosition;

/**
 * Represents an action of drawing a card in the game.
 */
public class DrawCardAction extends GameAction {

    /**
     * The position in the draw area from which to draw
     */
    private final DrawPosition drawPosition;
    private final int idOfRevealedDrawn;

    /**
     * Constructs a DrawCardAction with the specified username, draw position, and ID of the revealed drawn card.
     *
     * @param username the username of the player performing the action
     * @param drawPosition the position from which the card is drawn
     * @param idOfRevealedDrawn the ID of the revealed drawn card
     */
    public DrawCardAction(String username, DrawPosition drawPosition, int idOfRevealedDrawn) {
        super(GameActionType.DRAW_CARD, username);
        this.drawPosition = drawPosition;
        this.idOfRevealedDrawn = idOfRevealedDrawn;
    }

    /**
     * Returns the position from which the card is drawn.
     *
     * @return the draw position
     */
    public DrawPosition getDrawPosition() {
        return drawPosition;
    }

    /**
     * Returns the ID of the revealed drawn card.
     *
     * @return the ID of the revealed drawn card
     */
    public int getIdOfRevealedDrawn() {
        return idOfRevealedDrawn;
    }

    /**
     * Returns a string representation of the DrawCardAction.
     *
     * @return a string representation of the DrawCardAction
     */
    public String toString() {
        return "DrawCardAction[username=" + username + ", drawPosition=" + drawPosition + ", idOfRevealedDrawn=" + idOfRevealedDrawn + "]";
    }
}
