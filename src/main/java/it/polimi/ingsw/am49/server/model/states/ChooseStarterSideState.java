package it.polimi.ingsw.am49.server.model.states;

import it.polimi.ingsw.am49.common.actions.ChooseStarterSideAction;
import it.polimi.ingsw.am49.common.actions.GameAction;
import it.polimi.ingsw.am49.common.actions.GameActionType;
import it.polimi.ingsw.am49.server.model.Game;
import it.polimi.ingsw.am49.server.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.server.model.cards.placeables.ResourceCard;
import it.polimi.ingsw.am49.server.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.server.model.decks.DeckLoader;
import it.polimi.ingsw.am49.server.model.decks.GameDeck;
import it.polimi.ingsw.am49.common.enumerations.GameStateType;
import it.polimi.ingsw.am49.server.model.events.*;
import it.polimi.ingsw.am49.server.model.players.Player;
import it.polimi.ingsw.am49.common.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.common.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.common.util.Log;
import it.polimi.ingsw.am49.server.model.cards.placeables.GoldCard;

import java.util.*;

public class ChooseStarterSideState extends GameState {

    /**
     * Used to keep track of the players that have already chosen the side of ther starter card.
     */
    private final Set<Player> playersChoosing;

    /**
     * The number of {@link ResourceCard}'s in the starter card;
     */
    private final int starterHandResources = 2;

    /**
     * The number of {@link GoldCard}'s in the starter card;
     */
    private final int starterHandGolds = 1;

    private final GameDeck<ObjectiveCard> objectiveDeck;

    /**
     * Constructor for ChoseStarterSideState.
     * @param game instance of the {@link Game} class.
     */
    public ChooseStarterSideState(Game game) {
        super(GameStateType.CHOOSE_STARTER_SIDE, game, Set.of(GameActionType.CHOOSE_STARTER_SIDE));
        this.playersChoosing = new HashSet<>(game.getPlayers());
        this.notYourTurnMessage = "You have already chosen the side of your starter card. You must wait for the other players.";
        this.objectiveDeck = DeckLoader.getInstance().getNewObjectiveDeck();
    }

    /**
     * Deals one starter card for every player in the game and notifies that the game state has changed to ChooseStarterSide.
     */
    @Override
    public void setUp() {
        Collections.shuffle(this.game.getPlayers());
        this.game.setCurrentPlayer(this.game.getStartingPlayer());
        this.game.triggerEvent(new PlayersOrderEvent(this.game.getPlayers()));

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

        this.game.triggerEvent(new GameStateChangedEvent(
                this.type,
                this.game.getCurrentPlayer(),
                this.game.getTurn(),
                this.game.getRound(),
                this.game.isEndGame(),
                this.game.isFinalRound()
        ));
    }

    /**
     * Handles the main logic for the {@link ChooseStarterSideState}.
     * @param action tells witch type of {@link GameAction} needs to be handled.
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
     * Deals three cards for the initial hand of each player.
     */
    private void assignInitialHand() {
        for (Player p : this.game.getPlayers()) {
            for (int i = 0; i < this.starterHandResources; i++)
                p.getHand().add(this.game.getResourceGameDeck().draw());

            for (int i = 0; i < this.starterHandGolds; i++)
                p.getHand().add(this.game.getGoldGameDeck().draw());

            this.game.triggerEvent(new HandEvent(p, p.getHand().stream().toList()));
        }

        this.game.triggerEvent(new DrawAreaEvent(
                this.game.getResourceGameDeck().size(),
                this.game.getGoldGameDeck().size(),
                this.game.getResourceGameDeck().peek().getResource(),
                this.game.getGoldGameDeck().peek().getResource(),
                List.of(this.game.getRevealedResources()),
                List.of(this.game.getRevealedGolds())
        ));
    }

    /**
     * Handles the disconnection of a player.
     * 
     * @param username the username of the player to be disconnected.
     */
    public void disconnectPlayer(String username) {
        Player player = this.game.getPlayerByUsername(username);
        if (player == null || !player.isOnline()) return;

        player.setIsOnline(false);

        if (this.playersChoosing.contains(player)) {
            boolean flipped = new Random().nextBoolean();
            try {
                this.execute(new ChooseStarterSideAction(username, flipped));
            } catch (NotYourTurnException | InvalidActionException e) {
                Log.getLogger().severe("Disconnect player anomaly... Exception message: " + e.getMessage());
            }
        }
    }
}
