package it.polimi.ingsw.am49.view.gui;


public enum SceneTitle {
    WELCOME,
    MAIN_MENU,
    ROOM,
    STARTER_CARD,
    OBJECTIVE_CARDS,
    WAITING;
//    OVERVIEW,
//    CHOOSE_OBJECTIVE,
//    GAME_OVER;

    public String getFileName() {
        String fileName = null;

        switch (this) {
            case WELCOME -> fileName = "welcome.fxml";
            case MAIN_MENU -> fileName = "mainMenu.fxml";
            case ROOM -> fileName = "room.fxml";
            case STARTER_CARD -> fileName= "starterCard.fxml";
            case WAITING -> fileName = "waitForOther.fxml";
            case OBJECTIVE_CARDS -> fileName = "objectiveCards.fxml";
        }

        return fileName;
    }

    public String getFilePath() {
        System.out.println("/it/polimi/ingsw/am49/fxml/" + getFileName());
        return "/it/polimi/ingsw/am49/fxml/" + getFileName();
    }
}