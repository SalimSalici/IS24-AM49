package it.polimi.ingsw.am49.client.controller;

import it.polimi.ingsw.am49.common.gameupdates.ChatMSG;
import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.common.actions.ChooseObjectiveAction;
import it.polimi.ingsw.am49.common.actions.ChooseStarterSideAction;
import it.polimi.ingsw.am49.common.actions.DrawCardAction;
import it.polimi.ingsw.am49.common.actions.PlaceCardAction;
import it.polimi.ingsw.am49.common.enumerations.CornerPosition;
import it.polimi.ingsw.am49.common.enumerations.DrawPosition;
import it.polimi.ingsw.am49.common.Server;
import it.polimi.ingsw.am49.common.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.common.exceptions.NotInGameException;
import it.polimi.ingsw.am49.common.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.common.exceptions.RoomException;

import java.rmi.RemoteException;

/**
 * Controller class for game-related actions and interactions.
 */
public class GameController extends ClientController {

    /**
     * Constructs a GameController with a specified server and client application.
     * 
     * @param server the server instance to be used by this controller
     * @param client the client application instance
     */
    public GameController(Server server, ClientApp client) {
        super(server, client);
    }

    /**
     * Sends a request to the server to choose the starter side.
     * 
     * @param side the side to choose
     * @throws InvalidActionException if the action is invalid
     * @throws NotYourTurnException if it is not the player's turn
     * @throws NotInGameException if the player is not in a game
     */
    public void chooseStarterSide(boolean side) throws InvalidActionException, NotYourTurnException, NotInGameException {
        try {
            this.server.executeAction(this.client, new ChooseStarterSideAction(ClientApp.getUsername(), side));
        } catch (RemoteException e) {
            this.backToServerSelection();
        }
    }

    /**
     * Sends a request to the server to choose an objective.
     * 
     * @param objectiveId the ID of the objective to choose
     * @throws InvalidActionException if the action is invalid
     * @throws NotYourTurnException if it is not the player's turn
     * @throws NotInGameException if the player is not in a game
     */
    public void chooseObjective(int objectiveId) throws InvalidActionException, NotYourTurnException, NotInGameException {
        try {
            this.server.executeAction(this.client, new ChooseObjectiveAction(ClientApp.getUsername(), objectiveId));
        } catch (RemoteException e) {
            this.backToServerSelection();
        }
    }

    /**
     * Sends a request to the server to place a card on the game board.
     * 
     * @param cardId the ID of the card to place
     * @param parentRow the row of the parent card
     * @param parentCol the column of the parent card
     * @param cornerPosition the corner position to place the card
     * @param flipped whether the card should be flipped
     * @throws InvalidActionException if the action is invalid
     * @throws NotYourTurnException if it is not the player's turn
     * @throws NotInGameException if the player is not in a game
     */
    public void placeCard(int cardId, int parentRow, int parentCol, CornerPosition cornerPosition, boolean flipped) throws InvalidActionException, NotYourTurnException, NotInGameException {
        try {
            this.server.executeAction(this.client, new PlaceCardAction(ClientApp.getUsername(), cardId, parentRow, parentCol, cornerPosition, flipped));
        } catch (RemoteException e) {
            this.backToServerSelection();
        }
    }

    /**
     * Sends a request to the server to draw a card from the deck.
     * 
     * @param drawPosition the position from which to draw the card
     * @param idOfRevealedDrawn the ID of the card that was revealed during the draw
     * @throws InvalidActionException if the action is invalid
     * @throws NotYourTurnException if it is not the player's turn
     * @throws NotInGameException if the player is not in a game
     */
    public void drawCard(DrawPosition drawPosition, int idOfRevealedDrawn) throws InvalidActionException, NotYourTurnException, NotInGameException {
        try {
            this.server.executeAction(this.client, new DrawCardAction(ClientApp.getUsername(), drawPosition, idOfRevealedDrawn));
        } catch (RemoteException e) {
            this.backToServerSelection();
        }
    }

    /**
     * Sends a chat message to a specified recipient.
     *
     * @param message the message to send
     * @param recipient the recipient of the message
     */
    public void chatMessage(String message, String recipient) {
        try {
            this.server.chatMessage(this.client, new ChatMSG(message, ClientApp.getUsername(), recipient));
        } catch (RemoteException e) {
            this.backToServerSelection();
        }
    }

    /**
     * Initiates the process for the player to leave the game room.
     */
    public void leave() {
        try {
            new Thread(() -> {
                try {
                    server.leaveRoom(this.client);
                } catch (RemoteException | RoomException ignored) {}
            }).start();
            this.client.getVirtualGame().clearAllObservers();
            Thread.sleep(150);
            this.view.showMainMenu();
        } catch (InterruptedException ignored) {
        } finally {
            this.client.stopHeartbeat();
        }
    }

    private void backToServerSelection() {
        new Thread(() -> {
            try { server.leaveRoom(this.client); } catch (RemoteException | RoomException ignored) {}
        }).start();
        if (this.client.getVirtualGame() != null)
            this.client.getVirtualGame().clearAllObservers();
        this.view.showServerSelection();
    }
}
