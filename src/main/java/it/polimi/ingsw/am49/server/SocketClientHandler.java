package it.polimi.ingsw.am49.server;

import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.messages.mtc.LoginOutcomeMTC;
import it.polimi.ingsw.am49.messages.mtc.MessageToClient;
import it.polimi.ingsw.am49.messages.mts.LoginMTS;
import it.polimi.ingsw.am49.messages.mts.LogoutMTS;
import it.polimi.ingsw.am49.messages.mts.MessageToServerNew;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.List;

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
            if (msg instanceof MessageToServerNew)
                this.handleMessage((MessageToServerNew) msg);
        }

        objectInputStream.close();
    }

    private void handleMessage(MessageToServerNew msg) throws RemoteException {
        switch (msg.getType()) {
            case LOGIN -> {
                this.server.login(this, ((LoginMTS) msg).username());
            }
            case LOGOUT -> {
                this.server.logout(this, ((LogoutMTS) msg).username());

            }
            default -> System.err.println("Received unknown type of message: " + msg.getType());
        }
    }

    private void disconnect() throws IOException {
        this.shouldListen = false;
        this.objectOutputStream.close();
        this.clientSocket.close();
        System.out.println("(Socket client disconnected)");
    }

    @Override
    public void loginOutcome(boolean outcome) throws RemoteException {
        LoginOutcomeMTC mtc = new LoginOutcomeMTC(outcome);
        try {
            this.objectOutputStream.writeObject(mtc);
        } catch (IOException e) {
            throw new RemoteException("Could not send login outcome to client through sockets");
        }
    }

    @Override
    public void lobbyList(List<String> lobbies) throws RemoteException {

    }

    @Override
    public void joinGame(MessageToClient msg) throws RemoteException {

    }

    @Override
    public void receiveGameUpdate(MessageToClient msg) throws RemoteException {

    }

    @Override
    public void ping() throws RemoteException {

    }
}
