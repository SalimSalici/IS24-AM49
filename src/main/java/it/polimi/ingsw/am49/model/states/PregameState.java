package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.model.actions.JoinGameAction;
import it.polimi.ingsw.am49.model.actions.LeaveGameAction;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.actions.GameActionType;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.events.PlayerJoinedEvent;
import it.polimi.ingsw.am49.model.events.PlayerLeftEvent;
import it.polimi.ingsw.am49.model.players.Player;
import java.util.*;

import java.util.Set;

public class PregameState extends GameState {

    private final int maxPlayers;

    public PregameState(Game game, int maxPlayers) {
        super(GameStateType.PREGAME, game, Set.of(GameActionType.JOIN_GAME, GameActionType.LEAVE_GAME));
        this.maxPlayers = maxPlayers;
    }

    @Override
    public void execute(GameAction action) throws Exception {
        this.checkActionValidity(action);

        switch (action.getType()) {
            case JOIN_GAME -> this.addPlayer((JoinGameAction) action);
            case LEAVE_GAME -> this.removePlayer((LeaveGameAction) action);
            default -> throw new Exception("Malformed message");
        }

        if (this.game.getPlayers().size() >= this.maxPlayers) {
            this.assignColor(this.game.getPlayers());
            Collections.shuffle(this.game.getPlayers());
            this.goToNextState(new ChooseStarterSideState(this.game));
        }
    }

    @Override
    protected boolean isYourTurn(GameAction action) {
        return true;
    }

    private void addPlayer(JoinGameAction joinGameAction) throws Exception {
        String username = joinGameAction.getUsername();
        if (this.game.getPlayerByUsername(username) != null)
            throw new Exception("There already is a player with username '" + username + "'");

        Player newPlayer = new Player(username);
        this.game.getPlayers().add(newPlayer);
        this.game.triggerEvent(new PlayerJoinedEvent(this.game.getPlayers()));
    }

    private void removePlayer(LeaveGameAction leaveGameAction) {
        String username = leaveGameAction.getUsername();
        this.game.getPlayers().remove(this.game.getPlayerByUsername(username));
        this.game.triggerEvent(new PlayerLeftEvent(this.game.getPlayers()));
    }

    /**
     * This method assigns a random {@link Color} to every player in the game.
     * @param players is the list of players in {@link Game}
     */
    private void assignColor(List<Player> players){
        Color[] colors = Color.values();
        List<Color> colorsList = new ArrayList<>(Arrays.asList(colors));
        Collections.shuffle(colorsList);

        if(players.size() > colorsList.size()) throw new IllegalArgumentException("There aren't enough colors");

        for(int i = 0; i < players.size(); i++){
            players.get(i).setColor(colorsList.get(i));
        }
    }
}