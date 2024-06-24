package it.polimi.ingsw.am49.client.controller;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.client.connectors.ConnectorType;
import it.polimi.ingsw.am49.common.reconnectioninfo.CompleteGameInfo;
import it.polimi.ingsw.am49.common.reconnectioninfo.RoomInfo;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.common.exceptions.AlreadyInRoomException;
import it.polimi.ingsw.am49.common.exceptions.CreateRoomException;
import it.polimi.ingsw.am49.common.exceptions.GameAlreadyStartedException;
import it.polimi.ingsw.am49.common.exceptions.JoinRoomException;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Controller for menu-related actions in the client application.
 */
public class MenuController extends ClientController {

    /**
     * Constructs a new MenuController with a specified server and client application.
     *
     * @param server the server instance to be used by this controller
     * @param client the client application instance
     */
    public MenuController(Server server, ClientApp client) {
        super(server, client);
    }

    /**
     * Fetches a list of available rooms from the server.
     *
     * @return a list of RoomInfo objects representing the available rooms
     * @throws RemoteException if a remote communication error occurs
     */
    public List<RoomInfo> fetchRooms() throws RemoteException {
        return this.server.fetchRooms(client);
    }

    /**
     * Creates a new room on the server.
     *
     * @param roomName the name of the room to create
     * @param numPlayers the number of players in the room
     * @return the RoomInfo object representing the newly created room
     * @throws AlreadyInRoomException if the client is already in another room
     * @throws CreateRoomException if the room cannot be created
     * @throws RemoteException if a remote communication error occurs
     */
    public RoomInfo createRoom(String roomName, int numPlayers) throws AlreadyInRoomException, CreateRoomException, RemoteException {
        RoomInfo roomInfo = this.server.createRoom(client, roomName, numPlayers, ClientApp.getUsername());
        this.view.showRoom(roomInfo);
        return roomInfo;
    }

    /**
     * Joins an existing room on the server.
     *
     * @param roomName the name of the room to join
     * @return the RoomInfo object representing the joined room
     * @throws AlreadyInRoomException if the client is already in another room
     * @throws JoinRoomException if there is an error joining the room
     * @throws RemoteException if a remote communication error occurs
     * @throws GameAlreadyStartedException if the game in the room has already started
     */
    public RoomInfo joinRoom(String roomName) throws AlreadyInRoomException, JoinRoomException, RemoteException, GameAlreadyStartedException {
        RoomInfo roomInfo = this.server.joinRoom(client, roomName, ClientApp.getUsername());
        this.view.showRoom(roomInfo);
        return roomInfo;
    }

    /**
     * Reconnects to a game in a room after a disconnection.
     *
     * @param roomName the name of the room to reconnect to
     * @throws AlreadyInRoomException if the client is already in another room
     * @throws JoinRoomException if there is an error reconnecting to the room
     * @throws RemoteException if a remote communication error occurs
     */
    public void reconnect(String roomName) throws AlreadyInRoomException, JoinRoomException, RemoteException {
        CompleteGameInfo completeGameInfo = this.server.reconnect(client, roomName, ClientApp.getUsername());
        this.client.loadGame(completeGameInfo);
        this.view.showGame(this.client.getVirtualGame());
    }

    /**
     * Changes the username of the client application.
     *
     * @param username the new username to set
     */
    public void changeUsername(String username) {
        ClientApp.setUsername(username);
    }

    public void connectToServer(String host, int port, ConnectorType connectorType) throws RemoteException {
        this.client.setServer(host, port, connectorType);

        if (ClientApp.getUsername() == null)
            this.view.showWelcome();
        else
            this.view.showMainMenu();
    }
}
