package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.messages.mts.MessageToServer;
import it.polimi.ingsw.am49.messages.mts.MessageToServerType;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.model.decks.DeckLoader;
import it.polimi.ingsw.am49.model.decks.GameDeck;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.Collections;
import java.util.Set;

public class PregameState extends GameState {

    private final int maxPlayers;
    public PregameState(Game game, int maxPlayers) {
        super(GameStateType.SETUP, game, Set.of(MessageToServerType.JOIN_GAME));
        this.maxPlayers = maxPlayers;
    }

    @Override
    public void setUp() {}

    @Override
    public void execute(MessageToServer msg) throws Exception {
        this.checkMsgValidity(msg);

        String username = msg.getUsername();
        Player newPlayer = new Player(username);
        this.game.getPlayers().add(newPlayer);

        if (this.game.getPlayers().size() >= maxPlayers) {
            this.nextState = new ChooseStarterSideState(this.game);
            this.goToNextState();
        }
    }

    @Override
    protected boolean isYourTurn(MessageToServer msg) {
        return true;
    }
}
