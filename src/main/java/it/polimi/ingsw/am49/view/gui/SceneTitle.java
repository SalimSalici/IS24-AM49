package it.polimi.ingsw.am49.view.gui;


public enum SceneTitle {
    WELCOME,
    MAIN_MENU,
    CHANGE_USERNAME,
    CREATE_ROOM,
    ROOM,
    STARTER_CARD,
    OBJECTIVE_CARDS,
    WAITING;
    //OVERVIEW;

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
            case CHANGE_USERNAME -> fileName = "changeUsername.fxml";
            case CREATE_ROOM -> fileName = "createRoom.fxml";
            //case OVERVIEW -> fileName = "overview.fxml";
        }

        return fileName;
    }

    public String getFilePath() {
        System.out.println("/it/polimi/ingsw/am49/fxml/" + getFileName());
        return "/it/polimi/ingsw/am49/fxml/" + getFileName();
    }
}