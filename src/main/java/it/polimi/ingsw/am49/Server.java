package it.polimi.ingsw.am49;

import it.polimi.ingsw.am49.controller.SingleGameController;
import it.polimi.ingsw.am49.messages.mts.CreateNewGameMTS;
import it.polimi.ingsw.am49.messages.mts.MessageToServer;
import it.polimi.ingsw.am49.model.Game;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

public class Server {
    private final Map<Integer, SingleGameController> controllers;

    public Server(){
        controllers = new HashMap<>();
    }

    private int generateId() {
        int id;
        boolean isUnique;
        Random random = new Random();

        do {
            id = random.nextInt();
            isUnique = true;

            for (Integer usedId : controllers.keySet()) {
                if (usedId == id) {
                    isUnique = false;
                    break;
                }
            }
        } while (!isUnique);

        return id;
    }


    public SingleGameController createLobby(CreateNewGameMTS msg) throws Exception {
        int id = 0;//generateId();
        SingleGameController controller = new SingleGameController(id, msg.getClient(), msg.getNumOfPlayers());
        controllers.put(id, controller);
        return controller;
    }

    public SingleGameController joinLobby(MessageToServer msg, int id) throws Exception{
        if(!controllers.containsKey(id)) throw new Exception("The lobby doesn't exist");
        return controllers.get(id);
    }

}
