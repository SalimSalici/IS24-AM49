package it.polimi.ingsw.am49.view.tui;

import it.polimi.ingsw.am49.chat.ChatMSG;
import it.polimi.ingsw.am49.client.TuiApp;
import it.polimi.ingsw.am49.client.controller.GameController;
import it.polimi.ingsw.am49.client.controller.MenuController;
import it.polimi.ingsw.am49.client.controller.RoomController;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.controller.gameupdates.GameStartedUpdate;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.view.tui.scenes.*;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SceneManager {

    private final TuiApp tuiApp;
    private final HashMap<SceneType, Scene> scenes;
    private final HashMap<VirtualPlayer, Scene> playerScenes;
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

    private final ExecutorService executor;

    public SceneManager(TuiApp tuiApp) {
        this.tuiApp = tuiApp;
        this.scenes = new HashMap<>();
        this.playerScenes = new HashMap<>();
        this.running = true;
        this.scanner =new Scanner(System.in);

        this.menuController = new MenuController(tuiApp.getServer(), tuiApp);
        this.roomController = new RoomController(tuiApp.getServer(), tuiApp);
        this.gameController = new GameController(tuiApp.getServer(), tuiApp);

        this.executor = Executors.newFixedThreadPool(10);
    }

    public void initialize() {

        this.roomScene = new RoomScene(this, this.tuiApp, roomController);
        this.starterCardScene = new StarterCardScene(this, this.tuiApp, gameController);
        this.chooseObjectiveCardScene = new ChooseObjectiveCardScene(this, this.tuiApp, gameController);
        this.chatScene = new ChatScene(this, this.tuiApp, gameController);

        this.scenes.put(SceneType.WELCOME_SCENE, new WelcomeScene(this, this.tuiApp));
        this.scenes.put(SceneType.MAIN_MENU_SCENE, new MainMenuScene(this, this.tuiApp, this.menuController));
        this.scenes.put(SceneType.OVERVIEW_SCENE, new GameOverviewScene(this, this.tuiApp, gameController));
        this.scenes.put(SceneType.END_GAME_SCENE, new EndGameScene(this, this.tuiApp));
        this.scenes.put(SceneType.CHAT_SCENE, chatScene);


        this.switchScene(SceneType.WELCOME_SCENE);

        this.loop();
    }

    public synchronized void terminate() {
        this.running = false;
    }

    private void loop() {
        while (running) {
            String input = this.scanner.nextLine();
            synchronized (this) {
                this.executor.submit(() -> this.currentScene.handleInput(input));
            }
        }
    }

    public synchronized void switchScene(Scene scene) {
        if (this.currentScene != null)
            this.currentScene.unfocus();
        this.currentScene = scene;
        this.currentScene.focus();
        this.currentScene.printView();
    }

    public synchronized void switchScene(SceneType sceneType) {
        this.switchScene(this.scenes.get(sceneType));
    }

    public synchronized void switchScene(VirtualPlayer player) {
        this.switchScene(this.playerScenes.get(player));
    }

    public synchronized void switchScene(RoomInfo roomInfo) {
        this.roomScene.setRoomInfo(roomInfo);
        this.switchScene(roomScene);
    }

    public synchronized void chatMessage(ChatMSG chatMSG) {
        this.chatScene.addChatMessage(chatMSG);
    }

    public synchronized void roomUpdate(RoomInfo roomInfo, String message) {
        if (this.currentScene.equals(this.roomScene))
            this.roomScene.roomUpdate(roomInfo, message);
    }

    public synchronized void gameStarted(GameStartedUpdate gameStartedUpdate) {
        this.initializePlayerScenes();
        this.starterCardScene.setStarterCardId(gameStartedUpdate.starterCardId());
        this.switchScene(this.starterCardScene);
        this.chatScene.clearMessages();
    }

    public synchronized void initializePlayerScenes() {
        this.destroyPlayerScenes();
        for (VirtualPlayer player : this.tuiApp.getVirtualGame().getPlayers())
            this.playerScenes.put(player, new PlayerScene(this, this.tuiApp, player, gameController));
    }

    public synchronized void chooseObjectiveCardUpdate(List<Integer> objectiveCardIds) {
        this.chooseObjectiveCardScene.setObjectiveCardIds(objectiveCardIds);
        this.switchScene(this.chooseObjectiveCardScene);
    }

    public synchronized boolean isFocused(Scene scene) {
        return this.currentScene.equals(scene);
    }

    public synchronized void destroyPlayerScenes() {
        for (VirtualPlayer player : this.playerScenes.keySet())
            this.playerScenes.get(player).unfocus();
        this.playerScenes.clear();
    }

    public synchronized void setServer(Server server){
        this.menuController.setServer(server);
        this.roomController.setServer(server);
        this.gameController.setServer(server);
    }

    public RoomController getRoomController() {
        return this.roomController;
    }
}
