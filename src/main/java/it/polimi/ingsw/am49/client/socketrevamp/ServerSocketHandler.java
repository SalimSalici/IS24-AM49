package it.polimi.ingsw.am49.client.socketrevamp;

import it.polimi.ingsw.am49.client.Client;
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

public class ServerSocketHandler extends SocketHandler implements Server {
    private final Client client;

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

    @Override
    public void disconnect(Client client) throws RemoteException {
        // Implementation details for disconnecting
    }

    @Override
    public List<RoomInfo> fetchRooms(Client client) throws RemoteException {
        try {
            return this.sendRequest(new FetchRoomsMTS(this.getUniqueId()), List.class);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

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

    @Override
    public RoomInfo joinRoom(Client client, String roomName, String username) throws RemoteException, AlreadyInRoomException, JoinRoomException {
        try {
            return this.sendRequest(new JoinRoomMTS(this.getUniqueId(), roomName, username), RoomInfo.class);
        } catch (AlreadyInRoomException | JoinRoomException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

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

    @Override
    public void reconnect(Client c, String gameName) throws RemoteException {
        // Implement as needed
    }

    @Override
    public void ping(Client c) throws RemoteException {
        // Implement as needed
    }

    @Override
    public String getClientHostAddress() throws RemoteException {
        // Implement as needed
        return null;
    }

    @Override
    protected void handlePushMessage(SocketMessage pushMsg) {
        try {
            switch (pushMsg) {
                case RoomUpdateMTC params ->
                        this.client.roomUpdate(params.roomInfo(), params.message());
                case ReceiveGameUpdateMTC params -> {
                    this.client.receiveGameUpdate(params.gameUpdate());
                }
                default -> throw new RemoteException("Unexpected message received: " + pushMsg);
            }
        } catch (RemoteException e) {
            Log.getLogger().severe("Error handling RMI call from server in handlePushMessage(...): " + e.getMessage());
        }
    }
}
