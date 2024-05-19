package it.polimi.ingsw.am49.server;

import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.server.exceptions.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Server extends Remote {

    public void disconnectClient(Client client) throws RemoteException;

    public List<RoomInfo> fetchRooms(Client client) throws RemoteException;

    /**
     * This method lets clients create new rooms
     * @param client the client creating the room
     * @param roomName the name of the room
     * @param numPlayers the number of players that will eventually be playing in the room
     * @param creatorUsername the username of the createor of the room
     * @return if successfull, {@link RoomInfo} with info about the created room
     * @throws RemoteException if there is a network issue while invoking the remote method
     * @throws AlreadyInRoomException if the client making the request is associated with an already existing room
     * @throws CreateRoomException if (roomName or creatorUsername) is unvalid (too short or too long)
     *                                  or if numPlayers is invalid (too big or too small)
     */
    public RoomInfo createRoom(Client client, String roomName, int numPlayers, String creatorUsername)
            throws RemoteException, AlreadyInRoomException, CreateRoomException;

    /**
     * This method is used by clients to join existing rooms for which games have not already started
     * @param client   the client trying joining the room
     * @param roomName the room that the client is trying to join
     * @param username the username of the player trying to join the room
     * @return {@link RoomInfo} with room information
     * @throws RemoteException          if there is a network issue while invoking the remote method
     * @throws AlreadyInRoomException   if the client making the request is associated with an already existing room
     * @throws JoinRoomException        if the room could not be joined (username already taken, max player number reached...)
     * @throws IllegalArgumentException if username is invalid (too short or too long)
     */
    public RoomInfo joinRoom(Client client, String roomName, String username)
            throws RemoteException, AlreadyInRoomException, JoinRoomException, IllegalArgumentException;

    public RoomInfo readyUp(Client client, Color color) throws RemoteException;

    public RoomInfo readyDown(Client client) throws RemoteException;

    /**
     * This method is used by clients to leave the room they are in
     * @param client the client leaving the room
     * @return true if client left the room, false if the client was not in a room
     * @throws RemoteException if there is a network issue while invoking the remote method
     */
    public boolean leaveRoom(Client client) throws RemoteException;

    public void executeAction(Client c, GameAction action)
            throws RemoteException, NotInGameException, NotYourTurnException;

    public void reconnect(Client c, String gameName) throws RemoteException;

    public void ping(Client c) throws RemoteException;
}
