
package it.polimi.ingsw.am49;

import it.polimi.ingsw.am49.view.gui.SceneTitle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        System.out.println(SceneTitle.WELCOME.getFilePath());
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(SceneTitle.WELCOME.getFilePath()));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("My JavaFX Application");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}