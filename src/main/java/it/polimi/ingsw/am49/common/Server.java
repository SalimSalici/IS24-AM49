package it.polimi.ingsw.am49.common;

import it.polimi.ingsw.am49.common.exceptions.*;
import it.polimi.ingsw.am49.common.gameupdates.ChatMSG;
import it.polimi.ingsw.am49.common.Client;
import it.polimi.ingsw.am49.common.reconnectioninfo.CompleteGameInfo;
import it.polimi.ingsw.am49.common.reconnectioninfo.RoomInfo;
import it.polimi.ingsw.am49.common.actions.GameAction;
import it.polimi.ingsw.am49.common.enumerations.Color;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * The Server interface defines the methods that the server must implement to support client interactions.
 * It extends the Remote interface to support RMI (Remote Method Invocation).
 */
public interface Server extends Remote {

    /**
     * Fetches the list of available rooms for the client.
     *
     * @param client the client requesting the room list
     * @return a list of RoomInfo objects representing the available rooms
     * @throws RemoteException if a remote communication error occurs
     */
    public List<RoomInfo> fetchRooms(Client client) throws RemoteException;

    /**
     * Creates a new room.
     *
     * @param client the client creating the room
     * @param roomName the name of the room
     * @param numPlayers the number of players that will eventually be playing in the room
     * @param creatorUsername the username of the creator of the room
     * @return if successful, RoomInfo with info about the created room
     * @throws RemoteException if there is a network issue while invoking the remote method
     * @throws AlreadyInRoomException if the client making the request is associated with an already existing room
     * @throws CreateRoomException if roomName or creatorUsername is invalid (too short or too long)
     *                             or if numPlayers is invalid (too big or too small)
     */
    public RoomInfo createRoom(Client client, String roomName, int numPlayers, String creatorUsername)
            throws RemoteException, AlreadyInRoomException, CreateRoomException;

    /**
     * Joins an existing room that has not already started.
     *
     * @param client the client trying to join the room
     * @param roomName the room that the client is trying to join
     * @param username the username of the player trying to join the room
     * @return RoomInfo with room information
     * @throws RemoteException if there is a network issue while invoking the remote method
     * @throws AlreadyInRoomException if the client making the request is associated with an already existing room
     * @throws JoinRoomException if the room could not be joined (username already taken, max player number reached...)
     * @throws GameAlreadyStartedException if the game in the room has already started
     * @throws IllegalArgumentException if username is invalid (too short or too long)
     */
    public RoomInfo joinRoom(Client client, String roomName, String username)
            throws RemoteException, AlreadyInRoomException, JoinRoomException, GameAlreadyStartedException;

    /**
     * Marks a client as ready to play.
     *
     * @param client the client that is ready
     * @param color the chosen color of the client
     * @return RoomInfo with updated room information
     * @throws RemoteException if a remote communication error occurs
     * @throws RoomException if there is an error in the room
     */
    public RoomInfo readyUp(Client client, Color color) throws RemoteException, RoomException;

    /**
     * Marks a client as no longer ready to play.
     *
     * @param client the client that is no longer ready
     * @return RoomInfo with updated room information
     * @throws RemoteException if a remote communication error occurs
     * @throws RoomException if there is an error in the room
     */
    public RoomInfo readyDown(Client client) throws RemoteException, RoomException;

    /**
     * Lets a client leave the room they are in.
     *
     * @param client the client leaving the room
     * @return true if client left the room, false if the client was not in a room
     * @throws RemoteException if a remote communication error occurs
     * @throws RoomException if there is an error in the room
     */
    public boolean leaveRoom(Client client) throws RemoteException, RoomException;

    /**
     * Executes a game action.
     *
     * @param client the client executing the action
     * @param action the game action to be executed
     * @throws RemoteException if a remote communication error occurs
     * @throws InvalidActionException if the action is invalid
     * @throws NotYourTurnException if it is not the player's turn
     * @throws NotInGameException if the client is not in a game
     */
    public void executeAction(Client client, GameAction action)
            throws RemoteException, InvalidActionException, NotYourTurnException, NotInGameException;

    /**
     * Reconnects a client to a game.
     *
     * @param client the client reconnecting
     * @param roomName the name of the room
     * @param username the username of the player
     * @return CompleteGameInfo with game information
     * @throws RemoteException if a remote communication error occurs
     * @throws JoinRoomException if the client cannot join the room
     * @throws AlreadyInRoomException if the client is already in a room
     */
    public CompleteGameInfo reconnect(Client client, String roomName, String username)
            throws RemoteException, JoinRoomException, AlreadyInRoomException;

    /**
     * Pings the server to check if it is still responsive.
     *
     * @param client the client pinging the server
     * @throws RemoteException if a remote communication error occurs
     */
    public void ping(Client client) throws RemoteException;

    /**
     * Gets the host address of the client.
     *
     * @return the client host address
     * @throws RemoteException if a remote communication error occurs
     */
    public String getClientHostAddress() throws RemoteException;

    /**
     * Sends a chat message.
     *
     * @param client the client sending the message
     * @param msg the chat message to be sent
     * @throws RemoteException if a remote communication error occurs
     */
    public void chatMessage(Client client, ChatMSG msg) throws RemoteException;
}
