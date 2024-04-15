package it.polimi.ingsw.am49.controller;

import it.polimi.ingsw.am49.Client;
import it.polimi.ingsw.am49.messages.mtc.ChooseObjectiveMTC;
import it.polimi.ingsw.am49.messages.mtc.MessageToClient;
import it.polimi.ingsw.am49.messages.mts.GameActionMTS;
import it.polimi.ingsw.am49.messages.mts.MessageToServer;
import it.polimi.ingsw.am49.messages.mts.MessageToServerType;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.actions.JoinGameAction;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.cards.Card;
import it.polimi.ingsw.am49.model.events.*;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SingleGameController implements EventListener {

    private final Game game;
    private final List<Client> clients;

    public SingleGameController(int id, Client client, int numPlayers) throws Exception {
        this.clients = new ArrayList<>();
        this.game = new Game(id, numPlayers);

        this.game.addEventListener(GameEventType.PLAYER_JOINED_EVENT, this);
        this.game.addEventListener(GameEventType.PLAYER_LEFT_EVENT, this);

        sendMessge(new GameActionMTS(client, new JoinGameAction(client.getUserName())));
        clients.add(client);
    }

    public void sendMessge(MessageToServer msg) throws Exception {
        switch (msg.getType()) {
            case MessageToServerType.GAME_ACTION: {
                this.game.executeAction(((GameActionMTS)msg).getAction());
                break;
            }
        }
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
                handlePlayerJoinedEvent((PlayerJoinedEvent)event);
                break;
            }
            case GameEventType.PLAYER_LEFT_EVENT: {
                handlePlayerLeftEvent((PlayerLeftEvent)event);
                break;
            }
        }
    }

    private void handlePlayerJoinedEvent(PlayerJoinedEvent event){
        clients.addAll((event).players().stream().map(player -> getClientByUsername(player.getUsername())).toList());
    }

    private void handlePlayerLeftEvent(PlayerLeftEvent event){
        clients.removeAll((event).players().stream().map(player -> getClientByUsername(player.getUsername())).toList());
    }

    private void handleChoosableObjectivesAssigned(ChoosableObjectivesAssignedEvent event) {
        Map<Player, List<ObjectiveCard>> playersToObjectives = event.playersToObjectives();
        Client client;

        playersToObjectives.forEach((player, cards) -> {

            ChooseObjectiveMTC mtc = new ChooseObjectiveMTC(
                    cards.stream()
                            .map(Card::getId)
                            .toList()
            );
            this.getClientByUsername(player.getUsername()).sendMessage(mtc);
        });
    }

    private Client getClientByUsername(String username) {
        for (Client c : this.clients)
            if (c.getUserName().equals(username))
                return c;
        return null;
    }
}
