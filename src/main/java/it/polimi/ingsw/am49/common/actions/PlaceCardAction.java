package it.polimi.ingsw.am49.common.actions;

import it.polimi.ingsw.am49.common.enumerations.CornerPosition;

/**
 * Represents an action to place a card on the game board.
 */
public class PlaceCardAction extends GameAction {

    /**
     * The ID of the card being placed.
     */
    private final int cardId;

    /**
     * The row of the parent card.
     */
    private final int parentRow;

    /**
     * The column of the parent card.
     */
    private final int parentCol;

    /**
     * The corner position where the card is placed relative to the parent card.
     */
    private final CornerPosition cornerPosition;

    /**
     * Indicates if the card is placed flipped.
     */
    private final boolean flipped;

    /**
     * Constructs a new PlaceCardAction.
     *
     * @param username the username of the player performing the action
     * @param cardId the ID of the card being placed
     * @param parentRow the row of the parent card
     * @param parentCol the column of the parent card
     * @param cornerPosition the corner position where the card is placed relative to the parent card
     * @param flipped indicates if the card is placed flipped
     */
    public PlaceCardAction(String username, int cardId, int parentRow, int parentCol, CornerPosition cornerPosition, boolean flipped) {
        super(GameActionType.PLACE_CARD, username);
        this.cardId = cardId;
        this.parentRow = parentRow;
        this.parentCol = parentCol;
        this.cornerPosition = cornerPosition;
        this.flipped = flipped;
    }

    /**
     * Returns the card ID.
     *
     * @return the card ID
     */
    public int getCardId() {
        return cardId;
    }

    /**
     * Returns the parent row.
     *
     * @return the parent row
     */
    public int getParentRow() {
        return parentRow;
    }

    /**
     * Returns the parent column.
     *
     * @return the parent column
     */
    public int getParentCol() {
        return parentCol;
    }

    /**
     * Returns the corner position.
     *
     * @return the corner position
     */
    public CornerPosition getCornerPosition() {
        return cornerPosition;
    }

    /**
     * Returns whether the card is flipped.
     *
     * @return true if the card is flipped, otherwise false
     */
    public boolean getFlipped() {
        return flipped;
    }

    /**
     * Returns a string representation of the PlaceCardAction.
     *
     * @return a string representation of the PlaceCardAction
     */
    @Override
    public String toString() {
        return "PlaceCardAction[" +
                "username=" + getUsername() +
                ", cardId=" + cardId +
                ", parentRow=" + parentRow +
                ", parentCol=" + parentCol +
                ", cornerPosition=" + cornerPosition +
                ", flipped=" + flipped +
                ']';
    }
}
