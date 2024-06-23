package it.polimi.ingsw.am49.client.view.gui;

/**
 * Enumerates the different scenes used in the GUI
 * Each enum constant corresponds to a specific scene in the game.
 *
 * This enum facilitates managing and accessing the file paths for the FXML files
 * that define the layout of each scene.
 */
public enum SceneTitle {
    WELCOME,
    SERVER_SETUP,
    MAIN_MENU,
    CHANGE_USERNAME,
    CREATE_ROOM,
    ROOM,
    STARTER_CARD,
    OBJECTIVE_CARDS,
    WAITING,
    BOARD,
    OVERVIEW,
    CHAT,
    END_GAME;

    /**
     * @return The FXML filename corresponding to the scene, or null if no mapping is defined.
     */
    public String getFileName() {
        String fileName = null;

        switch (this) {
            case WELCOME -> fileName = "welcome.fxml";
            case SERVER_SETUP -> fileName = "serverSetUp.fxml";
            case MAIN_MENU -> fileName = "mainMenu.fxml";
            case ROOM -> fileName = "room.fxml";
            case STARTER_CARD -> fileName= "starterCard.fxml";
            case WAITING -> fileName = "waitForOther.fxml";
            case OBJECTIVE_CARDS -> fileName = "objectiveCards.fxml";
            case CHANGE_USERNAME -> fileName = "changeUsername.fxml";
            case CREATE_ROOM -> fileName = "createRoom.fxml";
            case OVERVIEW -> fileName = "overview.fxml";
            case BOARD -> fileName = "board.fxml";
            case CHAT -> fileName = "chat.fxml";
            case END_GAME -> fileName = "endGame.fxml";
        }

        return fileName;
    }

    /**
     * @return The full path of the FXML file
     */
    public String getFilePath() {
        System.out.println("/it/polimi/ingsw/am49/fxml/" + getFileName());
        return "/it/polimi/ingsw/am49/fxml/" + getFileName();
    }
}