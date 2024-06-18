package it.polimi.ingsw.am49.view.tui.scenes;

import it.polimi.ingsw.am49.chat.ChatMSG;
import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.client.TuiApp;
import it.polimi.ingsw.am49.client.controller.GameController;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.util.Observer;
import it.polimi.ingsw.am49.view.tui.SceneManager;

import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatScene extends Scene implements Observer {

    private VirtualGame game;
    private final List<TimedChatMessage> chatMessages;
    private final GameController gameController;

    public ChatScene(SceneManager sceneManager, TuiApp tuiApp, GameController gameController) {
        super(sceneManager, tuiApp);
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

    private void printHeader() {
        System.out.println("*******************************");
        System.out.println("| Welcome to Codex Naturalis! |");
        System.out.println("*******************************");
        System.out.println("        |   Chat   |          ");
        System.out.println("        ************          ");
    }

    private void printMessages() {
        if (this.chatMessages.isEmpty())
            System.out.println("No messages...");
        for (TimedChatMessage msg : this.chatMessages)
            this.printMsg(msg);
    }

    private void printMsg(TimedChatMessage msg) {
        String sender = msg.chatMSG.sender();
        String recipient = msg.chatMSG.recipient();
        String toPrint = msg.timestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " ";
        VirtualPlayer senderPlayer = this.tuiApp.getVirtualGame().getPlayerByUsername(sender);
        toPrint += this.getColoredUsername(senderPlayer) + " (" + senderPlayer.getPoints() + " points)";
        if (!recipient.equals("*")) {
            if (recipient.equals(ClientApp.getUsername()))
                toPrint += " whispered you";
            else {
                VirtualPlayer recipientPlayer = this.tuiApp.getVirtualGame().getPlayerByUsername(recipient);
                toPrint += " whispered " + this.getColoredUsername(recipientPlayer);
            }
        }
        toPrint += ": " + msg.chatMSG.text();
        System.out.println(toPrint);
    }

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

    public void addChatMessage(ChatMSG msg) {
        this.chatMessages.add(new TimedChatMessage(msg, LocalDateTime.now()));
        this.refreshView();
    }

    private void handleSendMessage(String message) {
        try {
            this.gameController.chatMessage(message, "*");
            this.refreshView();
        } catch (RemoteException e) {
            this.showError("Network error");
            e.printStackTrace();
        }
    }

    private void handleSendPrivateMessage(String[] parts) {
        if (parts.length < 3) {
            showError("Invalid command. You must specify recipient and message text.");
            return;
        }
        try {
            String message = Arrays.stream(parts).skip(2).collect(Collectors.joining(" "));
            this.gameController.chatMessage(message, parts[1]);
            this.refreshView();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearMessages() {
        this.chatMessages.clear();
    }

    @Override
    public void focus() {
        this.game = this.tuiApp.getVirtualGame();
        this.game.addObserver(this);
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

    private record TimedChatMessage(ChatMSG chatMSG, LocalDateTime timestamp) {}
}
