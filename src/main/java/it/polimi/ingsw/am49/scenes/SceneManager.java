package it.polimi.ingsw.am49.scenes;

public class SceneManager {
    private Scene currentScene;
    private boolean running;

    public void start(Scene initialScene) {
        this.running = true;
        this.currentScene = initialScene;
        while (running) {
            this.currentScene.play();
        }
    }

    public void setScene(Scene scene) {
        this.currentScene = scene;
    }
    public Scene getScene() {
        return this.currentScene;
    }

    public void stop() {
        this.running = false;
    }
}
