package it.polimi.ingsw.am49.client.controller;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.RoomException;
import it.polimi.ingsw.am49.view.View;

import java.rmi.RemoteException;

/**
 * Controller class for room-related actions on the client side.
 */
public class RoomController extends ClientController {

    /**
     * Constructs a RoomController with a specified server and client.
     * 
     * @param server the server to interact with
     * @param client the client application
     */
    public RoomController(Server server, ClientApp client) {
        super(server, client);
    }

    /**
     * Signals the server that the client is ready to proceed.
     * 
     * @param color the color chosen by the client
     * @return RoomInfo containing the updated room state
     * @throws RoomException if there is an issue with room operations
     * @throws RemoteException if there is an issue with remote method invocation
     */
    public RoomInfo readyUp(Color color) throws RoomException, RemoteException {
        return this.server.readyUp(this.client, color);
    }

    /**
     * Signals the server that the client is no longer ready to proceed.
     * 
     * @return RoomInfo containing the updated room state
     * @throws RoomException if there is an issue with room operations
     * @throws RemoteException if there is an issue with remote method invocation
     */
    public RoomInfo readyDown() throws RoomException, RemoteException {
        return this.server.readyDown(this.client);
    }

    /**
     * Requests the server to remove the client from the room and stops the client's heartbeat.
     * 
     * @throws RoomException if there is an issue with leaving the room
     * @throws RemoteException if there is an issue with remote method invocation
     */
    public void leaveRoom() throws RoomException, RemoteException {
        try {
            new Thread(() -> {
                try {
                    server.leaveRoom(this.client);
                } catch (RemoteException | RoomException ignored) {}
            }).start();
            Thread.sleep(150);
            this.view.showMainMenu();
        } catch (InterruptedException ignored) {
        } finally {
            this.client.stopHeartbeat();
        }
    }
}
