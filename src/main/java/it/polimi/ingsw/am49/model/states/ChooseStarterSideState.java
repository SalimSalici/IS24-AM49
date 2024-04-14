package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.messages.mts.ChooseStarterSideMTS;
import it.polimi.ingsw.am49.messages.mts.MessageToServer;
import it.polimi.ingsw.am49.messages.mts.MessageToServerType;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.model.decks.DeckLoader;
import it.polimi.ingsw.am49.model.decks.GameDeck;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.events.CardPlacedEvent;
import it.polimi.ingsw.am49.model.events.GameStateChangedEvent;
import it.polimi.ingsw.am49.model.events.HandUpdateEvent;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.HashSet;
import java.util.Set;

public class ChooseStarterSideState extends GameState {

    private final Set<Player> playersChoosing;

    protected ChooseStarterSideState(Game game) {
        super(GameStateType.CHOOSE_STARTER_SIDE, game, Set.of(MessageToServerType.CHOOSE_STARTER_SIDE));
        this.playersChoosing = new HashSet<>(game.getPlayers());
        this.notYourTurnMessage =
                "You have already choosen the side of your starter card. You must wait for the other players.";
    }

    @Override
    public void setUp() {
        // TODO: (maybe) set up drawable decks and revealed drawable cards here

        GameDeck<StarterCard> starterDeck = DeckLoader.getInstance().getNewStarterDeck();
        for (Player p : this.game.getPlayers())
            p.setStarterCard(starterDeck.draw());

        this.game.triggerEvent(new GameStateChangedEvent(this.type, this.game.getTurn(), this.game.getRound()));
    }

    @Override
    public void execute(MessageToServer msg) throws Exception {
        this.checkMsgValidity(msg);

        Player player = this.game.getPlayerByUsername(msg.getUsername());
        boolean flipped = ((ChooseStarterSideMTS)msg).getFlipped();
        player.chooseStarterSide(flipped);
        this.game.triggerEvent(new CardPlacedEvent(player, player.getBoard().getStarterTile()));

        this.playersChoosing.remove(player);
        if (this.playersChoosing.isEmpty()) {
            this.assignInitialHand();
            this.nextState = new ChooseObjectiveState(this.game);
            this.goToNextState();
        }
    }

    @Override
    protected boolean isYourTurn(MessageToServer msg) {
        return playersChoosing.contains(this.game.getPlayerByUsername(msg.getUsername()));
    }

    private void assignInitialHand() throws Exception {
        for (Player p : this.game.getPlayers()) {
            p.drawCard(this.game.getResourceGameDeck().draw());
            p.drawCard(this.game.getResourceGameDeck().draw());
            p.drawCard(this.game.getGoldGameDeck().draw());

            // TODO: ColouredCard must be deleted. StartedCard, ResourceCard and GoldCard must all extend PlaceableCard directly
            this.game.triggerEvent(
                    new HandUpdateEvent(p, p.getHand().stream().map(c -> (PlaceableCard)c).toList())
            );
        }
    }
}
