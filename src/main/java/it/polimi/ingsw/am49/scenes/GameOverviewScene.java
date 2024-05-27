package it.polimi.ingsw.am49.scenes;

import it.polimi.ingsw.am49.client.TuiApp;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.model.actions.DrawCardAction;
import it.polimi.ingsw.am49.model.enumerations.DrawPosition;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotInGameException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.util.Observer;
import it.polimi.ingsw.am49.view.tui.TuiDrawAreaRenderer;
import it.polimi.ingsw.am49.view.tui.TuiPlayerRenderer;
import it.polimi.ingsw.am49.view.tui.textures.AnsiColor;

import java.rmi.RemoteException;
import java.util.List;

public class GameOverviewScene extends Scene implements Observer {
    private final VirtualGame game;
    private boolean running = true;
    private final TuiDrawAreaRenderer tuiDrawAreaRenderer;
    private TuiPlayerRenderer focusedPlayerRenderer;
    private String errorMessage;

    public GameOverviewScene(SceneManager sceneManager, TuiApp tuiApp) {
        super(sceneManager, tuiApp);
        this.game = tuiApp.getVirtualGame();
        this.game.addObserver(this);
        this.tuiDrawAreaRenderer = new TuiDrawAreaRenderer(this.game.getDrawableArea());
        this.focusedPlayerRenderer = new TuiPlayerRenderer(this.game.getPlayerByUsername(tuiApp.getUsername()), false, this.game.getCommonObjectives());
        this.errorMessage = "";
    }

    @Override
    public void play() {
        while (this.running) {
            this.printContent();
            this.promptCommand();
            String[] parts = scanner.nextLine().trim().toLowerCase().split(" ");
            if (parts.length == 0) {
                System.out.println("Invalid command, please try again.");
                continue;
            }

            String command = parts[0];
            switch (command) {
                case "1", "chat":
                    this.errorMessage = "Not yet supported.";
                    break;
                case "2", "focus":
                    this.handleFocusPlayer(parts);
                    break;
                case "3", "view":
                    this.handleViewPlayer(parts);
                    break;
                case "4", "draw":
                    if (this.canDraw())
                        this.handleDrawCard(parts);
                    else
                        this.errorMessage = "You cannot do that action now.";
                    break;
                case "exit":
                    try {
                        this.tuiApp.getServer().disconnect(this.tuiApp);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                    this.sceneManager.setScene(new MainMenuScene(this.sceneManager, this.tuiApp));
                    this.stop();
                    break;
                default:
                    this.errorMessage = "Invalid command, please try again.";
            }
        }
    }

    private void printContent() {
        this.printHeader();
        System.out.println();
        this.printPlayerList();
        System.out.println("\n");
        this.printDrawArea();
        System.out.println("\n");
        this.printFocusedPlayer();
        System.out.println("\n\n");
    }

    private void printHeader() {
        this.clearScreen();
        System.out.println("*******************************");
        System.out.println("| Welcome to Codex Naturalis! |");
        System.out.println("*******************************");
        System.out.println("     | Game Overview |        ");
        System.out.println("     *****************        ");
        System.out.println("\nCurrent state: " + this.game.getGameState());
    }

    private void printPlayerList() {
        System.out.println("------ Players " + "-".repeat(95));
        System.out.print("\nPlayers *current* [points]:");
        List<VirtualPlayer> players = this.game.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            VirtualPlayer p = players.get(i);
            boolean isCurrentPlayer = p.equals(this.game.getCurrentPlayer());

            System.out.print(" (" + (i+1) + ") ");
            if (this.tuiApp.getUsername().equals(p.getUsername())) System.out.print("you->");
            if (isCurrentPlayer) System.out.print("*");
            System.out.print(AnsiColor.fromColor(p.getColor()) + p.getUsername() + AnsiColor.ANSI_RESET);
            if (isCurrentPlayer) System.out.print("*");
            System.out.print(" [" + p.getPoints() + "]    ");
        }
        System.out.print("\n");
    }

    private void printDrawArea() {
        System.out.println("------ Draw Area " + "-".repeat(95));
        System.out.println();
        this.tuiDrawAreaRenderer.print();
    }

