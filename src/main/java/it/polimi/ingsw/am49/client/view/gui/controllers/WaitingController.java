package it.polimi.ingsw.am49.client.view.gui.controllers;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.common.enumerations.GameStateType;
import it.polimi.ingsw.am49.common.util.Observer;
import it.polimi.ingsw.am49.client.view.gui.SceneTitle;

/**
 * Controller class for the waiting screen in the GUI.
 * Handles game updates to transition to appropriate scenes based on game state changes.
 */
public class WaitingController extends GuiController implements Observer {

    private VirtualGame game;

    @Override
    public void init() {
        this.game = this.manager.getVirtualGame();
        game.addObserver(this);
    }

    @Override
    public void update() {
        if (this.game.getGameState() == GameStateType.PLACE_CARD)
            this.manager.changeScene(SceneTitle.OVERVIEW, true);
    }

    @Override
    public void onClose() {
        super.onClose();
        this.game.deleteObserver(this);
    }

    //
//    @Override
//    public void gameUpdate(GameUpdate gameUpdate){
//        if (gameUpdate.getType() == GameUpdateType.CHOOSABLE_OBJETIVES_UPDATE) {
//            ChoosableObjectivesUpdate update = (ChoosableObjectivesUpdate) gameUpdate;
//            List<Integer> objectiveCardIds = update.objectiveCards();
//            this.manager.setObjectiveCardsIds(objectiveCardIds);
//            this.manager.changeScene(SceneTitle.OBJECTIVE_CARDS, true);
//        }
//        if (gameUpdate.getType() == GameUpdateType.GAME_STATE_UPDATE) {
//            if (((GameStateChangedUpdate)gameUpdate).gameStateType() == GameStateType.PLACE_CARD) {
//                this.manager.changeScene(SceneTitle.OVERVIEW, true);
//            }
//        }
//    }
}
