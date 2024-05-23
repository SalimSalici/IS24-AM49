package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.model.actions.ChooseStarterSideAction;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.actions.GameActionType;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.model.decks.DeckLoader;
import it.polimi.ingsw.am49.model.decks.GameDeck;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.events.*;
import it.polimi.ingsw.am49.model.players.Player;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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

    private final GameDeck<ObjectiveCard> objectiveDeck;

    /**
     * Constructor for ChoseStarterSideState.
     * @param game istance of the {@link Game} class.
     */
    public ChooseStarterSideState(Game game) {
        super(GameStateType.CHOOSE_STARTER_SIDE, game, Set.of(GameActionType.CHOOSE_STARTER_SIDE));
        this.playersChoosing = new HashSet<>(game.getPlayers());
        this.notYourTurnMessage = "You have already choosen the side of your starter card. You must wait for the other players.";
        this.objectiveDeck = DeckLoader.getInstance().getNewObjectiveDeck();
    }

    /**
     * Deals one starter card for every player in the game and norifies that the game state has changed to ChooseStarterSide.
     */
    @Override
    public void setUp() {
        Collections.shuffle(this.game.getPlayers());
        this.game.setCurrentPlayer(this.game.getStartingPlayer());
        this.game.triggerEvent(new PlayersOrderEvent(this.game.getPlayers()));
        // TODO: (maybe) set up drawable decks and revealed drawable cards here

        // Draw common objectives
        ObjectiveCard[] commonObjectives = this.game.getCommonObjectives();
        for (int i = 0; i < commonObjectives.length; i++)
            commonObjectives[i] = objectiveDeck.draw();
        this.game.triggerEvent(new CommonObjectivesDrawnEvent(List.of(commonObjectives)));

        GameDeck<StarterCard> starterDeck = DeckLoader.getInstance().getNewStarterDeck();

        for (Player p : this.game.getPlayers()) {
            StarterCard card = starterDeck.draw();
            p.setStarterCard(card);
            this.game.triggerEvent(new StarterCardAssignedEvent(p, card));
        }

        this.game.triggerEvent(new GameStateChangedEvent(this.type, this.game.getTurn(), this.game.getRound(), this.game.getCurrentPlayer()));
    }

    /**
     * Handles the main logic for the {@link ChooseStarterSideState}.
     * @param action tells witch type of {@link GameAction} neds to be handled.
     * @throws InvalidActionException if the action is not supported by this state.
     * @throws NotYourTurnException if the player making the action is not the current player.
     */
    @Override
    public void execute(GameAction action) throws InvalidActionException, NotYourTurnException {
        this.checkActionValidity(action); //if this check fails the rest of the code won't be executed

        Player player = this.game.getPlayerByUsername(action.getUsername());
        boolean flipped = ((ChooseStarterSideAction)action).getFlipped();
        player.chooseStarterSide(flipped);
        this.game.triggerEvent(new CardPlacedEvent(player, player.getBoard().getStarterTile()));

        this.playersChoosing.remove(player);
        if (this.playersChoosing.isEmpty()) {
            this.assignInitialHand();
            this.goToNextState(new ChooseObjectiveState(this.game, this.objectiveDeck));
        }
    }

    @Override
    protected boolean isYourTurn(GameAction action) {
        return playersChoosing.contains(this.game.getPlayerByUsername(action.getUsername()));
    }

    /**
     * Deals three cards for the initial hand of eatch player.
     */
    private void assignInitialHand() {
        for (Player p : this.game.getPlayers()) {
            for (int i = 0; i < this.starterHandResources; i++)
                p.getHand().add(this.game.getResourceGameDeck().draw());

            for (int i = 0; i < this.starterHandGolds; i++)
                p.getHand().add(this.game.getGoldGameDeck().draw());

            this.game.triggerEvent(new HandEvent(p, p.getHand().stream().toList()));
        }
    }
}
