package it.polimi.ingsw.am49.messages.mtc;

public class CardPlacedMTC extends MessageToClient{
    private final int cardId;
    private final String userName;
    private final int row;
    private final int col;

    public CardPlacedMTC(int cardId, int row, int col, String userName){
        super(MessageToClientType.CARD_PLACED, " placed the card: ");
        this.cardId = cardId;
        this.userName = userName;
        this.row = row;
        this.col = col;
    }

    @Override
    public String getMessage() {
        return this.userName + super.getMessage() + this.cardId + " in (row, col): (" + this.row + ", " + this.col + ")";
    }
}