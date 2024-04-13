package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.messages.mts.ChooseStarterSideMTS;
import it.polimi.ingsw.am49.messages.mts.MessageToServer;
import it.polimi.ingsw.am49.messages.mts.MessageToServerType;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.HashSet;
import java.util.Set;

public class ChooseStarterSideState extends GameState {

    private final Set<Player> players;
    protected ChooseStarterSideState(Game game) {
        super(GameStateType.CHOOSE_STARTER_SIDE, game, Set.of(MessageToServerType.CHOOSE_STARTER_SIDE));
        this.players = new HashSet<>(game.getPlayers());
    }

    @Override
    public void execute(MessageToServer msg) throws Exception {
        this.checkMsgValidity(msg);
        Player player = this.game.getPlayerByUsername(msg.getUsername());
        boolean flipped = ((ChooseStarterSideMTS)msg).getFlipped();
        player.chooseStarterSide(flipped);
        this.players.remove(player);

        if (this.players.isEmpty()) {
            this.game.assignInitialHand();
            this.game.setGameState(new PlaceCardState(this.game));
        }
    }

    @Override
    protected boolean isYourTurn(MessageToServer msg) {
        return players.contains(this.game.getPlayerByUsername(msg.getUsername()));
    }
}
