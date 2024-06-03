package it.polimi.ingsw.am49.client.socketrevamp;

import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.messages.*;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.*;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

public class ServerSocketHandler implements Server {
    private final SocketHandler socketHandler;
    private final Client client;

    public ServerSocketHandler(String host, int port, Client client) throws IOException {
        this.client = client;
        this.socketHandler = new SocketHandler(host, port, client);

        new Thread(() -> {
            try {
                this.socketHandler.startListeningForMessages();
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
            return socketHandler.sendRequest(new FetchRoomsMTS(socketHandler.getUniqueId()), List.class);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public RoomInfo createRoom(Client client, String roomName, int numPlayers, String creatorUsername) throws RemoteException, AlreadyInRoomException, CreateRoomException {
        try {
            return socketHandler.sendRequest(new CreateRoomMTS(socketHandler.getUniqueId(), roomName, numPlayers, creatorUsername), RoomInfo.class);
        } catch (AlreadyInRoomException | CreateRoomException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public RoomInfo joinRoom(Client client, String roomName, String username) throws RemoteException, AlreadyInRoomException, JoinRoomException {
        try {
            return socketHandler.sendRequest(new JoinRoomMTS(socketHandler.getUniqueId(), roomName, username), RoomInfo.class);
        } catch (AlreadyInRoomException | JoinRoomException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public RoomInfo readyUp(Client client, Color color) throws RemoteException, RoomException {
        try {
            return socketHandler.sendRequest(new ReadyUpMTS(socketHandler.getUniqueId(), color), RoomInfo.class);
        } catch (RoomException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public RoomInfo readyDown(Client client) throws RemoteException, RoomException {
        try {
            return socketHandler.sendRequest(new ReadyDownMTS(socketHandler.getUniqueId()), RoomInfo.class);
        } catch (RoomException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public boolean leaveRoom(Client client) throws RemoteException, RoomException {
        try {
            return socketHandler.sendRequest(new LeaveRoomMTS(socketHandler.getUniqueId()), Boolean.class);
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
            socketHandler.sendRequest(new ExecuteActionMTS(socketHandler.getUniqueId(), action), Void.class);
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
}
