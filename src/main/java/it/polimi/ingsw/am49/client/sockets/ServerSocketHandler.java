package it.polimi.ingsw.am49.client.sockets;

import it.polimi.ingsw.am49.chat.ChatMSG;
import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.controller.CompleteGameInfo;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.messages.*;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.*;
import it.polimi.ingsw.am49.util.Log;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

/**
 * The ServerSocketHandler class is responsible for handling socket communication
 * between the client and the server, implementing the Server interface.
 */
public class ServerSocketHandler extends SocketHandler implements Server {
    private final Client client;

    /**
     * Constructs a ServerSocketHandler with the specified host, port, and client.
     *
     * @param host   the host to connect to
     * @param port   the port to connect to
     * @param client the client instance
     * @throws IOException if an I/O error occurs when creating the socket
     */
    public ServerSocketHandler(String host, int port, Client client) throws IOException {
        super(host, port);
        this.client = client;

        new Thread(() -> {
            try {
                this.startListeningForMessages();
            } catch (Exception e) {
                throw new RuntimeException("Failed to start listening for messages: " + e.getMessage(), e);
            }
        }).start();
    }

    /**
     * Fetches the list of rooms available on the server.
     *
     * @param client the client requesting the room list
     * @return a list of RoomInfo objects
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public List<RoomInfo> fetchRooms(Client client) throws RemoteException {
        try {
            return this.sendRequest(new FetchRoomsMTS(this.getUniqueId()), List.class);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    /**
     * Creates a new room on the server.
     *
     * @param client the client creating the room
     * @param roomName the name of the room
     * @param numPlayers the number of players in the room
     * @param creatorUsername the username of the room creator
     * @return the created RoomInfo object
     * @throws RemoteException if a remote communication error occurs
     * @throws AlreadyInRoomException if the client is already in a room
     * @throws CreateRoomException if there is an error creating the room
     */
    @Override
    public RoomInfo createRoom(Client client, String roomName, int numPlayers, String creatorUsername) throws RemoteException, AlreadyInRoomException, CreateRoomException {
        try {
            return this.sendRequest(new CreateRoomMTS(this.getUniqueId(), roomName, numPlayers, creatorUsername), RoomInfo.class);
        } catch (AlreadyInRoomException | CreateRoomException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    /**
     * Joins an existing room on the server.
     *
     * @param client the client joining the room
     * @param roomName the name of the room
     * @param username the username of the client
     * @return the RoomInfo object of the joined room
     * @throws RemoteException if a remote communication error occurs
     * @throws AlreadyInRoomException if the client is already in a room
     * @throws JoinRoomException if there is an error joining the room
     */
    @Override
    public RoomInfo joinRoom(Client client, String roomName, String username) throws RemoteException, AlreadyInRoomException, JoinRoomException, GameAlreadyStartedException {
        try {
            return this.sendRequest(new JoinRoomMTS(this.getUniqueId(), roomName, username), RoomInfo.class);
        } catch (AlreadyInRoomException | JoinRoomException | GameAlreadyStartedException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    /**
     * Marks the client as ready in the specified room.
     *
     * @param client the client readying up
     * @param color the color chosen by the client
     * @return the updated RoomInfo object
     * @throws RemoteException if a remote communication error occurs
     * @throws RoomException if there is an error with the room
     */
    @Override
    public RoomInfo readyUp(Client client, Color color) throws RemoteException, RoomException {
        try {
            return this.sendRequest(new ReadyUpMTS(this.getUniqueId(), color), RoomInfo.class);
        } catch (RoomException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    /**
     * Marks the client as not ready in the specified room.
     *
     * @param client the client readying down
     * @return the updated RoomInfo object
     * @throws RemoteException if a remote communication error occurs
     * @throws RoomException if there is an error with the room
     */
    @Override
    public RoomInfo readyDown(Client client) throws RemoteException, RoomException {
        try {
            return this.sendRequest(new ReadyDownMTS(this.getUniqueId()), RoomInfo.class);
        } catch (RoomException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    /**
     * Leaves the current room.
     *
     * @param client the client leaving the room
     * @return true if the client successfully left the room, false otherwise
     * @throws RemoteException if a remote communication error occurs
     * @throws RoomException if there is an error with the room
     */
    @Override
    public boolean leaveRoom(Client client) throws RemoteException, RoomException {
        try {
            return this.sendRequest(new LeaveRoomMTS(this.getUniqueId()), Boolean.class);
        } catch (RoomException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    /**
     * Executes a game action.
     *
     * @param c the client executing the action
     * @param action the game action to execute
     * @throws RemoteException if a remote communication error occurs
     * @throws InvalidActionException if the action is invalid
     * @throws NotYourTurnException if it is not the client's turn
     * @throws NotInGameException if the client is not in a game
     */
    @Override
    public void executeAction(Client c, GameAction action)
            throws RemoteException, InvalidActionException, NotYourTurnException, NotInGameException {
        try {
            this.sendRequest(new ExecuteActionMTS(this.getUniqueId(), action), Void.class);
        } catch (InvalidActionException | NotYourTurnException | NotInGameException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    /**
     * Reconnects the client to a game.
     *
     * @param c the client reconnecting
     * @param roomName the name of the room to reconnect to
     * @param username the username to be used by the client
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public CompleteGameInfo reconnect(Client c, String roomName, String username) throws RemoteException, JoinRoomException, AlreadyInRoomException {
        try {
            return this.sendRequest(new ReconnectMTS(this.getUniqueId(), roomName, username), CompleteGameInfo.class);
        } catch (AlreadyInRoomException | JoinRoomException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public void chatMessage(Client client, ChatMSG msg) throws RemoteException {
        try {
            this.sendRequest(new ChatMessageMTS(this.getUniqueId(), msg), Void.class);
        }catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    /**
     * Pings the server to check connectivity.
     *
     * @param c the client pinging the server
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void ping(Client c) throws RemoteException {
        try {
            this.sendRequest(new PingMTS(this.getUniqueId()), Void.class);
        }catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    /**
     * Gets the host address of the client.
     *
     * @return the client's host address
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public String getClientHostAddress() throws RemoteException {
        try {
            return this.sendRequest(new GetClientHostAddressMTS(this.getUniqueId()), String.class);
        }catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    /**
     * Handles push messages received from the server.
     *
     * @param pushMsg the push message to handle
     */
    @Override
    protected void handlePushMessage(SocketMessage pushMsg) {
        try {
            switch (pushMsg) {
                case RoomUpdateMTC params -> this.client.roomUpdate(params.roomInfo(), params.message());
                case ReceiveGameUpdateMTC params -> this.client.receiveGameUpdate(params.gameUpdate());
                case StartHeartbeatMTC ignored -> this.client.startHeartbeat();
                case StopHeartbeatMTC ignored -> this.client.stopHeartbeat();
                case ReceiveChatMessageMTC params -> this.client.receiveChatMessage(params.chatMSG());
                default -> throw new RemoteException("Unexpected message received: " + pushMsg);
            }
        } catch (RemoteException e) {
            Log.getLogger().severe("Error handling RMI call from server in handlePushMessage(...): " + e.getMessage());
        }
    }
}
