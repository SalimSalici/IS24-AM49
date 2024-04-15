package it.polimi.ingsw.am49.controller;

import it.polimi.ingsw.am49.messages.mtc.MessageToClient;
import it.polimi.ingsw.am49.messages.mts.JoinGameMTS;
import it.polimi.ingsw.am49.messages.mts.LeaveGameMTS;
import it.polimi.ingsw.am49.messages.mts.MessageToServer;
import it.polimi.ingsw.am49.messages.mts.MessageToServerType;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.events.ClientJoinedEvent;
import it.polimi.ingsw.am49.model.events.ClientLeftEvent;
import it.polimi.ingsw.am49.model.events.EventListener;
import it.polimi.ingsw.am49.model.events.GameEvent;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;

import java.util.ArrayList;
import java.util.List;

public class SingleGameController implements EventListener {

    private final Game game;
    private final List<Client> clients;

    public SingleGameController(int id, Client client, int numPlayers) throws Exception {
        this.clients = new ArrayList<>();
        this.game = new Game(id, numPlayers);

        this.game.addEventListener(GameEventType.PLAYER_JOINED_EVENT, this);
        this.game.addEventListener(GameEventType.PLAYER_LEFT_EVENT, this);

        sendMessge(new JoinGameMTS(client));
        clients.add(client);
    }

    public void sendMessge(MessageToServer msg) throws Exception {
        this.game.executeAction(msg);
    }

    public void broadCast(MessageToClient msg) {
        for (Client client : clients) {
            client.sendMessage(msg);
        }
    }

    @Override
    public void onEventTrigger(GameEvent event) {
        switch (event.getType()) {
            case GameEventType.PLAYER_JOINED_EVENT: {
                clients.add(((ClientJoinedEvent) event).client());
                break;
            }
            case GameEventType.PLAYER_LEFT_EVENT: {
                clients.remove(((ClientLeftEvent) event).client());
                break;
            }
        }
    }
}
