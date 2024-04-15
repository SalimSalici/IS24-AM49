package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.model.actions.ChooseStarterSideAction;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.actions.GameActionType;
import it.polimi.ingsw.am49.model.Game;
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

    /**
     * The number of {@link it.polimi.ingsw.am49.model.cards.placeables.ResourceCard}'s in the starter card;
     */
    private final int starterHandResources = 2;

    /**
     * The number of {@link it.polimi.ingsw.am49.model.cards.placeables.GoldCard}'s in the starter card;
     */
    private final int starterHandGolds = 1;

    protected ChooseStarterSideState(Game game) {
        super(GameStateType.CHOOSE_STARTER_SIDE, game, Set.of(GameActionType.CHOOSE_STARTER_SIDE));
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
    public void execute(GameAction action) throws Exception {
        this.checkActionValidity(action);

        Player player = this.game.getPlayerByUsername(action.getUsername());
        boolean flipped = ((ChooseStarterSideAction)action).getFlipped();
        player.chooseStarterSide(flipped);
        this.game.triggerEvent(new CardPlacedEvent(player, player.getBoard().getStarterTile()));

        this.playersChoosing.remove(player);
        if (this.playersChoosing.isEmpty()) {
            this.assignInitialHand();
            this.goToNextState(new ChooseObjectiveState(this.game));
        }
    }

    @Override
    protected boolean isYourTurn(GameAction action) {
        return playersChoosing.contains(this.game.getPlayerByUsername(action.getUsername()));
    }

    private void assignInitialHand() throws Exception {
        for (Player p : this.game.getPlayers()) {
            for (int i = 0; i < this.starterHandResources; i++)
                p.drawCard(this.game.getResourceGameDeck().draw());

            for (int i = 0; i < this.starterHandGolds; i++)
                p.drawCard(this.game.getGoldGameDeck().draw());

            this.game.triggerEvent(
                    new HandUpdateEvent(p, p.getHand().stream().toList())
            );
        }
    }
}
