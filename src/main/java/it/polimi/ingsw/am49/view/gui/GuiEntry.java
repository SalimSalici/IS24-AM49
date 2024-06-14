package it.polimi.ingsw.am49.view.gui;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Entry point for the GUI application.
 * This class initializes and starts the JavaFX application.
 */
public class GuiEntry extends Application {

    public static GuiManager guiManagerInstance;

    /**
     * The main method to launch the JavaFX application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        guiManagerInstance.start(stage);
    }
}
