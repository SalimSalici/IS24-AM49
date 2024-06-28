package it.polimi.ingsw.am49.client.view.tui.textures;

import it.polimi.ingsw.am49.common.enumerations.Color;

import java.util.HashMap;
import java.util.Map;

public enum AnsiColor {
    ANSI_RESET("\u001B[0m"),
    ANSI_RED("\u001B[31m"),
    ANSI_BRIGHT_RED("\u001B[91m"),
    ANSI_GREEN("\u001B[32m"),
    ANSI_YELLOW("\u001B[33m"),
    ANSI_BLUE("\u001B[34m"),
    ANSI_MAGENTA("\u001B[35m"),
    ANSI_BRIGHT_MAGENTA("\u001B[95m"),
    ANSI_CYAN("\u001B[36m"),
    ANSI_WHITE("\u001B[37m"),
    ANSI_BRIGHT_CYAN("\u001B[96m");

    private final String value;

    private AnsiColor(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    /**
     * Creates the binding letter color for the reading of the .txt file representing the cards
     */
    private static final Map<Character, AnsiColor> colorMap = new HashMap<>();

    static {
        colorMap.put('R', ANSI_RED);
        colorMap.put('Y', ANSI_YELLOW);
        colorMap.put('G', ANSI_GREEN);
        colorMap.put('B', ANSI_BLUE);
        colorMap.put('M', ANSI_MAGENTA);
        colorMap.put('T', ANSI_WHITE);
    }

    public static AnsiColor getColorForChar(char c) {
        return colorMap.getOrDefault(c, ANSI_RESET); // Default: reset color
    }

    public static AnsiColor fromColor(Color color) {
        return switch (color) {
            case YELLOW -> AnsiColor.ANSI_YELLOW;
            case GREEN -> AnsiColor.ANSI_GREEN;
            case BLUE -> AnsiColor.ANSI_BLUE;
            case RED -> AnsiColor.ANSI_RED;
        };
    }

    public String toTextualColor() {
        return switch (this) {
            case ANSI_RESET -> "[NUL]";
            case ANSI_RED, ANSI_BRIGHT_RED -> "[RED]";
            case ANSI_GREEN -> "[GRE]";
            case ANSI_YELLOW -> "[YEL]";
            case ANSI_BLUE -> "[BLU]";
            case ANSI_MAGENTA, ANSI_BRIGHT_MAGENTA -> "[MAG]";
            case ANSI_CYAN, ANSI_BRIGHT_CYAN -> "[CYA]";
            case ANSI_WHITE -> "[WHI]";
        };
    }

}

