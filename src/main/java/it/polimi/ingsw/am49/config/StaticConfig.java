package it.polimi.ingsw.am49.config;

public abstract class StaticConfig {
    public final static int boardMatWidth = 50;
    public final static int boardMatHeight = 50;
    public final static int starterCardRow = 25;
    public final static int starterCardCol = 25;
    public final static int maxPlayers = 4;
    public final static int cardsInHand = 3;

    public static boolean tuiColors = true;

    /**
     * Disables the color display in the text-based user interface.
     */
    public static void disableTuiColors() {
        tuiColors = false;
    }

    /**
     * Enables the color display in the text-based user interface.
     */
    public static void enableTuiColors() {
        tuiColors = true;
    }
}


