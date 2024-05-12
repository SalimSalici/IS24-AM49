package it.polimi.ingsw.am49.server;

import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler implements Client {
    private final Client client;
    private final ExecutorService executorService;

    public ClientHandler(Client client) {
        this.client = client;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void roomUpdate(RoomInfo roomInfo, String message) {
        executorService.submit(() -> {
            try {
                client.roomUpdate(roomInfo, message);
            } catch (RemoteException e) {
                System.err.println("Error sending room update");
                e.printStackTrace();
            }
        });
    }

    public void receiveGameUpdate(GameUpdate gameUpdate) {
        executorService.submit(() -> {
            try {
                client.receiveGameUpdate(gameUpdate);
            } catch (RemoteException e) {
                System.err.println("Error sending game update");
                e.printStackTrace();
            }
        });
    }

    public void playerDisconnected(String username) {
        executorService.submit(() -> {
            try {
                client.playerDisconnected(username);
            } catch (RemoteException e) {
                System.err.println("Error notifying client of player disconnection");
                e.printStackTrace();
            }
        });
    }

    public void ping() {
        executorService.submit(() -> {
            try {
                client.ping();
            } catch (RemoteException e) {
                System.err.println("Error pinging client");
                e.printStackTrace();
            }
        });
    }

    public Client getClient() {
        return client;
    }
}

