package it.polimi.ingsw.am49.model.cards.objectives;

import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.model.enumerations.RelativePosition;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a pattern in the game, consisting of a pivot resource and relative positions of other resources.
 */
public class Pattern implements Serializable {
    private final Resource pivotResource;
    private final List<Resource> resources;
    private final List<RelativePosition> positions;

    /**
     * Constructs a new Pattern with specified pivot resource, list of resources, and their relative positions.
     *
     * @param pivotResource The central resource of the pattern.
     * @param resources     The list of resources relative to the pivot.
     * @param positions     The list of relative positions corresponding to each resource.
     */
    public Pattern(Resource pivotResource, List<Resource> resources, List<RelativePosition> positions) {
        this.pivotResource = pivotResource;
        this.resources = new LinkedList<>(resources);
        this.positions = new LinkedList<>(positions);
    }

    /**
     * Returns the pivot resource of the pattern.
     *
     * @return The pivot resource.
     */
    public Resource getPivotResource() {
        return pivotResource;
    }

    /**
     * Returns an unmodifiable list of resources in the pattern.
     *
     * @return An unmodifiable list of resources.
     */
    public List<Resource> getResources() {
        return Collections.unmodifiableList(this.resources);
    }

    /**
     * Returns an unmodifiable list of relative positions of the resources in the pattern.
     *
     * @return An unmodifiable list of relative positions.
     */
    public List<RelativePosition> getPositions() {
        return Collections.unmodifiableList(positions);
    }
}
