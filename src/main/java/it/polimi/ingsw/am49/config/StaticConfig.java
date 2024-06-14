package it.polimi.ingsw.am49.config;

public abstract class StaticConfig {
    public final static int boardMatWidth = 50;
    public final static int boardMatHeight = 50;
    public final static int starterCardRow = 25;
    public final static int starterCardCol = 25;
    public final static int maxPlayers = 4;
    public final static int cardsInHand = 3;

    public static boolean tuiColors = true;

    public static void disableTuiColors() {
        tuiColors = false;
    }

    public static void enableTuiColors() {
        tuiColors = true;
    }
}
