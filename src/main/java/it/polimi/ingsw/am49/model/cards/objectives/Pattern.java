package it.polimi.ingsw.am49.model.cards.objectives;

import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.model.enumerations.RelativePosition;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Pattern implements Serializable {
    private final Resource pivotResource;
    private final List<Resource> resources;
    private final List<RelativePosition> positions;

    public Pattern(Resource pivotResource, List<Resource> resources, List<RelativePosition> positions) {
        this.pivotResource = pivotResource;
        this.resources = new LinkedList<>(resources);
        this.positions = new LinkedList<>(positions);
    }

    public Resource getPivotResource() {
        return pivotResource;
    }

    public List<Resource> getResources() {
        return Collections.unmodifiableList(this.resources);
    }

    public List<RelativePosition> getPositions() {
        return Collections.unmodifiableList(positions);
    }
}
