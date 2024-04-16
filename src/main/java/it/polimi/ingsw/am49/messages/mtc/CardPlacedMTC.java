package it.polimi.ingsw.am49.messages.mtc;

public class CardPlacedMTC extends MessageToClient{
    private final int cardId;
    private final String userName;

    public CardPlacedMTC(int cardId, String userName){
        super(MessageToClientType.CARD_PLACED, " placed the card: ");
        this.cardId = cardId;
        this.userName = userName;
    }

    @Override
    public String getMessage() {
        return this.userName + super.getMessage() + this.cardId;
    }
}