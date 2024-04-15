package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.messages.mts.JoinGameMTS;
import it.polimi.ingsw.am49.messages.mts.LeaveGameMTS;
import it.polimi.ingsw.am49.messages.mts.MessageToServer;
import it.polimi.ingsw.am49.messages.mts.MessageToServerType;
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

    /**
     * The number of {@link it.polimi.ingsw.am49.model.cards.placeables.ResourceCard}'s in the starter card;
     */
    private final int starterHandResources = 2;

    /**
     * The number of {@link it.polimi.ingsw.am49.model.cards.placeables.GoldCard}'s in the starter card;
     */
    private final int starterHandGolds = 1;

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
            assignColor(this.game.getPlayers());
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
        this.game.triggerEvent(new PlayerJoinedEvent(this.game.getPlayers()));
    }

    private void removePlayer(LeaveGameMTS leaveGameMsg) {
        String username = leaveGameMsg.getUsername();
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

    /**
     * This method assigns the starting card to every palayer.
     * @param players a list of all the players, is stored in {@link Game}.
     * @throws Exception if you have to many cards, see {@link Player}.
     */
    private void assignHand(List<Player> players) throws Exception {
        for (Player player : players) {
            for (int i = 0; i < starterHandResources; i++) {
                player.drawCard(this.game.getResourceGameDeck().draw());
            }

            for (int i = 0; i < starterHandGolds; i++) {
                player.drawCard(this.game.getGoldGameDeck().draw());
            }
        }
    }
}
