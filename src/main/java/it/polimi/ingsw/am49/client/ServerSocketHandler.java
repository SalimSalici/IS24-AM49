package it.polimi.ingsw.am49.client;

import it.polimi.ingsw.am49.messages.mtc.LoginOutcomeMTC;
import it.polimi.ingsw.am49.messages.mtc.MessageToClientNew;
import it.polimi.ingsw.am49.messages.mts.LoginMTS;
import it.polimi.ingsw.am49.messages.mts.LogoutMTS;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.server.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class ServerSocketHandler implements Server {
    private final Socket socket;
    private final Client client;
    private final ObjectOutputStream objectOutputStream;
    private boolean shouldListen;

//    private final Queue<MessageToClientNew> mtcBuffer;

    public ServerSocketHandler(String host, int port, Client client) throws IOException {
        this.socket = new Socket(host, port);
        this.client = client;
        this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
        this.shouldListen = true;
//        this.mtcBuffer = new LinkedList<>();

        new Thread(() -> {
            try {
                this.startListeningForMessages();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void startListeningForMessages() throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(this.socket.getInputStream());

        while (this.shouldListen) {
            Object msg = objectInputStream.readObject();
            if (msg instanceof MessageToClientNew) {
                this.handleMessage((MessageToClientNew) msg);
//                synchronized (mtcBuffer) {
//                    mtcBuffer.add((MessageToClientNew) msg);
//                }
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
        switch (msg.getType()) {
            case LOGIN_OUTCOME -> this.client.loginOutcome(((LoginOutcomeMTC)msg).outcome());
        }
//        System.out.println("Received message from the server of type: " + msg.getType());
    }

    @Override
    public void login(Client client, String username) throws RemoteException {
        // TODO: maybe instead of try catch and "this.objectOutputStream.writeObject(new LoginMTS(username))"
        //       just implement a private method sendMessage(MessageToServer msg)
        try {
            this.objectOutputStream.writeObject(new LoginMTS(username));
        } catch (IOException e) {
            throw new RemoteException("Could not send login request to server through sockets");
        }

//        synchronized (mtcBuffer) {
//            while (mtcBuffer.isEmpty()) {
//                try {
//                    mtcBuffer.wait();
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//        }

    }

    @Override
    public void logout(Client client, String username) throws RemoteException {
        try {
            this.objectOutputStream.writeObject(new LogoutMTS(username));
        } catch (IOException e) {
            throw new RemoteException("Could not send logout request to server through sockets");
        }
    }

    @Override
    public void fetchLobbies(Client client) throws RemoteException {

    }

    @Override
    public void createGame(Client client, String gameName, int numPlayers) throws RemoteException {

    }

    @Override
    public void joinGame(Client client, String gameName) throws RemoteException {

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
}
