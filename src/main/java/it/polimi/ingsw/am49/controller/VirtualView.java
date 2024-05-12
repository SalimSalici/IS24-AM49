package it.polimi.ingsw.am49.controller;

import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.controller.gameupdates.GameStartedUpdate;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.events.*;
import it.polimi.ingsw.am49.model.players.Player;

import java.rmi.RemoteException;
import java.util.LinkedHashMap;

public class VirtualView implements EventListener {
    private final Game game;
    private final Client client;
    private final String username;

    public VirtualView(Game game, Client client, String username) {
        this.game = game;
        this.client = client;
        this.username = username;

        for (GameEventType eventType : GameEventType.values())
            this.game.addEventListener(eventType, this);
    }

    @Override
    public void onEventTrigger(GameEvent event) {
        try {
            switch (event.getType()) {
                case STARTER_CARD_ASSIGNED_EVENT -> {
                    StarterCardAssignedEvent evt = (StarterCardAssignedEvent) event;
                    if (evt.player().getUsername().equals(this.username)) {
                        int starterCardId = evt.starterCard().getId();
                        LinkedHashMap<String, Color> players = new LinkedHashMap<>();
                        for (Player p : this.game.getPlayers())
                            players.put(p.getUsername(), p.getColor());
                        GameStartedUpdate update = new GameStartedUpdate(this.username, starterCardId, players);
                        this.client.receiveGameUpdate(update);
                    }
                }
                case CHOOSABLE_OBJECTIVES_EVENT -> {
                    ChoosableObjectivesEvent evt = (ChoosableObjectivesEvent) event;
                    if (evt.player().getUsername().equals(this.username))
                        this.client.receiveGameUpdate(event.toGameUpdate());
                }
                case HAND_UPDATE_EVENT -> {
                    HandEvent evt = (HandEvent) event;
                    if (evt.player().getUsername().equals(this.username))
                        this.client.receiveGameUpdate(event.toGameUpdate());
                    else
                        this.client.receiveGameUpdate(((HandEvent) event).toHiddenHandUpdate());
                }
                case PLAYERS_ORDER_SET_EVENT -> {
                    // discard... Player order will be communicated to the client with a GameStartedUpdate
                }
                default -> this.client.receiveGameUpdate(event.toGameUpdate());
            }
        } catch (RemoteException ex) {
            // TODO: handle exception properly
            System.err.println("Error sending game update to client with username " + this.username);
            ex.printStackTrace();
        }
    }
}