    private void printFocusedPlayer() {
        System.out.println("------ Focused Player " + "-".repeat(95));
        System.out.println();
        this.focusedPlayerRenderer.print();
    }

    private void promptCommand() {
        System.out.println(AnsiColor.ANSI_RED + this.errorMessage + AnsiColor.ANSI_RESET);
        System.out.print("Available commands: ");
        System.out.print("(1) chat | (2) focus player | (3) view player | ");
        if (this.canDraw())
            System.out.print("(4) draw card | ");
        System.out.print("exit");
        System.out.print("\n>>> ");

        this.errorMessage = "";
    }

    private void handleViewPlayer(String[] args) {
        if (args.length < 2) {
            this.errorMessage = "You must specify a player.";
            return;
        }
        try {
            int playerIndex = Integer.parseInt(args[1]) - 1;
            VirtualPlayer player = this.game.getPlayers().get(playerIndex);
            this.sceneManager.setScene(new ViewPlayerScene(this.sceneManager, this.tuiApp, player));
            this.stop();
        } catch (NumberFormatException e) {
            this.errorMessage = "Argument must be a number. Please try again.";
            return;
        } catch (IndexOutOfBoundsException e) {
            this.errorMessage = "Argument must be between 1 and " + this.game.getPlayers().size();
            return;
        }
    }

    private void handleFocusPlayer(String[] args) {
        if (args.length < 2) {
            this.errorMessage = "You must specify a player.";
            return;
        }
        try {
            int playerIndex = Integer.parseInt(args[1]) - 1;
            VirtualPlayer player = this.game.getPlayers().get(playerIndex);
            boolean hidden = !player.getUsername().equals(this.tuiApp.getUsername());
            this.focusedPlayerRenderer = new TuiPlayerRenderer(player, hidden, this.game.getCommonObjectives());
        } catch (NumberFormatException e) {
            this.errorMessage = "Argument must be a number. Please try again.";
            return;
        } catch (IndexOutOfBoundsException e) {
            this.errorMessage = "Argument must be between 1 and " + this.game.getPlayers().size();
            return;
        }
    }

    private void handleDrawCard(String[] args) {
        if (args.length < 2) {
            this.errorMessage = "You must specify a card.";
            return;
        }
        try {
            int choice = Integer.parseInt(args[1]);
            DrawCardAction action = getDrawCardAction(choice);
            this.tuiApp.getServer().executeAction(this.tuiApp, action);
        } catch (NumberFormatException e) {
            this.errorMessage = "Argument must be a number. Please try again.";
            return;
        } catch (IndexOutOfBoundsException e) {
            this.errorMessage = "Argument must be between 1 and 6.";
            return;
        } catch (NotYourTurnException | InvalidActionException e) {
            this.errorMessage = e.getMessage();
        } catch (NotInGameException e) {
            this.errorMessage = "It seems like you are not in a game. Please restart the application.";
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private DrawCardAction getDrawCardAction(int choice) {
        DrawPosition drawPosition = switch (choice) {
            case 1 -> DrawPosition.RESOURCE_DECK;
            case 4 -> DrawPosition.GOLD_DECK;
            case 2, 3, 5, 6 -> DrawPosition.REVEALED;
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + choice);
        };
        int drawId = switch (choice) {
            case 1, 4 -> 0;
            case 2 -> this.game.getDrawableArea().getRevealedResourcesIds().getFirst();
            case 3 -> this.game.getDrawableArea().getRevealedResourcesIds().get(1);
            case 5 -> this.game.getDrawableArea().getRevealedGoldsIds().getFirst();
            case 6 -> this.game.getDrawableArea().getRevealedGoldsIds().get(1);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + choice);
        };
        return new DrawCardAction(this.tuiApp.getUsername(), drawPosition, drawId);
    }

    private boolean canDraw() {
        return  this.game.getCurrentPlayer().getUsername().equals(this.tuiApp.getUsername())
                && this.game.getGameState() == GameStateType.DRAW_CARD;
    }

    @Override
    public void update() {
        this.printContent();
        this.promptCommand();
    }

    private void stop() {
        this.running = false;
        this.game.deleteObserver(this);
    }
}