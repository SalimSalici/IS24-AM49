package it.polimi.ingsw.am49.server;

import it.polimi.ingsw.am49.common.Server;
import it.polimi.ingsw.am49.common.gameupdates.ChatMSG;
import it.polimi.ingsw.am49.common.Client;
import it.polimi.ingsw.am49.common.reconnectioninfo.RoomInfo;
import it.polimi.ingsw.am49.common.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.common.exceptions.RoomException;
import it.polimi.ingsw.am49.common.util.IntervalTimer;
import it.polimi.ingsw.am49.common.util.Log;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Handles client-server interactions, including heartbeat checks and message forwarding.
 */
public class ClientHandler implements Client {
    private final Client client;
    private final Server server;
    private final ExecutorService executorService;
    private long lastHeartbeat;
    private final IntervalTimer hearbeatCheckerTimer;
    @SuppressWarnings("FieldCanBeLocal")
    private final int timeoutInSeconds = ServerConfig.clientHeartbeatTimeout;

    /**
     * Constructs a ClientHandler for managing a client's connection and interactions.
     * @param client The client to be managed.
     * @param server The server on which the client is hosted.
     */
    public ClientHandler(Client client, Server server) {
        this.client = client;
        this.server = server;
        this.executorService = Executors.newSingleThreadExecutor();
        this.hearbeatCheckerTimer = new IntervalTimer(this::checkHeartbeat, 10, 2000, TimeUnit.MILLISECONDS);
    }

    /**
     * Initializes the heartbeat mechanism to monitor client activity.
     */
    public void initializeHeartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
        try { this.client.startHeartbeat(); } catch (RemoteException ignored) {}
        this.hearbeatCheckerTimer.start();
    }

    /**
     * Updates the last heartbeat timestamp to the current time.
     */
    public void heartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
    }

    /**
     * Sends a room update to the client asynchronously.
     * @param roomInfo Information about the room.
     * @param message The message to be sent.
     */
    public void roomUpdate(RoomInfo roomInfo, String message) {
        executorService.submit(() -> {
            try {
                client.roomUpdate(roomInfo, message);
            } catch (RemoteException ignored) {
                this.disonnectAndClose();
            }
        });
    }

    /**
     * Receives a game update and forwards it to the client asynchronously.
     * @param gameUpdate The game update to be forwarded.
     */
    public void receiveGameUpdate(GameUpdate gameUpdate) {
        executorService.submit(() -> {
            try {
                client.receiveGameUpdate(gameUpdate);
            } catch (RemoteException e) {
                this.disonnectAndClose();
            }
        });
    }

    /**
     * Starts the heartbeat mechanism for the client asynchronously.
     */
    public void startHeartbeat() {
        executorService.submit(() -> {
            try {
                client.startHeartbeat();
            } catch (RemoteException ignored) {
                this.disonnectAndClose();
            }
        });
    }

    /**
     * Stops the client's heartbeat mechanism asynchronously.
     */
    @Override
    public void stopHeartbeat() {
        executorService.submit(() -> {
            try {
                client.stopHeartbeat();
            } catch (RemoteException ignored) {}
        });
    }

    /**
     * Receives a chat message and forwards it to the client asynchronously.
     * @param msg The chat message to be forwarded.
     */
    @Override
    public void receiveChatMessage(ChatMSG msg) {
        executorService.submit(() -> {
            try {
                client.receiveChatMessage(msg);
            } catch (RemoteException e) {
                this.disonnectAndClose();
            }
        });
    }

    /**
     * Returns the client associated with this handler.
     * @return The managed client.
     */
    public Client getClient() {
        return client;
    }

    /**
     * Instructs the server to remove the client from the room.
     */
    public void leaveRoom() {
        try {
            this.server.leaveRoom(this.client);
        } catch (RemoteException | RoomException ignored) {}
    }

    /**
     * Closes the client's connection and stops the heartbeat checker.
     */
    public void close() {
        this.stopHeartbeat();
        this.hearbeatCheckerTimer.stop();
        this.hearbeatCheckerTimer.shutdown();
    }

    /**
     * Disconnects the client and closes all associated resources.
     */
    public void disonnectAndClose() {
        System.out.println("Disconnect and close client.");
        this.close();
        this.leaveRoom();

    }

    /**
     * Checks if the client's last heartbeat was within the acceptable interval and disconnects the client if not.
     */
    private void checkHeartbeat() {
        if (System.currentTimeMillis() - this.lastHeartbeat > this.timeoutInSeconds * 1000) {
            Log.getLogger().warning("Client failed heartbeat check. Disconnecting now.");
            this.disonnectAndClose();
        }
    }
}
