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
import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.model.events.DrawAreaEvent;
import it.polimi.ingsw.am49.model.events.HandEvent;
import it.polimi.ingsw.am49.model.players.Player;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents the game state for drawing cards from a specific {@link it.polimi.ingsw.am49.model.decks.GameDeck} chosen by the player.
 * This state handles the drawing logic and updates their hand and draw area accordingly.
 */
public class DrawCardState extends GameState {

    /**
     * The current player in the game.
     */
    private final Player currentPlayer;

    /**
     * The game deck containing resource cards.
     */
    private final GameDeck<ResourceCard> resourceGameDeck;

    /**
     * The game deck containing gold cards.
     */
    private final GameDeck<GoldCard> goldGameDeck;

    /**
     * The revealed resource cards.
     */
    private final ResourceCard[] revealedResources;

    /**
     * The revealed gold cards.
     */
    private final GoldCard[] revealedGolds;

    /**
     * Constructs the DrawCardState.
     * @param game instance of the {@link Game} class.
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
     * Handles the drawing process from the chosen {@link DrawPosition}. After drawing the hand is updated and an event
     * is triggered.
     * The method also checks if the game is over, if not it moves to the next turn.
     * @param action tells which type of {@link GameAction} needs to be handled.
     * @throws InvalidActionException if the action is not supported by this state.
     * @throws NotYourTurnException if the player making the action is not the current player.
     */
    @Override
    public void execute(GameAction action) throws NotYourTurnException, InvalidActionException {
        this.checkActionValidity(action);

        Log.getLogger().info("Executing action " + action.toString());

        DrawCardAction drawCardAction = (DrawCardAction) action;
        DrawPosition drawPosition = drawCardAction.getDrawPosition();
        switch (drawPosition) {
            case RESOURCE_DECK -> this.drawFromDeck(this.currentPlayer, this.resourceGameDeck);
            case GOLD_DECK -> this.drawFromDeck(this.currentPlayer, this.goldGameDeck);
            case REVEALED -> {
                boolean found =
                        this.drawAndReplaceRevealedCard(this.currentPlayer, drawCardAction.getIdOfRevealedDrawn(), this.revealedResources, this.resourceGameDeck);
                if (!found)
                    found = this.drawAndReplaceRevealedCard(this.currentPlayer, drawCardAction.getIdOfRevealedDrawn(), this.revealedGolds, this.goldGameDeck);
                if (!found)
                    throw new InvalidActionException("Attempt to draw a card that is not in the draw area.");
            }
        }

        Resource resourceDeckPeek =
                this.resourceGameDeck.peek() != null ? this.resourceGameDeck.peek().getResource() : null;
        Resource goldDeckPeek =
                this.goldGameDeck.peek() != null ? this.goldGameDeck.peek().getResource() : null;

        this.game.triggerEvent(
                new DrawAreaEvent(
                        this.resourceGameDeck.size(),
                        this.goldGameDeck.size(),
                        resourceDeckPeek,
                        goldDeckPeek,
                        Arrays.stream(this.game.getRevealedResources()).collect(Collectors.toCollection(ArrayList::new)),
                        Arrays.stream(this.game.getRevealedGolds()).collect(Collectors.toCollection(ArrayList::new))
                )
        );
        this.game.triggerEvent(
                new HandEvent(currentPlayer, new ArrayList<>(currentPlayer.getHand()))
        );

        this.game.handleSwitchToNextTurn();
    }

    /**
     * Handles the disconnection of a player.
     * @param username the username of the player to be disconnected.
     */
    @Override
    public void disconnectPlayer(String username) {
        Player player = this.game.getPlayerByUsername(username);
        if (player == null || !player.isOnline()) return;

        player.setIsOnline(false);

        if (this.currentPlayer.equals(player))
            this.afkAction(player);
    }

    /**
     * Performs an action for a player who is AFK (away from keyboard).
     * @param player the player who is AFK.
     */
    private void afkAction(Player player) {
        try {
            if (!this.resourceGameDeck.isEmpty())
                this.execute(new DrawCardAction(player.getUsername(), DrawPosition.RESOURCE_DECK, 0));
            else if (!this.goldGameDeck.isEmpty()) {
                this.execute(new DrawCardAction(player.getUsername(), DrawPosition.GOLD_DECK, 0));
            }

            for (GoldCard revealedGold : this.revealedGolds)
                if (revealedGold != null) {
                    this.execute(new DrawCardAction(player.getUsername(), DrawPosition.REVEALED, revealedGold.getId()));
                    return;
                }

            for (ResourceCard revealedResource : this.revealedResources)
                if (revealedResource != null) {
                    this.execute(new DrawCardAction(player.getUsername(), DrawPosition.REVEALED, revealedResource.getId()));
                    return;
                }
        } catch (NotYourTurnException | InvalidActionException e) {
            Log.getLogger().severe("Disconnect player anomaly... Exception message: " + e.getMessage());
        }
    }

    /**
     * Draws a card from the specified deck and adds it to the player's hand.
     *
     * @param <T>    the type of card being drawn.
     * @param player the player who is drawing the card.
     * @param deck   the deck to draw from.
     * @throws InvalidActionException if the deck is empty.
     */
    private <T extends PlaceableCard> void drawFromDeck(Player player, GameDeck<T> deck) throws InvalidActionException {
        if (deck.size() <= 0)
            throw new InvalidActionException("Deck is empty.");
        T card = deck.draw();
        player.drawCard(card);
    }

    /**
     * Draws and replaces a revealed card, and adds the drawn card to the player's gabd.
     *
     * @param <T>           the type of card being drawn.
     * @param player        the player who is drawing the card.
     * @param cardId        the ID of the card to be drawn.
     * @param revealedCards the array of revealed cards.
     * @param deck          the deck to draw from.
     * @return true if the card was successfully drawn and replaced, false otherwise.
     * @throws InvalidActionException if the card cannot be drawn.
     */
    private <T extends PlaceableCard> boolean drawAndReplaceRevealedCard(Player player, int cardId, T[] revealedCards, GameDeck<T> deck) throws InvalidActionException {
        for (int i = 0; i < revealedCards.length; i++) {
            T current = revealedCards[i];
            if (current != null && current.getId() == cardId) {
                player.drawCard(current);
                revealedCards[i] = deck.draw();
                return true;
            }
        }
        return false;
    }

}
