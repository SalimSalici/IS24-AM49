package it.polimi.ingsw.am49.messages.mtc;

public class CardPlacedMTC extends MessageToClient{
    private final String username;
    private final int cardId;
    private final int row;
    private final int col;
    private final boolean flipped;
    private final int points;

    public CardPlacedMTC(String username, int cardId, int row, int col, boolean flipped, int points){
        super(MessageToClientType.CARD_PLACED, " placed the card: ");
        this.username = username;
        this.cardId = cardId;
        this.row = row;
        this.col = col;
        this.flipped = flipped;
        this.points = points;
    }

    @Override
    public String getMessage() {
        return this.username + super.getMessage() + this.cardId + " in (row, col): (" + this.row + ", " + this.col + ")";
    }


    public int getCardId() {
        return cardId;
    }

    public String getUsername() {
        return username;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public int getPoints() {
        return points;
    }
}