package it.polimi.ingsw.am49.server;

import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.controller.RoomInfo;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.server.exceptions.*;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {

    /**
     * Logs in a client into the server, associating it to the supplied username.
     * @param client the client trying to log in
     * @param username the username chosen by the client
     * @return true if the login is successful, false if the username chosen is not available
     * @throws RemoteException if there is a network issue while invoking the remote method
     * @throws InvalidUsernameException if the chosen username is not valid (too short or too long)
     */
    public boolean login(Client client, String username) throws RemoteException, InvalidUsernameException;

    public void logout(Client client, String username) throws RemoteException;

    public void fetchLobbies(Client client) throws RemoteException;

    /**
     * This method lets clients create new rooms
     * @param client the client creating the room
     * @param roomName the name of the room
     * @param numPlayers the number of players that will eventually be playing in the room
     * @param creatorUsername the username of the createor of the room
     * @return true if game was created succesfully, false if room name was not available.
     * @throws RemoteException if there is a network issue while invoking the remote method
     * @throws AlreadyInRoomException if the client making the request is associated with an already existing room
     * @throws IllegalArgumentException if (roomName or creatorUsername) is unvalid (too short or too long)
     *                                  or if numPlayers is invalid (too big or too small)
     */
    public boolean createRoom(Client client, String roomName, int numPlayers, String creatorUsername)
            throws RemoteException, AlreadyInRoomException, IllegalArgumentException;

    /**
     * This method is used by clients to join existing rooms for which games have not already started
     * @param client   the client trying joining the room
     * @param roomName the room that the client is trying to join
     * @param username the username of the player trying to join the room
     * @return List of other usernames of the other players already in the room
     * @throws RemoteException          if there is a network issue while invoking the remote method
     * @throws AlreadyInRoomException   if the client making the request is associated with an already existing room
     * @throws JoinRoomException        if the room could not be joined (username already taken, max player number reached...)
     * @throws IllegalArgumentException if username is invalid (too short or too long)
     */
    public RoomInfo joinRoom(Client client, String roomName, String username)
            throws RemoteException, AlreadyInRoomException, JoinRoomException, IllegalArgumentException;

    public void executeAction(Client c, GameAction action)
            throws RemoteException, NotInGameException;

    public void reconnect(Client c, String gameName) throws RemoteException;

    public void ping(Client c) throws RemoteException;
}
