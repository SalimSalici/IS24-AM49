package it.polimi.ingsw.am49.server;

import it.polimi.ingsw.am49.chat.ChatMSG;
import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.server.exceptions.RoomException;
import it.polimi.ingsw.am49.util.IntervalTimer;
import it.polimi.ingsw.am49.util.Log;

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
    @SuppressWarnings("FieldCanBeLocal")
    private final int timeoutInSeconds = 50000;

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
            try {
                client.roomUpdate(roomInfo, message);
            } catch (RemoteException ignored) {
                this.disonnectAndClose();
            }
        });
    }

    public void receiveGameUpdate(GameUpdate gameUpdate) {
        executorService.submit(() -> {
            try {
                client.receiveGameUpdate(gameUpdate);
            } catch (RemoteException e) {
                this.disonnectAndClose();
            }
        });
    }

    public void startHeartbeat() {
        executorService.submit(() -> {
            try {
                client.startHeartbeat();
            } catch (RemoteException ignored) {
                this.disonnectAndClose();
            }
        });
    }

    @Override
    public void stopHeartbeat() {
        executorService.submit(() -> {
            try {
                client.stopHeartbeat();
            } catch (RemoteException ignored) {}
        });
    }

    @Override
    public void receiveChatMessage(ChatMSG msg) throws RemoteException {
        executorService.submit(() -> {
            try {
                client.receiveChatMessage(msg);
            } catch (RemoteException e) {
                this.disonnectAndClose();
            }
        });
    }

    public Client getClient() {
        return client;
    }

    public void leaveRoom() {
        try {
            this.server.leaveRoom(this.client);
        } catch (RemoteException | RoomException ignored) {}
    }

    public void close() {
        this.stopHeartbeat();
        this.hearbeatCheckerTimer.stop();
        this.hearbeatCheckerTimer.shutdown();
    }

    public void disonnectAndClose() {
        System.out.println("Disconnect and close client.");
        this.close();
        this.leaveRoom();

    }

    private void checkHeartbeat() {
        if (System.currentTimeMillis() - this.lastHeartbeat > this.timeoutInSeconds * 1000) {
            Log.getLogger().warning("Client failed heartbeat check. Disconnecting now.");
            this.disonnectAndClose();
        }
    }
}

