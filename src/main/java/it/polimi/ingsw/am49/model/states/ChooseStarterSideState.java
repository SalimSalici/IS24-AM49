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
import it.polimi.ingsw.am49.model.events.StarterCardAssignedEvent;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChooseStarterSideState extends GameState {

    /**
     * Used to keep track of the players that have already chosen the side of ther starter card.
     */
    private final Set<Player> playersChoosing;

    /**
     * The number of {@link it.polimi.ingsw.am49.model.cards.placeables.ResourceCard}'s in the starter card;
     */
    private final int starterHandResources = 2;

    /**
     * The number of {@link it.polimi.ingsw.am49.model.cards.placeables.GoldCard}'s in the starter card;
     */
    private final int starterHandGolds = 1;

    /**
     * Constructor for ChoseStarterSideState.
     * @param game istance of the {@link Game} class.
     */
    protected ChooseStarterSideState(Game game) {
        super(GameStateType.CHOOSE_STARTER_SIDE, game, Set.of(GameActionType.CHOOSE_STARTER_SIDE));
        this.playersChoosing = new HashSet<>(game.getPlayers());
        this.notYourTurnMessage =
                "You have already choosen the side of your starter card. You must wait for the other players.";
    }

    /**
     * Deals one starter card for every player in the game and norifies that the game state has changed to ChooseStarterSide.
     */
    @Override
    public void setUp() {
        // TODO: (maybe) set up drawable decks and revealed drawable cards here

        GameDeck<StarterCard> starterDeck = DeckLoader.getInstance().getNewStarterDeck();
        Map<Player, StarterCard> playersToStartingCard = new HashMap<>();

        for (Player p : this.game.getPlayers()) {
            StarterCard card = starterDeck.draw();
            p.setStarterCard(card);
            playersToStartingCard.put(p, card);
        }

        this.game.triggerEvent(new StarterCardAssignedEvent(playersToStartingCard));
        this.game.triggerEvent(new GameStateChangedEvent(this.type, this.game.getTurn(), this.game.getRound(), this.game.getCurrentPlayer()));
    }

    /**
     * Handles the main logic for the {@link ChooseStarterSideState}.
     * @param action tells witch type of {@link GameAction} neds to be handled.
     * @throws Exception
     */
    @Override
    public void execute(GameAction action) throws Exception {
        this.checkActionValidity(action);

        Player player = this.game.getPlayerByUsername(action.getUsername());
        boolean flipped = ((ChooseStarterSideAction)action).getFlipped();
        player.chooseStarterSide(flipped);
        this.game.triggerEvent(new CardPlacedEvent(player, player.getBoard().getStarterTile(), player.getPoints()));

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

    /**
     * Deals three cards for the initial hand of eatch player.
     * @throws Exception
     */
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
