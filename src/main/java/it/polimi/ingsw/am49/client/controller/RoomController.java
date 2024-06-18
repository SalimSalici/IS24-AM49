package it.polimi.ingsw.am49.client.controller;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.RoomException;

import java.rmi.RemoteException;

public class RoomController extends ClientController {
    public RoomController(Server server, ClientApp client) {
        super(server, client);
    }

    public RoomInfo readyUp(Color color) throws RoomException, RemoteException {
        return this.server.readyUp(this.client, color);
    }

    public RoomInfo readyDown() throws RoomException, RemoteException {
        return this.server.readyDown(this.client);
    }

    public void leaveRoom() throws RoomException, RemoteException {
        this.server.leaveRoom(this.client);
    }
}
