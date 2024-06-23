package it.polimi.ingsw.am49.common.enumerations;

/**
 * Enum representing the corner positions on a card.
 */
public enum CornerPosition {
    /** The top left corner position */
    TOP_LEFT,

    /** The top right corner position */
    TOP_RIGHT,

    /** The bottom left corner position */
    BOTTOM_LEFT,

    /** The bottom right corner position */
    BOTTOM_RIGHT;

    /**
     * Converts the corner position to a relative position.
     *
     * @return the corresponding {@link RelativePosition} for the corner position
     */
    public RelativePosition toRelativePosition() {
        RelativePosition relativePosition = null;
        switch (this) {
            case TOP_RIGHT -> relativePosition = RelativePosition.TOP_RIGHT;
            case TOP_LEFT -> relativePosition = RelativePosition.TOP_LEFT;
            case BOTTOM_RIGHT -> relativePosition = RelativePosition.BOTTOM_RIGHT;
            case BOTTOM_LEFT -> relativePosition = RelativePosition.BOTTOM_LEFT;
        }
        return relativePosition;
    }
}
