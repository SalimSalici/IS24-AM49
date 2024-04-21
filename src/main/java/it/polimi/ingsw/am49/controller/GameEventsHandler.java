package it.polimi.ingsw.am49.controller;

import it.polimi.ingsw.am49.controller.gameupdates.GameStateChangedUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.PlayerJoinedUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.PlayerLeftUpdate;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.events.*;

public class GameEventsHandler implements EventListener {
    private final Room room;
    private final Game game;

    public GameEventsHandler(Room room, Game game) {
        this.room = room;
        this.game = game;

        for (GameEventType eventType : GameEventType.values())
            this.game.addEventListener(eventType, this);
    }

    @Override
    public void onEventTrigger(GameEvent event) {
        System.out.println("Event received: " + event);
        this.room.broadcastGameUpdate(event.toGameUpdate());
//        switch (event.getType()) {
//            case PLAYER_JOINED_EVENT -> {
//                PlayerJoinedEvent playerJoinedEvent = (PlayerJoinedEvent) event;
//                GameUpdate update = new PlayerJoinedUpdate(playerJoinedEvent.playerWhoJoined().getUsername());
//                this.room.broadcastGameUpdate(update);
//            }
//            case PLAYER_LEFT_EVENT -> {
//                PlayerLeftEvent playerLeftEvent = (PlayerLeftEvent) event;
//                GameUpdate update = new PlayerLeftUpdate(playerLeftEvent.playerWhoLeft().getUsername());
//                this.room.broadcastGameUpdate(update);
//            }
//            case GAME_STATE_CHANGED_EVENT -> {
////                GameStateChangedEvent gameStateChangedEvent = (GameStateChangedEvent) event;
////                GameUpdate update = new GameStateChangedUpdate(
////                        gameStateChangedEvent.gameStateType(),
////                        gameStateChangedEvent.turn(),
////                        gameStateChangedEvent.round(),
////                        gameStateChangedEvent.currentPlayer().getUsername()
////                );
//                this.room.broadcastGameUpdate(event.toGameUpdate());
//            }
//        }
    }
}
