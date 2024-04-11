package it.polimi.ingsw.am49.model.enumerations;

public enum CornerPosition {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT;

    public RelativePosition toRelativePosition(){
        RelativePosition relativePosition = null;
        switch (this){
            case TOP_RIGHT -> relativePosition = RelativePosition.TOP_RIGHT;
            case TOP_LEFT -> relativePosition = RelativePosition.TOP_LEFT;
            case BOTTOM_RIGHT -> relativePosition = RelativePosition.BOTTOM_RIGHT;
            case BOTTOM_LEFT -> relativePosition = RelativePosition.BOTTOM_LEFT;
        }
        return relativePosition;
    }
}
