package it.polimi.ingsw.am49;

import it.polimi.ingsw.am49.controller.SingleGameController;
import it.polimi.ingsw.am49.messages.mtc.MessageToClient;
import it.polimi.ingsw.am49.messages.mts.CreateNewGameMTS;
import it.polimi.ingsw.am49.messages.mts.GameActionMTS;
import it.polimi.ingsw.am49.model.actions.*;
import it.polimi.ingsw.am49.model.enumerations.CornerPosition;
import it.polimi.ingsw.am49.model.enumerations.DrawPosition;

public class Client {
    private final String userName;
    Server server;
    SingleGameController controller;

    public Client(String userName, Server server){
        this.userName = userName;
        this.server = server;
    }

    public void sendMessage(MessageToClient msg){
        System.out.println(this.userName + ": " + msg.getMessage());
    }

    public void createGame(int numOfPlayers){
        try {
            this.controller = this.server.createLobby(new CreateNewGameMTS(this, numOfPlayers));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void joinGame(int id){
        try {
            this.controller = this.server.joinLobby(new GameActionMTS(this, new JoinGameAction(this.userName)), id);
            this.controller.sendMessge(new GameActionMTS(this, new JoinGameAction(this.userName)));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void leaveGame() throws Exception{
        if(this.controller == null) throw new Exception("You must be in a game to leave it!");

        try {
            this.controller.sendMessge(new GameActionMTS(this, new LeaveGameAction(this.userName)));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void chooseStarterSide(boolean flipped) throws Exception{
        if(this.controller == null) throw new Exception("You are not in the game");

        try {
            this.controller.sendMessge(new GameActionMTS(this, new ChooseStarterSideAction(this.userName, flipped)));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

//    public void chooseObjective(int objective) throws Exception{
//        if(this.controller == null) throw new Exception("You are not in the game");
//
//        try {
//            this.controller.sendMessge(new ChooseObjectiveMTS(this.getUserName(), objective));
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//            e.printStackTrace();
//        }
//    }

    public void chooseObjective(int objective) throws Exception{
        if(this.controller == null) throw new Exception("You are not in the game");

        this.controller.sendMessge(new GameActionMTS(this, new ChooseObjectiveAction(this.userName, objective)));
    }

    public void placeCard(int cardId, int parentRow, int parentCol, CornerPosition cornerPosition, boolean flipped) throws Exception{
        if(this.controller == null) throw new Exception("You are not in the game");

        try {
            this.controller.sendMessge(new GameActionMTS(this, new PlaceCard(this.getUserName(),cardId, parentRow, parentCol, cornerPosition, flipped)));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void drawCard(DrawPosition drawPosition, int idOfRevealedDrawn) throws Exception{
        if(this.controller == null) throw new Exception("You are not in the game");

        try {
            this.controller.sendMessge(new GameActionMTS(this, new DrawCardAction(this.userName, drawPosition, idOfRevealedDrawn)));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }


    public String getUserName() {
        return this.userName;
    }

    public Server getServer() {
        return server;
    }

    public SingleGameController getController() {
        return controller;
    }
}
