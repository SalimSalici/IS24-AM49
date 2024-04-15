package it.polimi.ingsw.am49.model.actions;

public abstract class GameAction {

    protected final GameActionType type;
    protected final String username;

    public GameAction(GameActionType type, String username) {
        this.type = type;
        this.username = username;
    }

    public GameActionType getType() {
        return this.type;
    }

    public String getUsername() {
        return this.username;
    }
}
