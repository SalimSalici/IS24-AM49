package it.polimi.ingsw.am49.view.tui.textures;

/**
 * Binds the first line of the .txt files rapresenting the front of the cards with re correct back side .txt file
 */
public enum BackTexture {
    RG("/it/polimi/ingsw/am49/textures/tui/resource_back_green.txt"),
    RB("/it/polimi/ingsw/am49/textures/tui/resource_back_blue.txt"),
    RR("/it/polimi/ingsw/am49/textures/tui/resource_back_red.txt"),
    RP("/it/polimi/ingsw/am49/textures/tui/resource_back_purple.txt"),
    GB("/it/polimi/ingsw/am49/textures/tui/gold_back_blue.txt"),
    GG("/it/polimi/ingsw/am49/textures/tui/gold_back_green.txt"),
    GR("/it/polimi/ingsw/am49/textures/tui/gold_back_red.txt"),
    GP("/it/polimi/ingsw/am49/textures/tui/gold_back_purple.txt"),
    ID_81("/it/polimi/ingsw/am49/textures/tui/81_back.txt"),
    ID_82("/it/polimi/ingsw/am49/textures/tui/82_back.txt"),
    ID_83("/it/polimi/ingsw/am49/textures/tui/83_back.txt"),
    ID_84("/it/polimi/ingsw/am49/textures/tui/84_back.txt"),
    ID_85("/it/polimi/ingsw/am49/textures/tui/85_back.txt"),
    ID_86("/it/polimi/ingsw/am49/textures/tui/86_back.txt"),
    OB("/it/polimi/ingsw/am49/textures/tui/objective_back.txt");

    private String fileName;

    BackTexture(String fileName) { this.fileName = fileName; }



    public static String getFileName(String code) {
        for (BackTexture texture : BackTexture.values()) {
            if (texture.name().equals(code)) {
                return texture.getValue();
            }
        }
        return null;
    }

    private String getValue(){ return this.fileName; }
}
