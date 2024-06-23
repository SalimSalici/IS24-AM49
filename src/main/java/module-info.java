module it.polimi.ingsw.am49 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.rmi;
    requires java.logging;

    opens it.polimi.ingsw.am49.server.model.cards to com.google.gson;
    opens it.polimi.ingsw.am49.server.model.cards.placeables to com.google.gson;
    opens it.polimi.ingsw.am49.server.model.cards.objectives to com.google.gson;
    opens it.polimi.ingsw.am49.client.view.gui.controllers to javafx.fxml;
    exports it.polimi.ingsw.am49.common.enumerations to com.google.gson;
    exports it.polimi.ingsw.am49.server to java.rmi;
    exports it.polimi.ingsw.am49.common.exceptions to java.rmi;
    exports it.polimi.ingsw.am49.client to java.rmi;
    exports it.polimi.ingsw.am49.common.actions to java.rmi;
    exports it.polimi.ingsw.am49.server.controller to java.rmi;
    exports it.polimi.ingsw.am49.common.gameupdates to java.rmi;
    exports it.polimi.ingsw.am49.server.controller.room to java.rmi;

    exports it.polimi.ingsw.am49.client.view.gui to javafx.fxml;
    opens it.polimi.ingsw.am49.client.view.gui to javafx.graphics;
    exports it.polimi.ingsw.am49.common.reconnectioninfo to java.rmi;
}