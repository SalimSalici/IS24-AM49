module it.polimi.ingsw.am49 {
    requires javafx.controls;
    requires javafx.fxml;


    opens it.polimi.ingsw.am49 to javafx.fxml;
    exports it.polimi.ingsw.am49;
}