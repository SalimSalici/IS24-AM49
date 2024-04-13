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
    public void execute(MessageToServer msg) throws Exception {
        this.checkMsgValidity(msg);

        String username = msg.getUsername();
        Player newPlayer = new Player(username);
        this.game.getPlayers().add(newPlayer);

        if (this.game.getPlayers().size() >= maxPlayers) {
            this.startGame();
            this.game.setGameState(new ChooseStarterSideState(this.game));
        }

        // TODO: send broadcast message with list players who have joined so far
        // TODO: if last player joined, send message that announces the start of the game (with starter cards)
    }

    @Override
    protected boolean isYourTurn(MessageToServer msg) {
        return true;
    }

    private void startGame() {
        Collections.shuffle(this.game.getPlayers());
        GameDeck<StarterCard> starterDeck = DeckLoader.getInstance().getNewStarterDeck();
        for (Player p : this.game.getPlayers())
            p.setStarterCard(starterDeck.draw());
        this.game.setGameState(new ChooseStarterSideState(this.game));
    }
}
