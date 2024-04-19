package it.polimi.ingsw.am49.server;

import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.messages.ReturnMessage;
import it.polimi.ingsw.am49.messages.SocketMessage;
import it.polimi.ingsw.am49.messages.mtc.MessageToClient;
import it.polimi.ingsw.am49.messages.LoginMTS;
import it.polimi.ingsw.am49.server.exceptions.InvalidUsernameException;

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
            if (msg instanceof SocketMessage)
                this.handleMessage((SocketMessage) msg);
        }

        objectInputStream.close();
    }

    private void handleMessage(SocketMessage msg) throws IOException {
        switch (msg) {
            case LoginMTS loginMTS -> {
                Object returnValue;
                try {
                    returnValue = this.server.login(this, loginMTS.username());
                } catch (InvalidUsernameException | RemoteException e) {
                    returnValue = e;
                }
                this.objectOutputStream.writeObject(new ReturnMessage(msg.id(), returnValue));
            }
//            case LOGOUT_MTS -> {
//                this.server.logout(this, ((LogoutMTS) msg).username());
//
//            }
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
    public void receiveGameUpdate(MessageToClient msg) throws RemoteException {

    }

    @Override
    public void ping() throws RemoteException {

    }
}
