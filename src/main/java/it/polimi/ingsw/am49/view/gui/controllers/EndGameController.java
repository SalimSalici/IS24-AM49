package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.controller.room.Room;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.AlreadyInRoomException;
import it.polimi.ingsw.am49.server.exceptions.JoinRoomException;
import it.polimi.ingsw.am49.server.exceptions.RoomException;
import it.polimi.ingsw.am49.util.Log;
import it.polimi.ingsw.am49.view.gui.SceneTitle;
import it.polimi.ingsw.am49.view.tui.scenes.InvalidSceneException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EndGameController extends GuiController {
    @FXML
    private Button leaveButton, viewboardsButton;

    @FXML
    private ListView<EndGameInfoItem> rankingListview;

    @Override
    public void init() {
        leaveButton.setOnAction(e -> leave());
        viewboardsButton.setOnAction(e -> backToOverview());

        List<VirtualPlayer> ranking = this.app.getVirtualGame().getRanking();
        List<EndGameInfoItem> endGameItems = new ArrayList<>();

        for (int rank = 0; rank < ranking.size(); rank++) {
            VirtualPlayer player = ranking.get(rank);
            endGameItems.add(new EndGameInfoItem(
                    player.getUsername(),
                    rank + 1, // Assign rank
                    player.getPoints(),
                    player.getCompletedObjectives(),
                    this.guiTextureManager.getImageByTotemColor(player.getColor())
            ));
        }

        rankingListview.getItems().addAll(endGameItems); // Populates rankingListview
        rankingListview.setCellFactory(param -> new EndGameInfoListCell());
    }

    private void leave(){
        this.manager.executorService.submit(() -> {
            try {
                this.app.getServer().leaveRoom(this.app);
            } catch (RoomException | RemoteException e) {
                Platform.runLater(() -> showErrorPopup(e.getMessage()));
            }

            try {
                Thread.sleep(250);
            } catch (InterruptedException ignored) {}
            try {
                this.manager.changeScene(SceneTitle.MAIN_MENU, true);
            } catch (InvalidSceneException e) {
                showErrorPopup(e.getMessage());
            }
        });
    }

    private void backToOverview(){
        try{
            this.manager.changeScene(SceneTitle.OVERVIEW, false);
        }
        catch (InvalidSceneException | NullPointerException e){
            showErrorPopup(e.getMessage());
        }
    }
}
