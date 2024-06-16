package it.polimi.ingsw.am49.controller;

import it.polimi.ingsw.am49.controller.gameupdates.GameStartedUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.cards.Card;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.model.events.*;
import it.polimi.ingsw.am49.model.players.Player;
import it.polimi.ingsw.am49.server.ClientHandler;
import it.polimi.ingsw.am49.util.Log;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class VirtualView implements EventListener {
    private final Game game;
    private final ClientHandler client;
    private final String username;

    public VirtualView(Game game, ClientHandler client, String username) {
        this.game = game;
        this.client = client;
        this.username = username;

        for (GameEventType eventType : GameEventType.values())
            this.game.addEventListener(eventType, this);
    }

    @Override
    public void onEventTrigger(GameEvent event) {
        GameUpdate update = event.toGameUpdate();
        if (update == null) return;
        switch (event.getType()) {
            case STARTER_CARD_ASSIGNED_EVENT -> {
                StarterCardAssignedEvent evt = (StarterCardAssignedEvent) event;
                if (evt.player().getUsername().equals(this.username)) {
                    int starterCardId = evt.starterCard().getId();

                    List<Integer> commonObjectivesIds = Arrays.stream(this.game.getCommonObjectives()).map(Card::getId).collect(Collectors.toCollection(java.util.ArrayList::new));

                    Resource resourceDeckTop = this.game.getResourceGameDeck().peek().getResource();
                    Resource goldDeckTop = this.game.getGoldGameDeck().peek().getResource();

                    List<Integer> revealedResourcesIds = Arrays.stream(this.game.getRevealedResources()).map(Card::getId).collect(Collectors.toCollection(java.util.ArrayList::new));
                    List<Integer> revealedGoldsIds = Arrays.stream(this.game.getRevealedGolds()).map(Card::getId).collect(Collectors.toCollection(java.util.ArrayList::new));

                    LinkedHashMap<String, Color> players = new LinkedHashMap<>();
                    for (Player p : this.game.getPlayers())
                        players.put(p.getUsername(), p.getColor());
                    GameStartedUpdate gameStartedUpdate
                            = new GameStartedUpdate(this.username, starterCardId, players, commonObjectivesIds, this.game.getResourceGameDeck().size(), this.game.getGoldGameDeck().size(), resourceDeckTop, goldDeckTop, revealedResourcesIds, revealedGoldsIds);
                    this.client.receiveGameUpdate(gameStartedUpdate);
                }
            }
            case CHOOSABLE_OBJECTIVES_EVENT -> {
                ChoosableObjectivesEvent evt = (ChoosableObjectivesEvent) event;
                if (evt.player().getUsername().equals(this.username))
                    this.client.receiveGameUpdate(update);
            }
            case HAND_UPDATE_EVENT -> {
                HandEvent evt = (HandEvent) event;
                if (evt.player().getUsername().equals(this.username))
                    this.client.receiveGameUpdate(update);
                else
                    this.client.receiveGameUpdate(((HandEvent) event).toHiddenHandUpdate());
            }
            default -> this.client.receiveGameUpdate(update);
        }
    }

    public void destroy() {
        for (GameEventType eventType : GameEventType.values())
            this.game.removeEventListener(eventType, this);
    }
}

