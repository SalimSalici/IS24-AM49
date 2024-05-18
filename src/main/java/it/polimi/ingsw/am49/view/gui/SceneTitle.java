package it.polimi.ingsw.am49.view.gui;


public enum SceneTitle {
    WELCOME,
    MAIN_MENU,
    ROOM;
//    OVERVIEW,
//    CHOOSE_STARTER,
//    CHOOSE_OBJECTIVE,
//    GAME_OVER;

    public String getFileName() {
        String fileName = null;

        switch (this) {
            case WELCOME -> fileName = "welcome.fxml";
            case MAIN_MENU -> fileName = "mainMenu.fxml";
            case ROOM -> fileName = "room.fxml";
        }

        return fileName;
    }

    public String getFilePath() {
        System.out.println("/it/polimi/ingsw/am49/fxml/" + getFileName());
        return "/it/polimi/ingsw/am49/fxml/" + getFileName();
    }
}