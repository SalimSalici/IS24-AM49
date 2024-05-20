package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.controller.gameupdates.ChoosableObjectivesUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameStateChangedUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdateType;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.scenes.ChooseObjectiveCardScene;
import it.polimi.ingsw.am49.scenes.GameOverviewScene;
import it.polimi.ingsw.am49.scenes.InvalidSceneException;
import it.polimi.ingsw.am49.view.gui.SceneTitle;

import java.util.List;

public class WaitingController extends GuiController{
    @Override
    public void gameUpdate(GameUpdate gameUpdate) throws InvalidSceneException {
        if (gameUpdate.getType() == GameUpdateType.CHOOSABLE_OBJETIVES_UPDATE) {
            ChoosableObjectivesUpdate update = (ChoosableObjectivesUpdate) gameUpdate;
            List<Integer> objectiveCardIds = update.objectiveCards();
            this.manager.setObjectiveCardsIds(objectiveCardIds);
            this.manager.changeScene(SceneTitle.OBJECTIVE_CARDS);
        }
        if (gameUpdate.getType() == GameUpdateType.GAME_STATE_UPDATE) {
            if (((GameStateChangedUpdate)gameUpdate).gameStateType() == GameStateType.PLACE_CARD) {
                this.manager.changeScene(SceneTitle.OVERVIEW);
            }
        }
    }
}
