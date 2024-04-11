module it.polimi.ingsw.am49 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens it.polimi.ingsw.am49 to javafx.fxml;
    opens it.polimi.ingsw.am49.model.cards to com.google.gson;
    opens it.polimi.ingsw.am49.model.cards.placeables to com.google.gson;
    opens it.polimi.ingsw.am49.model.cards.objectives to com.google.gson;
    exports it.polimi.ingsw.am49;
    exports it.polimi.ingsw.am49.model.enumerations to com.google.gson;
}