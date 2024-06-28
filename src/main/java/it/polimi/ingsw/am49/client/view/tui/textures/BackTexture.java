package it.polimi.ingsw.am49.client.view.tui.textures;

/**
 * Binds the first line of the .txt files representing the front of the cards with the correct back side .txt file.
 */
public enum BackTexture {

    /**
     * Resource back textures in various colors.
     */
    RG("/it/polimi/ingsw/am49/textures/tui/resource_back_green.txt"),
    RB("/it/polimi/ingsw/am49/textures/tui/resource_back_blue.txt"),
    RR("/it/polimi/ingsw/am49/textures/tui/resource_back_red.txt"),
    RP("/it/polimi/ingsw/am49/textures/tui/resource_back_purple.txt"),

    /**
     * Gold back textures in various colors.
     */
    GB("/it/polimi/ingsw/am49/textures/tui/gold_back_blue.txt"),
    GG("/it/polimi/ingsw/am49/textures/tui/gold_back_green.txt"),
    GR("/it/polimi/ingsw/am49/textures/tui/gold_back_red.txt"),
    GP("/it/polimi/ingsw/am49/textures/tui/gold_back_purple.txt"),

    /**
     * Starter cards back textures.
     */
    ID_81("/it/polimi/ingsw/am49/textures/tui/81_back.txt"),
    ID_82("/it/polimi/ingsw/am49/textures/tui/82_back.txt"),
    ID_83("/it/polimi/ingsw/am49/textures/tui/83_back.txt"),
    ID_84("/it/polimi/ingsw/am49/textures/tui/84_back.txt"),
    ID_85("/it/polimi/ingsw/am49/textures/tui/85_back.txt"),
    ID_86("/it/polimi/ingsw/am49/textures/tui/86_back.txt"),

    /**
     * Objective back texture.
     */
    OB("/it/polimi/ingsw/am49/textures/tui/objective_back.txt");

    /**
     * The file name of the back texture.
     */
    private final String fileName;

    /**
     * Constructs a BackTexture enum with the specified file name.
     *
     * @param fileName the file name of the back texture
     */
    BackTexture(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Retrieves the file name of the back texture corresponding to the given code.
     *
     * @param code the code of the back texture
     * @return the file name of the back texture, or null if the code does not match any texture
     */
    public static String getFileName(String code) {
        for (BackTexture texture : BackTexture.values()) {
            if (texture.name().equals(code)) {
                return texture.getValue();
            }
        }
        return null;
    }

    /**
     * Returns the file name of the back texture.
     *
     * @return the file name
     */
    public String getValue(){
        return this.fileName;
    }
}
