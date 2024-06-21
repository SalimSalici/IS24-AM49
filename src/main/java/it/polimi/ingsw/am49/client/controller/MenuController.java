package it.polimi.ingsw.am49.client.controller;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.controller.CompleteGameInfo;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.AlreadyInRoomException;
import it.polimi.ingsw.am49.server.exceptions.CreateRoomException;
import it.polimi.ingsw.am49.server.exceptions.GameAlreadyStartedException;
import it.polimi.ingsw.am49.server.exceptions.JoinRoomException;
import it.polimi.ingsw.am49.view.View;

import java.rmi.RemoteException;
import java.util.List;

public class MenuController extends ClientController {

    public MenuController(Server server, ClientApp client) {
        super(server, client);
    }

    public List<RoomInfo> fetchRooms() throws RemoteException {
        return this.server.fetchRooms(client);
    }

    public RoomInfo createRoom(String roomName, int numPlayers) throws AlreadyInRoomException, CreateRoomException, RemoteException {
        RoomInfo roomInfo = this.server.createRoom(client, roomName, numPlayers, ClientApp.getUsername());
        this.view.showRoom(roomInfo);
        return roomInfo;
    }

    public RoomInfo joinRoom(String roomName) throws AlreadyInRoomException, JoinRoomException, RemoteException, GameAlreadyStartedException {
        RoomInfo roomInfo = this.server.joinRoom(client, roomName, ClientApp.getUsername());
        this.view.showRoom(roomInfo);
        return roomInfo;
    }

    public void reconnect(String roomName) throws AlreadyInRoomException, JoinRoomException, RemoteException {
        CompleteGameInfo completeGameInfo = this.server.reconnect(client, roomName, ClientApp.getUsername());
        this.client.loadGame(completeGameInfo);
        this.view.showGame(this.client.getVirtualGame());
    }

    public void changeUsername(String username) {
        ClientApp.setUsername(username);
    }
}
