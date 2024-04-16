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
import it.polimi.ingsw.am49.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.model.events.*;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SingleGameController implements EventListener {

    private final Game game;
    private final List<Client> clients;

    public SingleGameController(int id, Client client, int numPlayers) throws Exception {
        this.clients = new ArrayList<>();
        this.game = new Game(id, numPlayers);

        for(GameEventType eventType: GameEventType.values()){
            this.game.addEventListener(eventType, this);
        }

        sendMessge(new GameActionMTS(client, new JoinGameAction(client.getUsername())));
    }

    public void sendMessge(MessageToServer msg) throws Exception {
        switch (msg.getType()) {
            case MessageToServerType.GAME_ACTION: {
                if ((((GameActionMTS)msg).getAction().getType()).equals(GameActionType.JOIN_GAME))
                    clients.add(msg.getClient());
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
                handleCommonObjectivesDrawn((CommonObjectivesDrawnEvent) event);
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
            case GameEventType.PLAYERS_ORDER_SET_EVENT:{
                handlePlayersOrderSetEvent((PlayersOrderSetEvent) event);
                break;
            }
            case GameEventType.CARD_PLACED_EVENT:{
                handleCardPlacedEvent((CardPlacedEvent) event);
                break;
            }
            case GameEventType.DRAW_AREA_UPDATE:{
                handleDrawAreaUpdateEvent((DrawAreaUpdateEvent) event);
                break;
            }
            case GameEventType.HAND_UPDATE_EVENT:{
                handleHandUpdateEvent((HandUpdateEvent) event);
                break;
            }
            case GameEventType.GAME_STATE_CHANGED_EVENT:{
                handleGameStateChangedEvent((GameStateChangedEvent) event);
                break;
            }
        }
    }

    private void handlePlayerJoinedEvent(PlayerJoinedEvent event){
        PlayerJoinedMTC mtc = new PlayerJoinedMTC(event.players().stream().map(Player::getUsername).toList());
        this.broadCast(mtc);
    }

    private void handlePlayerLeftEvent(PlayerLeftEvent event){
        clients.remove(this.getClientByUsername(event.playerWhoLeft().getUsername()));
        this.broadCast(new PlayerLeftMTC(
                event.remainingPlayers().stream().map(Player::getUsername).toList(),
                event.playerWhoLeft().getUsername()
        ));
    }

    private void handleStarterCardAssigned(StarterCardAssignedEvent event){
        Map<Player, StarterCard> playersToStarterCard = event.playersToStarterCard();

        playersToStarterCard.forEach((player, card) -> {
            StarterCardAssignedMTC mtc = new StarterCardAssignedMTC(card.getId());
            this.getClientByUsername(player.getUsername()).sendMessage(mtc);
        });
    }

    private void handleCommonObjectivesDrawn(CommonObjectivesDrawnEvent event){
        List<ObjectiveCard> commonObjectives = event.commonObjectives();

        CommonObjectivesMTC mtc = new CommonObjectivesMTC(commonObjectives.stream().map(Card::getId).toList());
        this.broadCast(mtc);
    }

    private void handleChoosableObjectivesAssigned(ChoosableObjectivesAssignedEvent event) {
        Map<Player, List<ObjectiveCard>> playersToObjectives = event.playersToObjectives();

        playersToObjectives.forEach((player, cards) -> {

            ChoosableObjectiveAssignedMTC mtc = new ChoosableObjectiveAssignedMTC(
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

    private void handlePlayersOrderSetEvent (PlayersOrderSetEvent event){
        PlayersOrderMTC mtc = new PlayersOrderMTC(event.playersOrder().stream().map(Player::getUsername).toList());
        this.broadCast(mtc);
    }

    private void handleCardPlacedEvent (CardPlacedEvent event){
        BoardTile boardTile = event.boardTile();
        CardPlacedMTC mtc = new CardPlacedMTC(
                event.player().getUsername(),
                boardTile.getCard().getId(),
                boardTile.getRow(),
                boardTile.getCol(),
                boardTile.getCard().isFlipped(),
                event.points()
        );
        broadCast(mtc);
    }

    private void handleDrawAreaUpdateEvent (DrawAreaUpdateEvent event){
        DrawAreaUpdateMTC mtc = new DrawAreaUpdateMTC(
                event.remainingResources(),
                event.remainingGolds(),
                event.revealedResources().stream().map(Card::getId).toList(),
                event.revealedGolds().stream().map(Card::getId).toList()
        );
        this.broadCast(mtc);
    }

    private void handleHandUpdateEvent (HandUpdateEvent event){
        HandUpdateMTC mtc = new HandUpdateMTC(event.player().getUsername(), event.hand().stream().map(PlaceableCard::getId).collect(Collectors.toSet()));
        broadCast(mtc);
    }

    private void handleGameStateChangedEvent (GameStateChangedEvent event){
        GameStateChangedMTC mtc = new GameStateChangedMTC(event.gameStateType(), event.round(), event.turn(), event.currentPlayer().getUsername());
        broadCast(mtc);
    }

    private Client getClientByUsername(String username) {
        for (Client c : this.clients)
            if (c.getUsername().equals(username))
                return c;
        return null;
    }
}
