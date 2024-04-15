package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.messages.mts.JoinGameMTS;
import it.polimi.ingsw.am49.messages.mts.LeaveGameMTS;
import it.polimi.ingsw.am49.messages.mts.MessageToServer;
import it.polimi.ingsw.am49.messages.mts.MessageToServerType;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.events.ClientLeftEvent;
import it.polimi.ingsw.am49.model.events.ClientJoinedEvent;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.Set;

public class PregameState extends GameState {

    private final int maxPlayers;

    public PregameState(Game game, int maxPlayers) {
        super(GameStateType.PREGAME, game, Set.of(MessageToServerType.JOIN_GAME, MessageToServerType.LEAVE_GAME));
        this.maxPlayers = maxPlayers;
    }

    @Override
    public void execute(MessageToServer msg) throws Exception {
        this.checkMsgValidity(msg);

        switch (msg.getType()) {
            case JOIN_GAME -> this.addPlayer((JoinGameMTS) msg);
            case LEAVE_GAME -> this.removePlayer((LeaveGameMTS) msg);
            default -> throw new Exception("Malformed message");
        }

        if (this.game.getPlayers().size() >= this.maxPlayers) {
            this.nextState = new ChooseStarterSideState(this.game);
            this.goToNextState();
        }
    }

    @Override
    protected boolean isYourTurn(MessageToServer msg) {
        return true;
    }

    private void addPlayer(JoinGameMTS joinGameMsg) throws Exception {
        String username = joinGameMsg.getUsername();
        if (this.game.getPlayerByUsername(username) != null)
            throw new Exception("There already is a player with username '" + username + "'");

        Player newPlayer = new Player(username);
        this.game.getPlayers().add(newPlayer);
        this.game.triggerEvent(new ClientJoinedEvent((joinGameMsg.getClient())));
    }

    private void removePlayer(LeaveGameMTS leaveGameMsg) {
        String username = leaveGameMsg.getUsername();
        this.game.getPlayers().remove(this.game.getPlayerByUsername(username));
        this.game.triggerEvent(new ClientLeftEvent(leaveGameMsg.getClient()));
    }
}
