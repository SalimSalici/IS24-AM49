package it.polimi.ingsw.am49.controller.gameupdates;

public record PersonalObjectiveChosenUpdate(String username, int objective) implements GameUpdate {
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.PERSONAL_OBJECTIVE_CHOSEN_UPDATE;
    }
}