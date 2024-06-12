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


/**
 * Manages the graphical user interface (GUI) for the application, handling scene transitions and stage setup.
 */
public class GuiManager {

    private Scene currentScene;
    private Stage stage;
    private final BiMap<SceneTitle, Scene> titleToScene = new BiMap<>();
    private final BiMap<SceneTitle, GuiController> titleToController = new BiMap<>();
    private RoomInfo roomInfo;
    private int starterCardId;
    private List<Integer> objectiveCardsIds;
    private final GuiApp app;
  
    // CONSTRUCTOR

    /**
     * Constructs a new GuiManager associated with the specified application.
     *
     * @param app the GuiApp instance associated with this manager
     */
    public GuiManager(GuiApp app) {
        this.app = app;
    }


    // PUBLIC METHODS


    /**
     * Starts the GUI manager by setting up the stage and running the initial scene.
     *
     * @param stage the primary stage for this application
     * @throws IOException if an I/O error occurs during setup
     */
    public void start(Stage stage) throws IOException {
        setup();
        this.stage = stage;
        stage.setResizable(false);
        run();
    }

    /**
     * Stops the GUI manager and exits the application.
     */
    public void stop() {
        System.exit(0);
    }


    /**
     * Changes the current scene to the specified scene.
     *
     * @param newSceneTitle the title of the new scene to be displayed
     */
    public void changeScene(SceneTitle newSceneTitle) {
        System.out.println("Changing scene to: " + newSceneTitle.getFileName());

        if (titleToScene.getValue(newSceneTitle) == null) {
            System.err.println("Couldn't find the specified scene");
            stop();
        }

        currentScene = titleToScene.getValue(newSceneTitle);
        titleToController.getValue(titleToScene.getKey(currentScene)).init();

        Platform.runLater(() -> {
            stage.setResizable(false);
            stage.setScene(currentScene);
            stage.sizeToScene();
            stage.show();
        });
    }


    // GETTERS

    /**
     * @param sceneTitle the title of the scene
     * @return the Scene associated with the specified title
     */
    public Scene getSceneBySceneTitle(SceneTitle sceneTitle) {
        return titleToScene.getValue(sceneTitle);
    }

    /**
     * @return the current scene
     */
    public Scene getCurrentScene() {
        return currentScene;
    }

    /**
     * @return the current GuiController
     */
    public GuiController getCurrentController(){
        return titleToController.getValue(titleToScene.getKey(currentScene));
    }

    /**
     * @return the room information
     */
    public RoomInfo getRoomInfo() {
        return roomInfo;
    }

    /**
     * @return the starter card ID
     */
    public int getStarterCardId() {
        return starterCardId;
    }

    /**
     * @return the list of objective cards IDs
     */
    public List<Integer> getObjectiveCardsIds() {
        return objectiveCardsIds;
    }


    //SETTERS

    /**
     * Sets the room information.
     *
     * @param roomInfo the new room information
     */
    public void setRoomInfo(RoomInfo roomInfo) {
        this.roomInfo = roomInfo;

        System.out.println(this.roomInfo.playersToColors());;
    }

    /**
     * Sets the starter card ID.
     *
     * @param starterCardId the new starter card ID
     */
    public void setStarterCardId(int starterCardId) {
        this.starterCardId = starterCardId;
    }

    /**
     * Sets the objective cards IDs.
     *
     * @param objectiveCardsIds the new list of objective cards IDs
     */
    public void setObjectiveCardsIds(List<Integer> objectiveCardsIds) {
        this.objectiveCardsIds = objectiveCardsIds;
    }


    // PRIVATE METHODS

    /**
     * Sets up the scenes and controllers for the application.
     */
    private void setup() {
        try {
            for (SceneTitle sceneTitle : SceneTitle.values()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneTitle.getFilePath()));
                //Creates actual scene for this scene name
                Scene scene;
                if (sceneTitle == SceneTitle.OVERVIEW) {
                    scene = new Scene(loader.load(), 1600, 780);
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

    /**
     * Runs the initial scene for the application.
     */
    private void run() {
        stage.setTitle("Codex Naturalis");
        stage.setScene(currentScene);
        //stage.getIcons().add(new Image(getClass().getResourceAsStream("")));
        //currentScene.setCursor(new ImageCursor(new Image(getClass().getResourceAsStream(""))));
        stage.show();
    }

}
