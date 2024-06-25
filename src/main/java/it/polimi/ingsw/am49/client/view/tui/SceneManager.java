package it.polimi.ingsw.am49.client.view.tui;

import it.polimi.ingsw.am49.client.view.tui.scenes.*;
import it.polimi.ingsw.am49.common.gameupdates.ChatMSG;
import it.polimi.ingsw.am49.client.controller.GameController;
import it.polimi.ingsw.am49.client.controller.MenuController;
import it.polimi.ingsw.am49.client.controller.RoomController;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.common.reconnectioninfo.RoomInfo;
import it.polimi.ingsw.am49.common.Server;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages the different scenes in the text-based user interface.
 */
public class SceneManager {
    private final HashMap<SceneType, Scene> scenes;
    private final HashMap<VirtualPlayer, Scene> playerScenes;
    private ServerScene serverScene;
    private RoomScene roomScene;
    private ChatScene chatScene;
    private StarterCardScene starterCardScene;
    private ChooseObjectiveCardScene chooseObjectiveCardScene;
    private Scene currentScene;
    private boolean running;
    private final Scanner scanner;

    private final MenuController menuController;
    private final RoomController roomController;
    private final GameController gameController;
    private VirtualGame virtualGame;

    private final ExecutorService executor;

    /**
     * Constructs a SceneManager with the specified controllers.
     *
     * @param menuController the menu controller
     * @param roomController the room controller
     * @param gameController the game controller
     */
    public SceneManager(MenuController menuController, RoomController roomController, GameController gameController) {
        this.scenes = new HashMap<>();
        this.playerScenes = new HashMap<>();
        this.running = true;
        this.scanner = new Scanner(System.in);

        this.menuController = menuController;
        this.roomController = roomController;
        this.gameController = gameController;

        this.executor = Executors.newFixedThreadPool(10);
    }

    /**
     * Initializes the scenes and starts the main loop.
     */
    public void initialize() {

        this.roomScene = new RoomScene(this, roomController);
        this.starterCardScene = new StarterCardScene(this, gameController);
        this.chooseObjectiveCardScene = new ChooseObjectiveCardScene(this, gameController);
        this.chatScene = new ChatScene(this, gameController);
        this.serverScene = new ServerScene(this, menuController);

        this.scenes.put(SceneType.WELCOME_SCENE, new WelcomeScene(this));
        this.scenes.put(SceneType.MAIN_MENU_SCENE, new MainMenuScene(this, menuController));
        this.scenes.put(SceneType.OVERVIEW_SCENE, new GameOverviewScene(this, gameController));
        this.scenes.put(SceneType.END_GAME_SCENE, new EndGameScene(this, gameController));
        this.scenes.put(SceneType.CHAT_SCENE, chatScene);

//        this.switchScene(SceneType.WELCOME_SCENE);
        this.forceServerSelection(null);

        this.loop();
    }

    /**
     * Terminates the main loop.
     */
    public synchronized void terminate() {
        this.running = false;
    }

    /**
     * Main loop that handles user input.
     */
    private void loop() {
        while (running) {
            String input = this.scanner.nextLine();
            synchronized (this) {
                this.executor.submit(() -> this.currentScene.handleInput(input));
            }
        }
    }

    /**
     * Switches to the specified scene.
     *
     * @param scene the scene to switch to
     */
    public synchronized void switchScene(Scene scene) {
        if (this.currentScene == scene) return;
        if (this.currentScene != null) this.currentScene.unfocus();
        this.currentScene = scene;
        this.currentScene.focus();
//        this.currentScene.printView();
    }

    /**
     * Switches to the scene of the specified type.
     *
     * @param sceneType the type of the scene to switch to
     */
    public synchronized void switchScene(SceneType sceneType) {
        this.switchScene(this.scenes.get(sceneType));
    }

    /**
     * Switches to the scene associated with the specified player.
     *
     * @param player the player whose scene to switch to
     */
    public synchronized void switchScene(VirtualPlayer player) {
        this.switchScene(this.playerScenes.get(player));
    }

    /**
     * Switches to the room scene with the specified room information.
     *
     * @param roomInfo the room information
     */
    public synchronized void switchScene(RoomInfo roomInfo) {
        this.roomScene.setRoomInfo(roomInfo);
        this.switchScene(roomScene);
    }

    /**
     * Adds a chat message to the chat scene.
     *
     * @param chatMSG the chat message to add
     */
    public synchronized void chatMessage(ChatMSG chatMSG) {
        this.chatScene.addChatMessage(chatMSG);
    }

    /**
     * Updates the room scene with the specified room information and message.
     *
     * @param roomInfo the room information
     * @param message  the message
     */
    public synchronized void roomUpdate(RoomInfo roomInfo, String message) {
        if (this.currentScene.equals(this.roomScene))
            this.roomScene.roomUpdate(roomInfo, message);
    }

    /**
     * Handles the start of the game with the specified starter card ID.
     *
     * @param starterCardId the starter card ID
     */
    public synchronized void gameStarted(int starterCardId) {
        this.chatScene.clearMessages();
        this.initializePlayerScenes();
        this.starterCardScene.setStarterCardId(starterCardId);
        this.switchScene(this.starterCardScene);
    }

    /**
     * Initializes the player scenes.
     */
    public synchronized void initializePlayerScenes() {
        this.destroyPlayerScenes();
        for (VirtualPlayer player : this.virtualGame.getPlayers())
            this.playerScenes.put(player, new PlayerScene(this, virtualGame, player, gameController));
    }

    /**
     * Updates the choose objective card scene with the specified objective card IDs.
     *
     * @param objectiveCardIds the objective card IDs
     */
    public synchronized void chooseObjectiveCardUpdate(List<Integer> objectiveCardIds) {
        this.chooseObjectiveCardScene.setObjectiveCardIds(objectiveCardIds);
        this.switchScene(this.chooseObjectiveCardScene);
    }

    /**
     * Checks if the specified scene is currently focused.
     *
     * @param scene the scene to check
     * @return true if the scene is focused, false otherwise
     */
    public synchronized boolean isFocused(Scene scene) {
        return this.currentScene.equals(scene);
    }

    /**
     * Destroys the player scenes.
     */
    public synchronized void destroyPlayerScenes() {
        for (VirtualPlayer player : this.playerScenes.keySet())
            this.playerScenes.get(player).unfocus();
        this.playerScenes.clear();
    }

    /**
     * Sets the server for the controllers.
     *
     * @param server the server to set
     */
    public synchronized void setServer(Server server){
        this.menuController.setServer(server);
        this.roomController.setServer(server);
        this.gameController.setServer(server);
    }

    public synchronized void forceServerSelection(String message) {
        this.switchScene(serverScene);
        if (message != null)
            this.serverScene.showError(message);
    }

    /**
     * Gets the room controller.
     *
     * @return the room controller
     */
    public RoomController getRoomController() {
        return this.roomController;
    }

    /**
     * Sets the virtual game.
     *
     * @param virtualGame the virtual game to set
     */
    public void setVirtualGame(VirtualGame virtualGame) {
        this.virtualGame = virtualGame;
    }

    /**
     * Gets the virtual game.
     *
     * @return the virtual game
     */
    public VirtualGame getVirtualGame() {
        return virtualGame;
    }
}
