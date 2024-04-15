package it.polimi.ingsw.am49.messages.mtc;

public class CardDrawnMTC extends MessageToClient{
    private final int cardId;

    public CardDrawnMTC(int cardId){
        super(MessageToClientType.CARD_DRAWN, "You drew the card: ");
        this.cardId = cardId;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + this.cardId;
    }
}
