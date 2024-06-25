package it.polimi.ingsw.am49.client.controller;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.common.reconnectioninfo.RoomInfo;
import it.polimi.ingsw.am49.common.enumerations.Color;
import it.polimi.ingsw.am49.common.Server;
import it.polimi.ingsw.am49.common.exceptions.RoomException;

import java.rmi.RemoteException;
import java.util.HashMap;

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
     */
    public RoomInfo readyUp(Color color) throws RoomException {
        try {
            return this.server.readyUp(this.client, color);
        } catch (RemoteException e) {
            this.client.backToServerChoice();
            return new RoomInfo("fail", 2, new HashMap<>());
        }
    }

    /**
     * Signals the server that the client is no longer ready to proceed.
     * 
     * @return RoomInfo containing the updated room state
     * @throws RoomException if there is an issue with room operations
     */
    public RoomInfo readyDown() throws RoomException {
        try {
            return this.server.readyDown(this.client);
        } catch (RemoteException e) {
            this.client.backToServerChoice();
            return new RoomInfo("fail", 2, new HashMap<>());
        }
    }

    /**
     * Requests the server to remove the client from the room and stops the client's heartbeat.
     */
    public void leaveRoom() {
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
