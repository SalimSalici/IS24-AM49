package it.polimi.ingsw.am49.client.controller;

import it.polimi.ingsw.am49.chat.ChatMSG;
import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.model.actions.ChooseObjectiveAction;
import it.polimi.ingsw.am49.model.actions.ChooseStarterSideAction;
import it.polimi.ingsw.am49.model.actions.DrawCardAction;
import it.polimi.ingsw.am49.model.actions.PlaceCardAction;
import it.polimi.ingsw.am49.model.enumerations.CornerPosition;
import it.polimi.ingsw.am49.model.enumerations.DrawPosition;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotInGameException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.server.exceptions.RoomException;
import it.polimi.ingsw.am49.view.View;

import java.rmi.RemoteException;
import java.util.concurrent.Executors;

public class GameController extends ClientController {

    public GameController(Server server, ClientApp client) {
        super(server, client);
    }

    public void chooseStarterSide(boolean side) throws InvalidActionException, NotYourTurnException, NotInGameException, RemoteException {
        this.server.executeAction(this.client, new ChooseStarterSideAction(ClientApp.getUsername(), side));
    }

    public void chooseObjective(int objectiveId) throws InvalidActionException, NotYourTurnException, NotInGameException, RemoteException {
        this.server.executeAction(this.client, new ChooseObjectiveAction(ClientApp.getUsername(), objectiveId));
    }

    public void placeCard(int cardId, int parentRow, int parentCol, CornerPosition cornerPosition, boolean flipped) throws InvalidActionException, NotYourTurnException, NotInGameException, RemoteException {
        this.server.executeAction(this.client, new PlaceCardAction(ClientApp.getUsername(), cardId, parentRow, parentCol, cornerPosition, flipped));
    }

    public void drawCard(DrawPosition drawPosition, int idOfRevealedDrawn) throws InvalidActionException, NotYourTurnException, NotInGameException, RemoteException {
        this.server.executeAction(this.client, new DrawCardAction(ClientApp.getUsername(), drawPosition, idOfRevealedDrawn));
    }

    public void chatMessage(String message, String recipient) throws RemoteException {
        this.server.chatMessage(this.client, new ChatMSG(message, ClientApp.getUsername(), recipient));
    }

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
}
