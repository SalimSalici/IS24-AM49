module it.polimi.ingsw.am49 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.rmi;
    requires java.logging;

    opens it.polimi.ingsw.am49 to javafx.fxml;
    opens it.polimi.ingsw.am49.model.cards to com.google.gson;
    opens it.polimi.ingsw.am49.model.cards.placeables to com.google.gson;
    opens it.polimi.ingsw.am49.model.cards.objectives to com.google.gson;
    opens it.polimi.ingsw.am49.view.gui.controllers to javafx.fxml;
    exports it.polimi.ingsw.am49;
    exports it.polimi.ingsw.am49.model.enumerations to com.google.gson;
    exports it.polimi.ingsw.am49.server to java.rmi;
    exports it.polimi.ingsw.am49.server.exceptions to java.rmi;
    exports it.polimi.ingsw.am49.client to java.rmi;
    exports it.polimi.ingsw.am49.model.actions to java.rmi;
    exports it.polimi.ingsw.am49.controller to java.rmi;
    exports it.polimi.ingsw.am49.controller.gameupdates to java.rmi;
    exports it.polimi.ingsw.am49.controller.room to java.rmi;
    exports it.polimi.ingsw.am49.chat to java.rmi;

    exports it.polimi.ingsw.am49.view.gui to javafx.fxml;
    opens it.polimi.ingsw.am49.view.gui to javafx.graphics;
}