package it.polimi.ingsw.am49.controller;

import it.polimi.ingsw.am49.messages.mtc.MessageToClient;
import it.polimi.ingsw.am49.messages.mts.*;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.decks.DeckLoader;
import it.polimi.ingsw.am49.model.enumerations.CornerPosition;

public class Client {
    private final String userName;
    Server server;
    SingleGameController controller;

    public Client(String userName, Server server){
        this.userName = userName;
        this.server = server;
    }

    public void sendMessage(MessageToClient msg){
        System.out.println(msg.getMessage());
    }

    public void createGame(int numOfPlayers){
        try {
            this.controller = this.server.createLobby(new CreateGameMTS(this, numOfPlayers));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void joinGame(int id){
        try {
            this.controller = this.server.joinLobby(new JoinGameMTS(this), id);
            this.controller.sendMessge(new JoinGameMTS(this));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void leaveGame() throws Exception{
        if(this.controller == null) throw new Exception("You must be in a game to leave it!");

        try {
            this.controller.sendMessge(new LeaveGameMTS(this));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void chooseStarterSide(boolean flipped) throws Exception{
        if(this.controller == null) throw new Exception("You are not in the game");

        try {
            this.controller.sendMessge(new ChooseStarterSideMTS(this.getUserName(), flipped));
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

        this.controller.sendMessge(new ChooseObjectiveMTS(this.getUserName(), objective));
    }

    public void placeCard(int cardId, int parentRow, int parentCol, CornerPosition cornerPosition, boolean flipped) throws Exception{
        if(this.controller == null) throw new Exception("You are not in the game");

        try {
            this.controller.sendMessge(new PlaceCardMTS(this.getUserName(),cardId, parentRow, parentCol, cornerPosition, flipped));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }


    public String getUserName() {
        return userName;
    }

    public Server getServer() {
        return server;
    }

    public SingleGameController getController() {
        return controller;
    }
}
