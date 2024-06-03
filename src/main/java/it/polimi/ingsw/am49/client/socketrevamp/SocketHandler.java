package it.polimi.ingsw.am49.client.socketrevamp;

import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.messages.ReturnMessage;
import it.polimi.ingsw.am49.messages.SocketMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class SocketHandler {
    private final Socket socket;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;
    private final ConcurrentHashMap<Integer, CompletableFuture<Object>> returnValues = new ConcurrentHashMap<>();
    private final AtomicInteger uniqueId = new AtomicInteger();
    private volatile boolean shouldListen = true;

    public SocketHandler(String host, int port, Client client) throws IOException {
        this.socket = new Socket(host, port);
        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.input = new ObjectInputStream(socket.getInputStream());
    }

    public void startListeningForMessages() throws IOException, ClassNotFoundException {
        while (shouldListen) {
            Object msg = input.readObject();
            if (msg instanceof ReturnMessage returnMsg) {
                CompletableFuture<Object> future = returnValues.get(returnMsg.id());
                if (future != null) {
                    future.complete(returnMsg.returnValue());
                }
            } else if (msg instanceof SocketMessage pushMsg) {
                // TODO: handle
            }
        }
    }

    public <T> T sendRequest(SocketMessage request, Class<T> responseType) throws Exception {
        try {
            output.writeObject(request);
            CompletableFuture<Object> future = new CompletableFuture<>();
            returnValues.put(request.id(), future);
            Object result = future.get(3, TimeUnit.SECONDS);
            if (result instanceof Throwable) {
                throw (Exception) result;
            }
            return responseType.cast(result);
        } catch (IOException | InterruptedException | ExecutionException | TimeoutException | ClassCastException e) {
            throw new RemoteException("Failed to send request: " + e.getMessage(), e);
        }
    }

    public void handlePushMessage(SocketMessage msg) {}

    public int getUniqueId() {
        return uniqueId.getAndIncrement();
    }
}
