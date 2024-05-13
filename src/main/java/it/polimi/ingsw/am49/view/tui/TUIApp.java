package it.polimi.ingsw.am49.view.tui;

import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.model.actions.ChooseStarterSideAction;
import it.polimi.ingsw.am49.model.actions.JoinGameAction;
import it.polimi.ingsw.am49.model.actions.LeaveGameAction;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.*;

import java.rmi.RemoteException;
import java.util.Scanner;

public class TUIApp {
    private String username;
    private String color;
    private final Client client;
    private final Server server;
    private final Scanner scanner;

    private RoomInfo roomInfo;

    public TUIApp(Client client, Server server) {
        this.client = client;
        this.server = server;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Welcomes user and asks for a username.
     */
    public void startTUI() throws NotInGameException {
        System.out.println("\n***************************");
        System.out.println("Welcome to Codex Naturalis!");
        System.out.println("***************************\n");

        System.out.print("Choose a username> ");
        this.username = this.scanner.nextLine();
        System.out.println();

        this.mainMenuScene();
    }

    /**
     * Here the user can either change their username, craete a new room, join an existing room or disconnect
     * from the program.
     */
    public void mainMenuScene() throws NotInGameException { // TODO: handle NotInGameException (maybe in method roomScene())
        boolean done = false;
        while (!done) {
            System.out.print("Main menu (username|create|join|disconnect)> ");
            String input = this.scanner.nextLine();

            switch (input) {
                case "username" -> {
                    System.out.print("Choose a new username> ");
                    this.username = this.scanner.nextLine();
                    System.out.println("New username set to '" + this.username + "'");
                }
                case "create" -> {
                    try {
                        this.roomInfo = this.createRoomCommand();
                        System.out.println("Room created");
                        this.roomScene();
                        done = true;
                    } catch (AlreadyInRoomException e) {
                        System.out.println("It seems like you are already in a room. Please restart the application.");
                    } catch (CreateRoomException e) {
                        System.out.println(e.getMessage());
                    } catch (RemoteException e) {
                        System.out.println("Network error.");
                        System.out.println(e.getMessage());
                    } catch (NotYourTurnException e) {
                        throw new RuntimeException(e);
                    }
                }
                case "join" -> {
                    try {
                        this.roomInfo = this.joinRoomCommand();
                        this.roomScene();
                        done = true;
                    } catch (AlreadyInRoomException e) {
                        System.out.println("It seems like you are already in a room. Please restart the application.");
                    } catch (JoinRoomException e) {
                        System.out.println(e.getMessage());
                    } catch (RemoteException e) {
                        System.out.println("Network error.");
                        System.out.println(e.getMessage());
                    } catch (NotYourTurnException e) {
                        throw new RuntimeException(e);
                    }
                }
                case "disconnect" -> {
                    System.out.println("Closing application. Bye!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid command.");
            }
        }
    }

    private RoomInfo createRoomCommand() throws AlreadyInRoomException, RemoteException, CreateRoomException {
        System.out.print("Choose room name> ");
        String roomName = this.scanner.nextLine();
        System.out.print("Choose player number> ");
        int numPlayers = this.scanner.nextInt();
        this.scanner.nextLine();

        return this.server.createRoom(this.client, roomName, numPlayers, this.username);
    }

    private RoomInfo joinRoomCommand() throws AlreadyInRoomException, RemoteException, JoinRoomException {
        System.out.print("Choose room name> ");
        String roomName = this.scanner.nextLine();
        return this.server.joinRoom(this.client, roomName, this.username);
    }

    /**
     * Here the user is in the room waiting for other players to join and for the game to start.
     * The user can choose to ready up (or unready up) or to leave the room.
     * The user is also notified of other players joining / leaving and readying up / unreadying up.
     */
    public void roomScene() throws NotInGameException, RemoteException, NotYourTurnException {
        System.out.println("You joined a room - " + this.roomInfo.toString());
        boolean done = false;
        while (!done) {
            System.out.print("Room[color="+ color + "] (color|leave|disconnect)> ");
            String input = this.scanner.nextLine();

            // TODO: handle NotInGameException (should never happen at this stage,
            //       but if it happens tell the user to restart)
            switch (input) {
                case "color" -> {
                    System.out.print("Choose a new color (red, blue, yellow, green)> ");
                    this.color = this.scanner.nextLine();
                    this.server.readyUp(client, Color.valueOf(color.toUpperCase()));
                }
                case "leave" -> {
                    LeaveGameAction action = new LeaveGameAction(this.username);
                    this.server.executeAction(this.client, action);

                    if (!this.server.leaveRoom(this.client))
                        System.out.println("It seems like you were not in a room.");
                    else
                        System.out.println("Room left.");

                    System.out.println("Going back to main menu.");
                    this.mainMenuScene();
                    done = true;
                }
                case "dbg_next" -> {
                    this.starterCardScene();
                    done = true;
                }
                case "disconnect" -> {
                    this.server.leaveRoom(this.client);
                    System.out.println("Closing application. Bye!");
                    System.exit(0);
                }
             }
        }
    }

    public void starterCardScene() {
        boolean done = false;
        while (!done) {
            System.out.print("Do you want to flip starter side (true|false)> ");
            boolean flipped = this.scanner.nextBoolean();
            this.scanner.nextLine();
            try {
                this.server.executeAction(this.client, new ChooseStarterSideAction(this.username, flipped));
                done = true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
                System.out.println();
            }
        }
        System.out.println("Starter side chosen.");
        this.scanner.nextLine();
    }
}
