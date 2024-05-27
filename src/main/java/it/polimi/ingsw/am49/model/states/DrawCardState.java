package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.model.actions.DrawCardAction;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.actions.GameActionType;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.cards.placeables.GoldCard;
import it.polimi.ingsw.am49.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.model.cards.placeables.ResourceCard;
import it.polimi.ingsw.am49.model.decks.GameDeck;
import it.polimi.ingsw.am49.model.enumerations.DrawPosition;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.events.DrawAreaEvent;
import it.polimi.ingsw.am49.model.events.HandEvent;
import it.polimi.ingsw.am49.model.players.Player;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.util.Log;

import java.util.List;
import java.util.Set;

/**
 * Rapresents the game state for drawing cards from a soecific {@link it.polimi.ingsw.am49.model.decks.GameDeck} choosen by the player.
 * This state handles the drawing logic and updates their hand accordingly.
 */
public class DrawCardState extends GameState {

    private final Player currentPlayer;
    private final GameDeck<ResourceCard> resourceGameDeck;
    private final GameDeck<GoldCard> goldGameDeck;
    private final ResourceCard[] revealedResources;
    private final GoldCard[] revealedGolds;

    /**
     * Constructs the DrawCardState.
     * @param game istance of the {@link Game} class.
     */
    protected DrawCardState(Game game) {
        super(GameStateType.DRAW_CARD, game, Set.of(GameActionType.DRAW_CARD));
        this.currentPlayer = game.getCurrentPlayer();
        this.resourceGameDeck = this.game.getResourceGameDeck();
        this.goldGameDeck = this.game.getGoldGameDeck();
        this.revealedResources = this.game.getRevealedResources();
        this.revealedGolds = this.game.getRevealedGolds();
    }

    /**
     * Handles the drawing process from the choosen {@link DrawPosition}. After drawing the hand is updated and am event
     * is triggered.
     * The method also checks if the game is over, if not it mooves to the next turn.
     * @param action tells witch type of {@link GameAction} neds to be handled.
     * @throws InvalidActionException if the action is not supported by this state.
     * @throws NotYourTurnException if the player making the action is not the current player.
     */
    @Override
    public void execute(GameAction action) throws NotYourTurnException, InvalidActionException {
        this.checkActionValidity(action);

        Log.getLogger().info("Executing action of player '" + action.getUsername() + "'");

        DrawCardAction drawCardAction = (DrawCardAction) action;
        DrawPosition drawPosition = drawCardAction.getDrawPosition();
        PlaceableCard drawnCard = null;
        switch (drawPosition) {
            case RESOURCE_DECK -> {
                drawnCard = this.resourceGameDeck.draw();
                this.currentPlayer.drawCard(drawnCard);
            }
            case GOLD_DECK -> {
                drawnCard = this.goldGameDeck.draw();
                this.currentPlayer.drawCard(drawnCard);
            }
            case REVEALED -> {
                boolean found = false;
                for (int i = 0; i < this.revealedResources.length && !found; i++) {
                    ResourceCard current = this.revealedResources[i];
                    if (current.getId() == drawCardAction.getIdOfRevealedDrawn()) {
                        this.currentPlayer.drawCard(current);
                        this.revealedResources[i] = this.resourceGameDeck.draw();
                        found = true;
                    }
                }
                for (int i = 0; i < this.revealedGolds.length && !found; i++) {
                    GoldCard current = this.revealedGolds[i];
                    if (current.getId() == drawCardAction.getIdOfRevealedDrawn()) {
                        this.currentPlayer.drawCard(current);
                        this.revealedGolds[i] = this.goldGameDeck.draw();
                        found = true;
                    }
                }
                if (!found)
                    throw new InvalidActionException("Attempt to draw a card that is not in the draw area.");
            }
        }

        this.game.triggerEvent(
                new DrawAreaEvent(
                        this.resourceGameDeck.size(),
                        this.goldGameDeck.size(),
                        this.resourceGameDeck.peek().getResource(),
                        this.goldGameDeck.peek().getResource(),
                        List.of(this.game.getRevealedResources()),
                        List.of(this.game.getRevealedGolds())
                )
        );
        this.game.triggerEvent(
                new HandEvent(currentPlayer, currentPlayer.getHand().stream().toList())
        );

        // Check if game is over
        if (this.game.isFinalRound() && this.currentPlayer.equals(this.game.getLastPlayer())) {
            this.goToNextState(new EndGameState(this.game));
            return;
        }

        // Game is not over, move on to the next turn
        this.handleSwitchToNextTurn();

        this.goToNextState(new PlaceCardState(this.game));
    }

    @Override
    protected boolean isYourTurn(GameAction action) {
        return this.currentPlayer.getUsername().equals(action.getUsername());
    }

    /**
     * Handles the switch to next turn logic by also checking if the EndGame or FinalRound has to start.
     */
    private void handleSwitchToNextTurn() {
        this.game.incrementTurn();

        if (this.currentPlayer.getPoints() > 20 || (this.resourceGameDeck.isEmpty() && this.goldGameDeck.isEmpty()))
            this.game.setEndGame(true);

        if (this.currentPlayer.equals(this.game.getLastPlayer())) {
            this.game.incrementRound();
            if (this.game.isEndGame())
                this.game.setFinalRound(true);
        }

        this.game.setCurrentPlayer(this.game.getNextPlayer());
    }
}
