package it.polimi.ingsw.am49.server;

import it.polimi.ingsw.am49.common.exceptions.*;
import it.polimi.ingsw.am49.common.messages.*;
import it.polimi.ingsw.am49.common.gameupdates.ChatMSG;
import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.common.reconnectioninfo.RoomInfo;
import it.polimi.ingsw.am49.common.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.common.util.Log;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.RemoteException;

/**
 * Handles client connections over sockets, managing message reception and response.
 */
public class SocketClientHandler implements Client {

    private final Socket clientSocket;
    private final Server server;
    private final ObjectOutputStream objectOutputStream;

    private boolean shouldListen = true;

    /**
     * Constructs a new SocketClientHandler for managing communication with a client socket.
     * @param clientSocket the client socket
     * @param server the server instance
     * @throws IOException if an I/O error occurs while opening the output stream
     */
    public SocketClientHandler(Socket clientSocket, Server server) throws IOException {
        this.clientSocket = clientSocket;
        this.server = server;
        this.objectOutputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
        new Thread(() -> {
            try {
                this.startListeningForMessages();
            } catch (Exception e) {
                try {
                    this.server.leaveRoom(this);
                } catch (RemoteException | RoomException ignored) {}
            } finally {
                System.out.println("Socket " + clientSocket.getRemoteSocketAddress() + " disconnected.");
            }
        }).start();

    }

    /**
     * Listens for messages from the client and processes them accordingly.
     * @throws IOException if an I/O error occurs while reading from the stream
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    private void startListeningForMessages() throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(this.clientSocket.getInputStream());

        while (this.shouldListen) {
            Object msg;
            try {
                msg = objectInputStream.readObject();
            } catch (SocketException ex) {
                this.disconnect();
                break;
            }
            if (msg instanceof SocketMessage)
                this.handleMessage((SocketMessage) msg);
        }

        objectInputStream.close();
    }

    /**
     * Handles the received socket message and performs actions based on the message type.
     * @param msg the message received from the client
     * @throws IOException if an I/O error occurs while writing to the output stream
     */
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
                this.writeToOutputStream(new ReturnMessage(msg.id(), returnValue));
            }
            case JoinRoomMTS params -> {
                Object returnValue;
                try {
                    returnValue = this.server.joinRoom(
                            this,
                            params.roomName(),
                            params.username()
                    );
                } catch (JoinRoomException | RemoteException | AlreadyInRoomException | IllegalArgumentException |
                         GameAlreadyStartedException e) {
                    returnValue = e;
                }
                this.writeToOutputStream(new ReturnMessage(msg.id(), returnValue));
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
                this.writeToOutputStream(new ReturnMessage(msg.id(), returnValue));
            }
            case ReadyDownMTS ignored -> {
                Object returnValue;
                try{
                    returnValue = this.server.readyDown(this);
                }catch (RemoteException | RoomException e){
                    returnValue = e;
                }
                this.writeToOutputStream(new ReturnMessage(msg.id(), returnValue));
            }
            case LeaveRoomMTS ignored -> {
                Object returnValue;
                try{
                    returnValue = this.server.leaveRoom(this);
                } catch (RemoteException | RoomException e) {
                    returnValue = e;
                }
                this.writeToOutputStream(new ReturnMessage(msg.id(), returnValue));
            }
            case ExecuteActionMTS params -> {
                Object returnValue = null;
                try {
                    this.server.executeAction(this, params.gameAction());
                } catch (NotInGameException | NotYourTurnException | InvalidActionException e) {
                    returnValue = e;
                }
                this.writeToOutputStream(new ReturnMessage(msg.id(), returnValue));
            }
            case FetchRoomsMTS ignored -> {
                Object returnValue;
                try {
                    returnValue = this.server.fetchRooms(this);
                } catch (Exception e) {
                    returnValue = e;
                }
                this.writeToOutputStream(new ReturnMessage(msg.id(), returnValue));
            }
            case ReconnectMTS params -> {
                Object returnValue;
                try {
                    returnValue = this.server.reconnect(this, params.roomName(), params.useraname());
                } catch (Exception e) {
                    returnValue = e;
                }
                this.writeToOutputStream(new ReturnMessage(msg.id(), returnValue));
            }
            case ChatMessageMTS params -> {
                Object returnValue = null;
                try {
                    this.server.chatMessage(this, params.chatMSG());
                } catch (Exception e) {
                    returnValue = e;
                }
                this.writeToOutputStream(new ReturnMessage(msg.id(), returnValue));
            }
            case PingMTS ignored -> {
                Object returnValue = null;
                try {
                    this.server.ping(this);
                } catch (Exception e) {
                    returnValue = e;
                }
                this.writeToOutputStream(new ReturnMessage(msg.id(), returnValue));
            }
            case GetClientHostAddressMTS ignored -> {
                Object returnValue;
                try {
                    returnValue = this.clientSocket.getInetAddress().getHostAddress();
                } catch (Exception e) {
                    returnValue = e;
                }
                this.writeToOutputStream(new ReturnMessage(msg.id(), returnValue));
            }
            default -> Log.getLogger().severe("Received unknown type of message: " + msg.getClass().getSimpleName());
        }
    }

    @Override
    public void roomUpdate(RoomInfo roomInfo, String message) throws RemoteException {
        try {
            this.writeToOutputStream(new RoomUpdateMTC(0, roomInfo, message));
        } catch (IOException e) {
            throw new RemoteException(
                    "SOCKETS: Could not send message to client through sockets (SocketClientHandler::playerLeftYourRoom)"
            );
        }
    }

    @Override
    public void receiveGameUpdate(GameUpdate gameUpdate) throws RemoteException {
        try {
            this.writeToOutputStream(new ReceiveGameUpdateMTC(0, gameUpdate));
        } catch (IOException e) {
            throw new RemoteException(
                    "SOCKETS: Could not send message to client through sockets (SocketClientHandler::receiveGameUpdate)"
            );
        }
    }

    @Override
    public void startHeartbeat() throws RemoteException {
        try {
            this.writeToOutputStream(new StartHeartbeatMTC(0));
        } catch (IOException e) {
            throw new RemoteException(
                    "SOCKETS: Could not send message to client through sockets (SocketClientHandler::startHeartbeat)"
            );
        }
    }

    @Override
    public void stopHeartbeat() throws RemoteException {
        try {
            this.writeToOutputStream(new StopHeartbeatMTC(0));
        } catch (IOException e) {
            throw new RemoteException(
                    "SOCKETS: Could not send message to client through sockets (SocketClientHandler::stopHeartbeat)"
            );
        }
    }

    @Override
    public void receiveChatMessage(ChatMSG msg) throws RemoteException {
        try {
            this.writeToOutputStream(new ReceiveChatMessageMTC(0, msg));
        } catch (IOException e) {
            throw new RemoteException(
                    "SOCKETS: Could not send message to client through sockets (SocketClientHandler::stopHeartbeat)"
            );
        }
    }

    /**
     * Writes an object to the output stream in a thread-safe manner.
     * @param obj the object to be written
     * @throws IOException if an I/O error occurs while writing to the stream
     */
    private void writeToOutputStream(Object obj) throws IOException {
        synchronized (this.objectOutputStream) {
            this.objectOutputStream.writeObject(obj);
        }
    }

    /**
     * Disconnects the client by closing the socket and associated streams.
     * @throws IOException if an I/O error occurs while closing the socket or streams
     */
    private void disconnect() throws IOException {
        this.shouldListen = false;
        this.objectOutputStream.close();
        this.clientSocket.close();
        try { this.server.leaveRoom(this); } catch (RoomException ignored) {}
    }
}
