package it.polimi.ingsw.am49.server;

import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.server.exceptions.RoomException;
import it.polimi.ingsw.am49.util.IntervalTimer;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientHandler implements Client {
    private final Client client;
    private final Server server;
    private final ExecutorService executorService;
    private long lastHeartbeat;
    private final IntervalTimer hearbeatCheckerTimer;

    public ClientHandler(Client client, Server server) {
        this.client = client;
        this.server = server;
        this.executorService = Executors.newSingleThreadExecutor();
        this.hearbeatCheckerTimer = new IntervalTimer(this::checkHeartbeat, 10, 2000, TimeUnit.MILLISECONDS);
    }

    public void initializeHeartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
        try { this.client.startHeartbeat(); } catch (RemoteException ignored) {}
        this.hearbeatCheckerTimer.start();
    }

    public void heartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
    }

    public void roomUpdate(RoomInfo roomInfo, String message) {
        executorService.submit(() -> {
            try { client.roomUpdate(roomInfo, message); } catch (RemoteException ignored) {}
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

    public void startHeartbeat() {
        executorService.submit(() -> {
            try { client.startHeartbeat(); } catch (RemoteException ignored) {}
        });
    }

    @Override
    public void stopHeartbeat() {
        executorService.submit(() -> {
            try {
                client.stopHeartbeat();
            } catch (RemoteException e) {}
        });
    }

    public Client getClient() {
        return client;
    }

    private void checkHeartbeat() {
        if (System.currentTimeMillis() - this.lastHeartbeat > 5000) {
            System.out.println("Client failed heartbeat check. Disconnecting now.");
            this.close();
            try {
                this.server.leaveRoom(this.client);
            } catch (RemoteException | RoomException ignored) {}
        }
    }

    public void close() {
        this.stopHeartbeat();
        this.hearbeatCheckerTimer.stop();
        this.hearbeatCheckerTimer.shutdown();
    }
}

