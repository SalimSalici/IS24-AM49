package it.polimi.ingsw.am49.messages.mts;

import it.polimi.ingsw.am49.model.enumerations.CornerPosition;

public class PlaceCardMTS extends MessageToServer {

    private final int cardId;
    private final int parentRow;
    private final int parentCol;
    private final CornerPosition cornerPosition;
    private final boolean flipped;

    public PlaceCardMTS(String username, int cardId, int parentRow, int parentCol, CornerPosition cornerPosition, boolean flipped) {
        super(MessageToServerType.PLACE_CARD, username);
        this.cardId = cardId;
        this.parentRow = parentRow;
        this.parentCol = parentCol;
        this.cornerPosition = cornerPosition;
        this.flipped = flipped;
    }

    public int getCardId() {
        return cardId;
    }

    public int getParentRow() {
        return parentRow;
    }

    public int getParentCol() {
        return parentCol;
    }

    public CornerPosition getCornerPosition() {
        return cornerPosition;
    }

    public boolean getFlipped() {
        return flipped;
    }
}
