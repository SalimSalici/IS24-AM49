package it.polimi.ingsw.am49.view.tui.textures;

/**
 * This class will handle loading textures from disk for the CLI version of the game
 */
public class TuiTextureManager {
    private static TuiTextureManager instance;

    public static TuiTextureManager getInstance() {
        if (TuiTextureManager.instance == null)
            TuiTextureManager.instance = new TuiTextureManager();
        return TuiTextureManager.instance;
    }

    public TuiTexture getTexture(int id) {
        return new TuiTexture(AnsiColor.ANSI_YELLOW);
    };
}
