package it.polimi.ingsw.am49.client.sockets;

import it.polimi.ingsw.am49.common.messages.ReturnMessage;
import it.polimi.ingsw.am49.common.messages.SocketMessage;
import it.polimi.ingsw.am49.common.util.Log;

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

/**
 * The SocketHandler class is responsible for managing socket communication between the client and the server.
 * It handles the creation of socket connections, sending and receiving messages, and managing the lifecycle
 * of the socket connection.
 */
public class SocketHandler {
    private final Socket socket;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;
    private final ConcurrentHashMap<Integer, CompletableFuture<Object>> returnValues = new ConcurrentHashMap<>();
    private final AtomicInteger uniqueId = new AtomicInteger();
    private volatile boolean shouldListen = true;

    /**
     * Constructs a SocketHandler with the specified host and port.
     *
     * @param host the host to connect to
     * @param port the port to connect to
     * @throws IOException if an I/O error occurs when creating the socket
     */
    public SocketHandler(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        socket.setSoTimeout(5000);
        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.input = new ObjectInputStream(socket.getInputStream());
        socket.setSoTimeout(0);
    }

    /**
     * Constructs a SocketHandler with an existing socket.
     *
     * @param socket the existing socket
     * @throws IOException if an I/O error occurs when creating the input/output streams
     */
    public SocketHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.input = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Starts listening for messages from the socket.
     *
     * @throws IOException if an I/O error occurs while reading from the input stream
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    public void startListeningForMessages() throws IOException, ClassNotFoundException {
        while (shouldListen) {
            Object msg = input.readObject();
            if (msg instanceof ReturnMessage returnMsg) {
                Log.getLogger().info("Received return value from server: " + returnMsg);
                synchronized (returnValues) {
                    CompletableFuture<Object> future = returnValues.get(returnMsg.id());
                    if (future != null) {
                        future.complete(returnMsg.returnValue());
                    } else {
                        CompletableFuture<Object> newFuture = CompletableFuture.completedFuture(returnMsg.returnValue());
                        returnValues.put(returnMsg.id(), newFuture);
                    }
                }
            } else if (msg instanceof SocketMessage pushMsg) {
                Log.getLogger().info("Received call from server: " + pushMsg);
                this.handlePushMessage(pushMsg);
            } else {
                Log.getLogger().severe("Received unknown message from server: " + msg);
            }
        }
    }

    /**
     * Sends a request to the server and waits for a response.
     *
     * @param request the request message to send
     * @param responseType the expected type of the response
     * @return the response from the server
     * @throws Exception if an error occurs while sending the request or waiting for the response
     */
    public <T> T sendRequest(SocketMessage request, Class<T> responseType) throws Exception {
        try {
            synchronized(output) {
                output.writeObject(request);
            }

            CompletableFuture<Object> future;
            synchronized (this.returnValues) {
                future = this.returnValues.computeIfAbsent(
                        request.id(),
                        k -> new CompletableFuture<>()
                );
            }

            Object result = future.get(2000, TimeUnit.MILLISECONDS);

            if (result instanceof Throwable) {
                throw (Exception) result;
            }
            return responseType.cast(result);
        } catch (IOException | InterruptedException | ExecutionException | TimeoutException | ClassCastException e) {
            throw new RemoteException("Failed to send request: " + e.getMessage(), e);
        }
    }

    /**
     * Handles push messages received from the socket.
     *
     * @param msg the push message to handle
     */
    protected void handlePushMessage(SocketMessage msg) {}

    /**
     * Returns a unique identifier to be used for a new message to the socket.
     * When sending the message with the return value, the other party will need
     * to attach the generated unique identifier.
     *
     * @return a unique identifier
     */
    public int getUniqueId() {
        return uniqueId.getAndIncrement();
    }

    public void close() {
        try { this.socket.close(); } catch (IOException ignored) {}
    }
}
