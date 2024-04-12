package it.polimi.ingsw.am49.model.cards.objectives;

import it.polimi.ingsw.am49.model.cards.Card;
import it.polimi.ingsw.am49.model.players.PlayerBoard;

import java.io.Serializable;

/**
 * Rapresents the cards that give points to the player once the game is over,
 * considering if and how many times the objective shown on the card is completed.
 */
public class ObjectiveCard extends Card implements Serializable {

    /**
     * The amount of points given to the player assuming the objective shown on the card
     * is accomplished a single time.
     */
    protected final int points;

    /**
     * Object used to call the strategy design pattern.
     */
    protected final ObjectivePointsStrategy pointsStrategy;

    /**
     * Costructs a new ObjectiveCard object with the specified parameters.
     *
     * @param id the unique identifier of the card
     * @param points The amount of points given to the player assuming the objective shown on the card
     *               is accomplished a single time.
     * @param pointsStrategy object used to call the strategy design pattern
     */
    public ObjectiveCard(int id, int points, ObjectivePointsStrategy pointsStrategy) {
        super(id);
        this.points = points;
        this.pointsStrategy = pointsStrategy;
    }

    /**
     * Costructs a copy of another {@link ObjectiveCard}.
     *
     * @param other the {@link ObjectiveCard} that is being copied
     */
    public ObjectiveCard(ObjectiveCard other) {
        super(other.id);
        this.points = other.points;
        this.pointsStrategy = other.pointsStrategy;
    }

    @Override
    public Card clone() {
        return new ObjectiveCard(this);
    }

    /**
     *
     * @return the object used to call the strategy pattern
     */
    public ObjectivePointsStrategy getPointsStrategy() {
        return this.pointsStrategy;
    }

    /**
     * Calculates the total amount of points given to the player, considering the number of times
     * the objective shown on the card is accomplished.
     *
     * @param playerBoard the whole player board, containing all the already placed cards
     * @return the total points the player gets
     */
    public int calculatePoints(PlayerBoard playerBoard) {
        return this.pointsStrategy.execute(playerBoard) * points;
    }

    /**
     *
     * @return The amount of points given to the player assuming the objective shown on the card
     *         is accomplished a single time.
     */
    public int getPoints() {
        return this.points;
    }
}
