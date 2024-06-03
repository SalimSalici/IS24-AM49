package it.polimi.ingsw.am49.server;

import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.messages.*;
import it.polimi.ingsw.am49.server.exceptions.*;
import it.polimi.ingsw.am49.util.Log;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.RemoteException;

public class SocketClientHandler implements Client {

    private final Socket clientSocket;
    private final Server server;
    private final ObjectOutputStream objectOutputStream;

    private boolean shouldListen = true;

    public SocketClientHandler(Socket clientSocket, Server server) throws IOException {
        this.clientSocket = clientSocket;
        this.server = server;
        this.objectOutputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
        new Thread(() -> {
            try {
                this.startListeningForMessages();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void startListeningForMessages() throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(this.clientSocket.getInputStream());

        while (this.shouldListen) {
            Object msg = null;
            try {
                msg = objectInputStream.readObject();
            } catch (SocketException ex) {
                this.disconnect();
                break;
            }
            Log.getLogger().info("Received message from " + this.clientSocket.getRemoteSocketAddress() + " - " + msg);
            if (msg instanceof SocketMessage)
                this.handleMessage((SocketMessage) msg);
        }

        objectInputStream.close();
    }

    private void handleMessage(SocketMessage msg) throws IOException {
        switch (msg) {

            case CreateRoomMTS params -> {
                Object returnValue;
                try {
                    returnValue = this.server.createRoom(
                            this,
                            params.roomName(),
                            params.numPlayers(),
                            params.creatorUsername()
                    );
                } catch (AlreadyInRoomException | RemoteException | CreateRoomException e) {
                    returnValue = e;
                }
                this.objectOutputStream.writeObject(new ReturnMessage(msg.id(), returnValue));
            }
            case JoinRoomMTS params -> {
                Object returnValue;
                try {
                    returnValue = this.server.joinRoom(
                            this,
                            params.roomName(),
                            params.username()
                    );
                } catch (JoinRoomException | RemoteException | AlreadyInRoomException | IllegalArgumentException e) {
                    returnValue = e;
                }
                this.objectOutputStream.writeObject(new ReturnMessage(msg.id(), returnValue));
            }
            case ReadyUpMTS params -> {
                Object returnValue;
                try {
                    returnValue = this.server.readyUp(
                            this,
                            params.color()
                    );
                } catch (RemoteException | RoomException e) {
                    returnValue = e;
                }
                this.objectOutputStream.writeObject(new ReturnMessage(msg.id(), returnValue));
            }
            case ReadyDownMTS params -> {
                Object returnValue;
                try{
                    returnValue = this.server.readyDown(this);
                }catch (RemoteException | RoomException e){
                    returnValue = e;
                }
                this.objectOutputStream.writeObject(new ReturnMessage(msg.id(), returnValue));
            }
            case LeaveRoomMTS params -> {
                Object returnValue;
                try{
                    returnValue = this.server.leaveRoom(this);
                } catch (RemoteException | RoomException e) {
                    returnValue = e;
                }
                this.objectOutputStream.writeObject(new ReturnMessage(msg.id(), returnValue));
            }
            case ExecuteActionMTS params -> {
                Object returnValue = null;
                try {
                    this.server.executeAction(this, params.gameAction());
                } catch (NotInGameException | NotYourTurnException | InvalidActionException e) {
                    returnValue = e;
                }
                this.objectOutputStream.writeObject(new ReturnMessage(msg.id(), returnValue));
            }
            case FetchRoomsMTS params -> {
                Object returnValue;
                try {
                    returnValue = this.server.fetchRooms(this);
                } catch (Exception e) {
                    returnValue = e;
                }
                this.objectOutputStream.writeObject(new ReturnMessage(msg.id(), returnValue));
            }
            default -> System.err.println("Received unknown type of message: " + msg.getClass().getSimpleName());
        }
    }

    private void disconnect() throws IOException {
        this.shouldListen = false;
        this.objectOutputStream.close();
        this.clientSocket.close();
        System.out.println("(Socket client disconnected)");
    }

    @Override
    public void roomUpdate(RoomInfo roomInfo, String message) throws RemoteException {
        try {
            this.objectOutputStream.writeObject(
                    new RoomUpdateMTC(0, roomInfo, message)
            );
        } catch (IOException e) {
            throw new RemoteException(
                    "SOCKETS: Could not send message to client through sockets (SocketClientHandler::playerLeftYourRoom)"
            );
        }
    }

    @Override
    public void receiveGameUpdate(GameUpdate gameUpdate) throws RemoteException {
        try {
            this.objectOutputStream.writeObject(
                    new ReceiveGameUpdateMTC(0, gameUpdate)
            );
        } catch (IOException e) {
            throw new RemoteException(
                    "SOCKETS: Could not send message to client through sockets (SocketClientHandler::receiveGameUpdate)"
            );
        }
    }

    @Override
    public void playerDisconnected(String username) throws RemoteException {

    }

    @Override
    public void ping() throws RemoteException {

    }
}
