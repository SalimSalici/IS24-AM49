package it.polimi.ingsw.am49.view.gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class GuiEntry extends Application {

    public static GuiManager guiManagerInstance;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        guiManagerInstance.start(stage);
    }
}
