package it.polimi.ingsw.am49.client.view.tui.scenes;

import it.polimi.ingsw.am49.common.gameupdates.ChatMSG;
import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.client.controller.GameController;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.common.enumerations.GameStateType;
import it.polimi.ingsw.am49.common.util.Observer;
import it.polimi.ingsw.am49.client.view.tui.SceneManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the chat scene in the TUI where players can send messages.
 */
public class ChatScene extends Scene implements Observer {

    private VirtualGame game;
    private final List<TimedChatMessage> chatMessages;
    private final GameController gameController;

    /**
     * Constructs a new ChatScene.
     * @param sceneManager The scene manager handling scene transitions.
     * @param gameController The game controller for chat operations.
     */
    public ChatScene(SceneManager sceneManager, GameController gameController) {
        super(sceneManager);
        this.chatMessages = new LinkedList<>();
        this.gameController = gameController;
    }

    @Override
    public void printView() {
        this.clearScreen();
        this.printHeader();
        System.out.println("\n");
        this.printMessages();
        System.out.println("\n");
        this.printPrompt();
    }

    /**
     * Prints the chat header.
     */
    private void printHeader() {
        System.out.println("*******************************");
        System.out.println("| Welcome to Codex Naturalis! |");
        System.out.println("*******************************");
        System.out.println("        |   Chat   |          ");
        System.out.println("        ************          ");
    }

    /**
     * Prints all chat messages.
     */
    private void printMessages() {
        if (this.chatMessages.isEmpty())
            System.out.println("No messages...");
        for (TimedChatMessage msg : this.chatMessages)
            this.printMsg(msg);
    }

    /**
     * Prints a single chat message.
     * @param msg The message to print.
     */
    private void printMsg(TimedChatMessage msg) {
        String sender = msg.chatMSG.sender();
        String recipient = msg.chatMSG.recipient();
        String toPrint = msg.timestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " ";
        VirtualPlayer senderPlayer = this.sceneManager.getVirtualGame().getPlayerByUsername(sender);
        toPrint += this.getColoredUsername(senderPlayer) + " (" + senderPlayer.getPoints() + " points)";
        if (!recipient.equals("*")) {
            if (recipient.equals(ClientApp.getUsername()))
                toPrint += " whispered you";
            else {
                VirtualPlayer recipientPlayer = this.sceneManager.getVirtualGame().getPlayerByUsername(recipient);
                toPrint += " whispered " + this.getColoredUsername(recipientPlayer);
            }
        }
        toPrint += ": " + msg.chatMSG.text();
        System.out.println(toPrint);
    }

    /**
     * Prints the prompt for user input.
     */
    private void printPrompt() {
        this.printInfoOrError();
        System.out.print("Available commands: type and press enter to send a message to everyone | /to [username] | /back");
        System.out.print("\n>>> ");
    }

    @Override
    public void handleInput(String input) {
        String[] parts = input.split(" ");
        String command = parts[0];
        switch (command) {
            case "/b", "/back":
                this.sceneManager.switchScene(SceneType.OVERVIEW_SCENE);
                break;
            case "/to":
                this.handleSendPrivateMessage(parts);
                break;
            default:
                this.handleSendMessage(input);
        }
    }

    /**
     * Adds a chat message to the chat history.
     * @param msg The chat message to add.
     */
    public void addChatMessage(ChatMSG msg) {
        this.chatMessages.add(new TimedChatMessage(msg, LocalDateTime.now()));
        this.refreshView();
    }

    /**
     * Handles sending a message to all users.
     * @param message The message to send.
     */
    private void handleSendMessage(String message) {
        this.gameController.chatMessage(message, "*");
        this.refreshView();
    }

    /**
     * Handles sending a private message to a specific user.
     * @param parts The command parts containing the recipient and message.
     */
    private void handleSendPrivateMessage(String[] parts) {
        if (parts.length < 3) {
            showError("Invalid command. You must specify recipient and message text.");
            return;
        }
        String message = Arrays.stream(parts).skip(2).collect(Collectors.joining(" "));
        this.gameController.chatMessage(message, parts[1]);
        this.refreshView();
    }

    /**
     * Clears all messages from the chat history.
     */
    public void clearMessages() {
        this.chatMessages.clear();
    }

    @Override
    public void focus() {
        this.game = this.sceneManager.getVirtualGame();
        this.game.addObserver(this);
        this.printView();
    }

    @Override
    public void unfocus() {
        this.game.deleteObserver(this);
    }

    @Override
    public void update() {
        if (this.game.getGameState() == GameStateType.END_GAME)
            this.sceneManager.switchScene(SceneType.END_GAME_SCENE);
        else
            this.refreshView();
    }

    /**
     * Represents a chat message with a timestamp.
     */
    private record TimedChatMessage(ChatMSG chatMSG, LocalDateTime timestamp) {}
}
