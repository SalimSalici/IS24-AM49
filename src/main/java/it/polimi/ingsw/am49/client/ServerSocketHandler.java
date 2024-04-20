package it.polimi.ingsw.am49.client;

import it.polimi.ingsw.am49.controller.RoomInfo;
import it.polimi.ingsw.am49.messages.CreateRoomMTS;
import it.polimi.ingsw.am49.messages.JoinRoomMTS;
import it.polimi.ingsw.am49.messages.ReturnMessage;
import it.polimi.ingsw.am49.messages.mtc.MessageToClientNew;
import it.polimi.ingsw.am49.messages.LoginMTS;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.AlreadyInRoomException;
import it.polimi.ingsw.am49.server.exceptions.InvalidReturnTypeException;
import it.polimi.ingsw.am49.server.exceptions.InvalidUsernameException;
import it.polimi.ingsw.am49.server.exceptions.JoinRoomException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerSocketHandler implements Server {
    private final Socket socket;
    private final Client client;
    private final ObjectOutputStream objectOutputStream;
    private boolean shouldListen;
    private final Map<Integer, CompletableFuture<Object>> returnValues;
    private final AtomicInteger uniqueId;

    public ServerSocketHandler(String host, int port, Client client) throws IOException {
        this.socket = new Socket(host, port);
        this.client = client;
        this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
        this.shouldListen = true;
        this.returnValues = new ConcurrentHashMap<>();
        this.uniqueId = new AtomicInteger(0);

        new Thread(() -> {
            try {
                this.startListeningForMessages();
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void startListeningForMessages() throws IOException, ClassNotFoundException, InterruptedException {
        ObjectInputStream objectInputStream = new ObjectInputStream(this.socket.getInputStream());

        while (this.shouldListen) {
            Object msg = objectInputStream.readObject();
            if (msg instanceof ReturnMessage returnMsg) {

                System.out.println("MESSAGE WITH ID RECEIVED: " + returnMsg.id());

                synchronized (returnValues) {
                    CompletableFuture<Object> future = returnValues.get(returnMsg.id());
                    if (future != null) {
                        future.complete(returnMsg.returnValue());
                    } else {
                        CompletableFuture<Object> newFuture = CompletableFuture.completedFuture(returnMsg.returnValue());
                        returnValues.put(returnMsg.id(), newFuture);
                    }
                }

            } else if (msg instanceof MessageToClientNew mtcMessage) {
                this.handleMessage(mtcMessage);
            }
        }

        objectInputStream.close();
    }

    public void stopListening() throws IOException {
        this.shouldListen = false;
        this.objectOutputStream.close();
        this.socket.close();
    }

    // TODO: will be probably needed just to handle async messages (for example game updates)
    private void handleMessage(MessageToClientNew msg) throws RemoteException {
//        switch (msg.getType()) {
//            case LOGIN_OUTCOME -> this.client.loginOutcome(((LoginOutcomeMTC)msg).outcome());
//        }
//        System.out.println("Received message from the server of type: " + msg.getType());
    }

    @Override
    public boolean login(Client client, String username) throws RemoteException, InvalidUsernameException {
        int uniqueMessageId = this.getUniqueId();
        try {
            this.objectOutputStream.writeObject(new LoginMTS(uniqueMessageId, username));
        } catch (IOException e) {
            throw new RemoteException("SOCKETS: Could not send login request to server through sockets (Server::login)");
        }

        Object returnValue = this.waitForReturnValue(uniqueMessageId);

        return switch (returnValue) {
            case Boolean outcome -> outcome;
            case InvalidUsernameException e -> throw e;
            case Exception e -> throw new RemoteException(e.getMessage());
            default -> throw new InvalidReturnTypeException("Invalid return value type for Server::login method", returnValue);
        };
    }

    @Override
    public void logout(Client client, String username) throws RemoteException {
//        try {
//            this.objectOutputStream.writeObject(new LogoutMTS(username));
//        } catch (IOException e) {
//            throw new RemoteException("Could not send logout request to server through sockets");
//        }
    }

    @Override
    public void fetchLobbies(Client client) throws RemoteException {

    }

    @Override
    public boolean createRoom(Client client, String roomName, int numPlayers, String creatorUsername)
            throws RemoteException, AlreadyInRoomException, IllegalArgumentException {

        int uniqueMessageId = this.getUniqueId();
        try {
            this.objectOutputStream.writeObject(
                    new CreateRoomMTS(uniqueMessageId, roomName, numPlayers, creatorUsername)
            );
        } catch (IOException e) {
            throw new RemoteException("SOCKETS: Could not send login request to server through sockets (Server::createGame)");
        }

        Object returnValue = this.waitForReturnValue(uniqueMessageId);

        return switch (returnValue) {
            case Boolean outcome -> outcome;
            case IllegalArgumentException e -> throw e;
            case AlreadyInRoomException e -> throw e;
            case Exception e -> throw new RemoteException(e.getMessage());
            default -> throw new InvalidReturnTypeException("Invalid return value type for Server::createGame method", returnValue);
        };
    }

    @Override
    public RoomInfo joinRoom(Client client, String roomName, String username)
            throws RemoteException, AlreadyInRoomException, JoinRoomException, IllegalArgumentException {

        int uniqueMessageId = this.getUniqueId();
        try {
            this.objectOutputStream.writeObject(
                    new JoinRoomMTS(uniqueMessageId, roomName, username)
            );
        } catch (IOException e) {
            throw new RemoteException("SOCKETS: Could not send login request to server through sockets (Server::createGame)");
        }

        Object returnValue = this.waitForReturnValue(uniqueMessageId);

        return switch (returnValue) {
            case RoomInfo roomInfo -> roomInfo;
            case JoinRoomException e -> throw e;
            case AlreadyInRoomException e -> throw e;
            case IllegalArgumentException e -> throw e;
            case Exception e -> throw new RemoteException(e.getMessage());
            default -> throw new InvalidReturnTypeException("Invalid return value type for Server::createGame method", returnValue);
        };
    }

    @Override
    public void executeAction(Client c, GameAction action) throws RemoteException {

    }

    @Override
    public void reconnect(Client c, String gameName) throws RemoteException {

    }

    @Override
    public void ping(Client c) throws RemoteException {

    }

    /**
     * Returns a unique id for socket message identification.
     * The unique id is obtained by continuously incrementing an integer value. At some point
     * this value may reach Integer.MAX_INT, in which case, after being incremented again,
     * it will loop back to negative integers and keep going from there. This is not a problem, since the
     * client will (hopefully) never be waiting for billions of messages at the same time.
     * @return the uniquied id
     */
    private int getUniqueId() {
        return this.uniqueId.getAndIncrement();
    }

    private Object waitForReturnValue(int uniqueMessageId) throws RemoteException {
        CompletableFuture<Object> future = null;
        synchronized (this.returnValues) {
            future = this.returnValues.computeIfAbsent(
                    uniqueMessageId,
                    k -> new CompletableFuture<>()
            );
        }

        Object returnValue = null;
        try {
            // TODO: 3 seconds is arbitrary. Choose this value with a Settings file
            // This will block the thread until the value is available or 3 seconds have passed
            returnValue = future.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RemoteException("SOCKETS: Did not receive return value from server", e);
        } finally {
            this.returnValues.remove(uniqueMessageId);
        }
        return returnValue;
    }
}
