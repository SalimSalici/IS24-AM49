package it.polimi.ingsw.am49.common;

import it.polimi.ingsw.am49.common.gameupdates.ChatMSG;
import it.polimi.ingsw.am49.common.reconnectioninfo.RoomInfo;
import it.polimi.ingsw.am49.common.gameupdates.GameUpdate;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The Client interface defines the methods that a client must implement to communicate with the server.
 * It extends the Remote interface to support RMI (Remote Method Invocation).
 */
public interface Client extends Remote {

    /**
     * Updates the client with the current room information and a message.
     *
     * @param roomInfo the current room information
     * @param message the message to be sent to the client
     * @throws RemoteException if a remote communication error occurs
     */
    public void roomUpdate(RoomInfo roomInfo, String message) throws RemoteException;

    /**
     * Receives the gameUpdates {@link it.polimi.ingsw.am49.common.gameupdates}
     *
     * @param gameUpdate the game update to be sent to the client
     * @throws RemoteException if a remote communication error occurs
     */
    public void receiveGameUpdate(GameUpdate gameUpdate) throws RemoteException;

    /**
     * Starts the heartbeat mechanism to ensure the client is still connected.
     *
     * @throws RemoteException if a remote communication error occurs
     */
    public void startHeartbeat() throws RemoteException;

    /**
     * Stops the heartbeat mechanism.
     *
     * @throws RemoteException if a remote communication error occurs
     */
    public void stopHeartbeat() throws RemoteException;

    /**
     * Receives a chat message.
     *
     * @param msg the chat message to be received
     * @throws RemoteException if a remote communication error occurs
     */
    public void receiveChatMessage(ChatMSG msg) throws RemoteException;
}
