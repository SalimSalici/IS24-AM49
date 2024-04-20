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
import it.polimi.ingsw.am49.model.events.PlayersOrderSetEvent;
import it.polimi.ingsw.am49.model.players.Player;
import java.util.*;

import java.util.Set;

/**
 * Rapresents the phase of the game in witch players are waiting for everybody to join the lobby and is still
 * allowed to leave the lobby without permanently occupying one of the limited player spots. Here is also asigned
 * to every player a random color from the {@link Color} enum.
 */
public class PregameState extends GameState {

    /**
     * Is the max number of players that this lobby is setup for, ass soon as it's reached the game starts automatically.
     * It has to be a number between 0 and the maximum number of players allowed by the game rules.
     * See the {//TODO: add link to config file where the maxAllowedNumber will be defined }.
     */
    private final int maxPlayers;

    /**
     * Constructor of the PregameState.
     * @param game istance of the {@link Game} class.
     * @param maxPlayers number of player expected in this game.
     */
    public PregameState(Game game, int maxPlayers) {
        super(GameStateType.PREGAME, game, Set.of(GameActionType.JOIN_GAME, GameActionType.LEAVE_GAME));
        this.maxPlayers = maxPlayers;
    }

    /**
     * Handles the main logic for the PregameState (joining and leaving of players and start of the game when
     * the desider number of participants is reached).
     * @param action tells witch type of {@link GameAction} neds to be handled.
     * @throws Exception if there are issues with the handling of joining and leaving of players.
     */
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
            this.game.setCurrentPlayer(this.game.getStartingPlayer());
            this.game.triggerEvent(new PlayersOrderSetEvent(this.game.getPlayers()));
            this.goToNextState(new ChooseStarterSideState(this.game));
        }
    }

    @Override
    protected boolean isYourTurn(GameAction action) {
        return true;
    }

    /**
     * Handles the logic for adding players to the game while in the lobby stage.
     * @param joinGameAction specifies the action tipe and the username of the player to add.
     * @throws Exception if there already is a player with that username in the lobby
     */
    private void addPlayer(JoinGameAction joinGameAction) throws Exception {
        String username = joinGameAction.getUsername();
        if (this.game.getPlayerByUsername(username) != null)
            throw new Exception("There already is a player with username '" + username + "'");

        Player newPlayer = new Player(username);
        this.game.getPlayers().add(newPlayer);
        this.game.triggerEvent(new PlayerJoinedEvent(this.game.getPlayers()));
    }

    /**
     * Handles the logic to remove a player from the game while in the lobby stage.
     * @param leaveGameAction specifies the action tipe and the username of the player to add.
     */
    private void removePlayer(LeaveGameAction leaveGameAction) {
        String username = leaveGameAction.getUsername();
        Player playerToRemove = this.game.getPlayerByUsername(username);
        this.game.getPlayers().remove(playerToRemove);
        this.game.triggerEvent(new PlayerLeftEvent(this.game.getPlayers(), playerToRemove));
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