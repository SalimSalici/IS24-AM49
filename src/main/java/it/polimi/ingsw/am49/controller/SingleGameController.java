package it.polimi.ingsw.am49.controller;

import it.polimi.ingsw.am49.Client;
import it.polimi.ingsw.am49.messages.mtc.*;
import it.polimi.ingsw.am49.messages.mts.GameActionMTS;
import it.polimi.ingsw.am49.messages.mts.MessageToServer;
import it.polimi.ingsw.am49.messages.mts.MessageToServerType;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.actions.GameActionType;
import it.polimi.ingsw.am49.model.actions.JoinGameAction;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.cards.Card;
import it.polimi.ingsw.am49.model.cards.placeables.StarterCard;
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

        //clients.add(client);

        for(GameEventType eventType: GameEventType.values()){
            this.game.addEventListener(eventType, this);
        }

        sendMessge(new GameActionMTS(client, new JoinGameAction(client.getUserName())));
    }

    public void sendMessge(MessageToServer msg) throws Exception {
        switch (msg.getType()) {
            case MessageToServerType.GAME_ACTION: {
                if((((GameActionMTS)msg).getAction().getType()).equals(GameActionType.JOIN_GAME)) clients.add(msg.getClient());
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
            case GameEventType.STARTER_CARD_ASSIGNED_EVENT: {
                handleStarterCardAssigned((StarterCardAssignedEvent) event);
                break;
            }
            case GameEventType.COMMON_OBJECTIVES_DRAWN: {
                handleCommonObjectivesDrawn((CommonObjectivesDrawn) event);
                break;
            }
            case GameEventType.CHOOSABLE_OBJECTIVES_ASSIGNED_EVENT: {
                handleChoosableObjectivesAssigned((ChoosableObjectivesAssignedEvent) event);
                break;
            }
            case GameEventType.PERSONAL_OBJECTIVE_CHOSEN_EVENT:{
                handlePersonalObjectiveChosenEvent((PersonalObjectiveChosenEvent) event);
                break;
            }
            case GameEventType.CARD_PLACED_EVENT:{
                handleCardPlacedEvent((CardPlacedEvent) event);
                break;
            }
            case GameEventType.CARD_DRAWN_EVENT:{
                handleCardDrawnEvent((CardDrawnEvent) event);
                break;
            }
        }
    }

    private void handlePlayerJoinedEvent(PlayerJoinedEvent event){
        event.players().forEach(player -> {
            PlayerJoinedMTC mtc = new PlayerJoinedMTC(player.getUsername());
            this.broadCast(mtc);
        });
    }

    private void handlePlayerLeftEvent(PlayerLeftEvent event){
        clients.removeAll((event).players().stream().map(player -> getClientByUsername(player.getUsername())).toList());
        event.players().forEach(player -> {
            PlayerLeftMTC mtc = new PlayerLeftMTC(player.getUsername());
            this.broadCast(mtc);
        });
    }

    private void handleStarterCardAssigned(StarterCardAssignedEvent event){
        Map<Player, StarterCard> playersToStarterCard = event.playersToStarterCard();
        Client client;

        playersToStarterCard.forEach((player, card) -> {
            ChooseStarterSideMTC mtc = new ChooseStarterSideMTC(card.getId());
            this.getClientByUsername(player.getUsername()).sendMessage(mtc);
        });
    }

    private void handleCommonObjectivesDrawn(CommonObjectivesDrawn event){
        List<ObjectiveCard> commonObjectives = event.commonObjectives();
        Client client;

        CommonObjectivesMTC mtc = new CommonObjectivesMTC(commonObjectives.stream().map(Card::getId).toList());
        this.broadCast(mtc);
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

    private void handlePersonalObjectiveChosenEvent (PersonalObjectiveChosenEvent event){
        PersonalObjectiveChosenMTC mtc = new PersonalObjectiveChosenMTC(event.objectiveCard().getId());
        this.getClientByUsername(event.player().getUsername()).sendMessage(mtc);
    }

    private void handleCardPlacedEvent (CardPlacedEvent event){
        CardPlacedMTC mtc = new CardPlacedMTC(event.boardTile().getCard().getId(), event.player().getUsername());
        broadCast(mtc);
    }

    private void handleCardDrawnEvent (CardDrawnEvent event){
        CardDrawnMTC mtc = new CardDrawnMTC(event.card().getId());
        this.getClientByUsername(event.player().getUsername()).sendMessage(mtc);
    }

    private Client getClientByUsername(String username) {
        for (Client c : this.clients)
            if (c.getUserName().equals(username))
                return c;
        return null;
    }
}
