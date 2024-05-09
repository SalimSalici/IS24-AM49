package it.polimi.ingsw.am49.view.tui.textures;

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
    ANSI_BRIGHT_CYAN("\u001B[96m");

    private final String value;

    private AnsiColor(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}

