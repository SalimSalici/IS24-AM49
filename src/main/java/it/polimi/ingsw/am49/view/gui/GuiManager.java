package it.polimi.ingsw.am49.view.gui;

import it.polimi.ingsw.am49.client.GuiApp;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.util.BiMap;
import javafx.application.Platform;
import javafx.stage.Stage;
import com.google.gson.Gson;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.ingsw.am49.view.gui.controllers.GuiController;


public class GuiManager {

    private Scene currentScene;
    private Stage stage;
    private final BiMap<SceneTitle, Scene> titleToScene = new BiMap<>();
    private final BiMap<SceneTitle, GuiController> titleToController = new BiMap<>();
    private RoomInfo roomInfo;
    private int starterCardId;
    private List<Integer> objectiveCardsIds;
    private final Gson gson;
    private GuiApp app;
  
    // CONSTRUCTOR
    
    public GuiManager(GuiApp app) {
        this.app = app;
        gson = new Gson();
    }

    public void start(Stage stage) throws IOException {
        setup();
        this.stage = stage;
        stage.setWidth(600);
        stage.setHeight(400);
        stage.setResizable(true);
        run();
    }

    public void stop() {
        System.exit(0);
    }

    // PUBLIC METHODS

    public void changeScene(SceneTitle newSceneTitle) {
        System.out.println("Changing scene to: " + newSceneTitle.getFileName());

        if (titleToScene.getValue(newSceneTitle) == null) {
            System.err.println("Couldn't find the specified scene");
            stop();
        }

        currentScene = titleToScene.getValue(newSceneTitle);
        titleToController.getValue(titleToScene.getKey(currentScene)).init();

        Platform.runLater(() -> {
            if (newSceneTitle == SceneTitle.OVERVIEW) {
                stage.setWidth(1500);
                stage.setHeight(780);
            } else {
                stage.setWidth(600);
                stage.setHeight(400);
            }
            stage.setScene(currentScene);
            stage.sizeToScene();
            stage.show();
        });
    }

    public void setFullScreen() {
        stage.setFullScreen(true);
    }

    // PRIVATE METHODS

    private void setup() {
        try {
            for (SceneTitle sceneTitle : SceneTitle.values()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneTitle.getFilePath()));
                //Creates actual scene for this scene name
                Scene scene;
                if (sceneTitle == SceneTitle.OVERVIEW) {
                    scene = new Scene(loader.load(), 1500, 780);
                } else {
                    scene = new Scene(loader.load(), 600, 400);
                }
                //scene.setCursor(new ImageCursor(new Image(getClass().getResourceAsStream(""))));
                titleToScene.put(sceneTitle, scene);
                GuiController controller = loader.getController();
                //Sets the scene's controller
                controller.setGui(app, this);
                titleToController.put(sceneTitle, controller);
            }
        } catch (IOException e) {
            System.out.println("Senes setup failed");
            stop();
        }
        currentScene = titleToScene.getValue(SceneTitle.WELCOME);
        titleToController.getValue(titleToScene.getKey(currentScene)).init();
    }

    private void run() {
        stage.setTitle("Codex Naturalis");
        stage.setScene(currentScene);
        //stage.getIcons().add(new Image(getClass().getResourceAsStream("")));
        //currentScene.setCursor(new ImageCursor(new Image(getClass().getResourceAsStream(""))));
        stage.show();
    }

    // GETTERS

    public Scene getSceneBySceneTitle(SceneTitle SceneTitle) {
        return titleToScene.getValue(SceneTitle);
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    /**
     * Getter for a GuiController object corresponding to a given SceneTitle
     *
     * @param SceneTitle the name of the Scene
     * @return the corresponding GuiController object
     */
    public GuiController getControllerBySceneTitle(SceneTitle SceneTitle) {
        return titleToController.getValue(SceneTitle);
    }

    public GuiController getCurrentController(){
        return titleToController.getValue(titleToScene.getKey(currentScene));
    }

    public RoomInfo getRoomInfo() {
        return roomInfo;
    }

    public int getStarterCardId() {
        return starterCardId;
    }

    public List<Integer> getObjectiveCardsIds() {
        return objectiveCardsIds;
    }

    //SETTERS

    public void setRoomInfo(RoomInfo roomInfo) {
        this.roomInfo = roomInfo;

        System.out.println(this.roomInfo.playersToColors());;
    }

    public void setStarterCardId(int starterCardId) {
        this.starterCardId = starterCardId;
    }

    public void setObjectiveCardsIds(List<Integer> objectiveCardsIds) {
        this.objectiveCardsIds = objectiveCardsIds;
    }
}
