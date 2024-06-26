package it.polimi.ingsw.am49.client.view.gui.controllers;
import it.polimi.ingsw.am49.client.view.gui.SceneTitle;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for the game over GUI screen.
 * Handles user interactions at the end of the game, such as viewing the final rank and navigating back to the game scene to review the boards.
 */
public class EndGameController extends GuiController {
    @FXML
    private Button leaveButton, viewboardsButton;
    @FXML
    private VBox rankingVbox;

    /**
     * Initializes the end game controller.
     * Sets up the leave button and view boards button actions,
     * applies stylesheets, and creates the ranking elements.
     */
    @Override
    public void init() {
        leaveButton.setOnAction(e -> leave());
        viewboardsButton.setOnAction(e -> backToOverview());

        rankingVbox.getChildren().clear();

        Scene scene = leaveButton.getScene();
        if (scene != null) {
            scene.getStylesheets().add(getClass().getResource("/it/polimi/ingsw/am49/css/endGame.css").toExternalForm());
        }

        VirtualPlayer forfeitWinner = this.manager.getVirtualGame().getforfeitWinner();
        if (forfeitWinner != null) {
            createForfeitWinnerElements(forfeitWinner);
            return;
        }

        List<VirtualPlayer> ranking = this.manager.getVirtualGame().getRanking();

        createRankingElements(ranking);
    }

    /**
     * Creates the ranking elements for the end game screen.
     *
     * @param ranking the list of players in their ranking order
     */
    private void createRankingElements(List<VirtualPlayer> ranking) {
        HBox labelsHbox = new HBox();

        Pane spacer1 = new Pane();
        Pane spacer2 = new Pane();
        Pane spacer3 = new Pane();
        spacer1.setMaxWidth(50);
        spacer2.setMaxWidth(180);
        spacer3.setMaxWidth(40);
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        HBox.setHgrow(spacer3, Priority.ALWAYS);


        Label playersLabel = new Label("players");
        Label pointsLabel = new Label("points");
        Label objectivesLabel = new Label("objectives");

        playersLabel.getStyleClass().add("grey-label");
        pointsLabel.getStyleClass().add("grey-label");
        objectivesLabel.getStyleClass().add("grey-label");
        rankingVbox.setSpacing(4);

        labelsHbox.getChildren().addAll(spacer1, playersLabel, spacer2, pointsLabel, spacer3, objectivesLabel);
        labelsHbox.setId("labelsHbox");

        ListView<EndGameInfoItem> rankingListview = new ListView<>();
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

        rankingListview.getItems().addAll(endGameItems);
        rankingListview.setCellFactory(param -> new EndGameInfoListCell());
        rankingVbox.getChildren().addAll(labelsHbox, rankingListview);
    }


    /**
     * Creates the elements for displaying the forfeit winner.
     *
     * @param forfeitWinner the player who won by forfeit
     */
    private void createForfeitWinnerElements(VirtualPlayer forfeitWinner) {
        Label forfeitLabel = new Label("The game was won by forfeit by: ");
        Label winnerforfLabel = new Label(forfeitWinner.getUsername());
        ImageView totemforfImageview = new ImageView(this.guiTextureManager.getImageByTotemColor(forfeitWinner.getColor()));
        totemforfImageview.setFitWidth(50);
        totemforfImageview.setFitHeight(50);

        HBox forfeitHbox = new HBox(totemforfImageview, winnerforfLabel);
        forfeitHbox.setAlignment(Pos.CENTER);
        forfeitHbox.setSpacing(10);

        rankingVbox.getChildren().addAll(forfeitLabel, forfeitHbox);
        rankingVbox.setSpacing(50);
    }

    /**
     * Handles the action of leaving the game room and navigating back to the main menu.
     * If an error occurs, shows an error popup with the appropriate message.
     */
    private void leave(){
        this.manager.execute(() -> this.gameController.leave());
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
