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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller class for the game over GUI screen.
 * Handles user interactions at the end of the game, such as viewing the final rank and navigating back to the game scene to review the boards.
 */
public class EndGameController extends GuiController {
    @FXML
    private Button leaveButton, viewboardsButton;
    @FXML
    private ListView<EndGameInfoItem> rankingListview;
    @FXML
    private VBox rankingVbox;
    @FXML
    private HBox labelsHbox;
    @FXML
    private ImageView totemforfImageview;
    @FXML
    private Label winnerforfLabel, forfeitLabel;

    @Override
    public void init() {
        leaveButton.setOnAction(e -> leave());
        viewboardsButton.setOnAction(e -> backToOverview());

        VirtualPlayer forfeitWinner = this.manager.getVirtualGame().getforfeitWinner();
        if (forfeitWinner != null) {
            rankingVbox.getChildren().remove(labelsHbox);
            rankingVbox.getChildren().remove(rankingListview);

            winnerforfLabel.setText(forfeitWinner.getUsername());
            totemforfImageview.setImage(this.guiTextureManager.getImageByTotemColor(forfeitWinner.getColor()));

            return;
        }

        rankingVbox.getChildren().remove(forfeitLabel);
        rankingVbox.getChildren().remove(totemforfImageview);

        List<VirtualPlayer> ranking = this.manager.getVirtualGame().getRanking();
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

    /**
     * Handles the action of leaving the game room and navigating back to the main menu.
     * If an error occurs, shows an error popup with the appropriate message.
     */
    private void leave(){
        this.manager.executorService.submit(() -> this.gameController.leave());
    }

    /**
     * Handles the action of navigating back to the overview screen.
     * If an error occurs, shows an error popup with the appropriate message.
     */
    private void backToOverview(){
        try {
            this.manager.changeScene(SceneTitle.OVERVIEW, false);
        } catch (NullPointerException e){
            showErrorPopup(e.getMessage());
        }
    }
}
