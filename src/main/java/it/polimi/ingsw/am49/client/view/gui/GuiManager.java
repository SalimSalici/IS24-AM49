package it.polimi.ingsw.am49.client.view.gui;

import it.polimi.ingsw.am49.client.controller.GameController;
import it.polimi.ingsw.am49.client.controller.MenuController;
import it.polimi.ingsw.am49.client.controller.RoomController;
import it.polimi.ingsw.am49.client.view.gui.controllers.GuiController;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.common.reconnectioninfo.RoomInfo;
import it.polimi.ingsw.am49.common.util.BiMap;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
    private VirtualGame virtualGame;
    private final MenuController menuController;
    private final RoomController roomController;
    private final GameController gameController;
    public final ExecutorService executorService = Executors.newSingleThreadExecutor();
  
    // CONSTRUCTOR

    /**
     * Constructs a new GuiManager associated with the specified application.
     *
     */
    public GuiManager(MenuController menuController, RoomController roomController, GameController gameController) {
        this.menuController = menuController;
        this.roomController = roomController;
        this.gameController = gameController;
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
        // Add listener for when closing the window
        this.stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
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
    public void changeScene(SceneTitle newSceneTitle, boolean reload){
        System.out.println("Changing scene to: " + newSceneTitle.getFileName());

        if (titleToScene.getValue(newSceneTitle) == null) {
            System.err.println("GuiManager::changeScene received null SceneTitle.");
            return;
        }

        titleToController.getValue(titleToScene.getKey(currentScene)).onClose();
        currentScene = titleToScene.getValue(newSceneTitle);
        if(reload)
            titleToController.getValue(titleToScene.getKey(currentScene)).init();

        Platform.runLater(() -> {
            stage.setResizable(false);
            stage.setScene(currentScene);
            stage.sizeToScene();
            stage.show();
        });
    }


    // GETTERS


    public VirtualGame getVirtualGame() {
        return virtualGame;
    }

    /**
     * @param sceneTitle the title of the scene
     * @return the Scene associated with the specified title
     */
    public Scene getSceneBySceneTitle(SceneTitle sceneTitle) {
        return titleToScene.getValue(sceneTitle);
    }

    /**
     * @param sceneTitle the title of the scene
     * @return the Controller associated with the specified title
     */
    public GuiController getControllerBySceneTitle(SceneTitle sceneTitle){return titleToController.getValue(sceneTitle);}

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

    //TODO: JAVADOC
    public void setVirtualGame(VirtualGame virtualGame) {
        this.virtualGame = virtualGame;
    }

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
                    scene = new Scene(loader.load(), 1620, 780);
                } else {
                    scene = new Scene(loader.load(), 600, 400);
                }
                //scene.setCursor(new ImageCursor(new Image(getClass().getResourceAsStream(""))));
                titleToScene.put(sceneTitle, scene);
                GuiController controller = loader.getController();
                //Sets the scene's controller


                controller.setGui(this, this.menuController, this.roomController, this.gameController);
                titleToController.put(sceneTitle, controller);
            }
        } catch (IOException e) {
            System.out.println("Senes setup failed");
            stop();
        }
//        currentScene = titleToScene.getValue(SceneTitle.WELCOME);
        currentScene = titleToScene.getValue(SceneTitle.SERVER_SETUP);
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
