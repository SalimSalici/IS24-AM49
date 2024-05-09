package it.polimi.ingsw.am49.view.tui.textures;

import java.util.Random;

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
        Random random = new Random();
        AnsiColor c = AnsiColor.values()[random.nextInt(AnsiColor.values().length)];
        return new TuiTexture(c);
//        return new TuiTexture(AnsiColor.ANSI_BRIGHT_MAGENTA);
    };
}
