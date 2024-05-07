package it.polimi.ingsw.am49.view.tui.textures;

public enum AnsiColor {
    ANSI_RESET("\u001B[0m"),
    ANSI_RED("\u001B[31m"),
    ANSI_GREEN("\u001B[32m"),
    ANSI_YELLOW("\u001B[33m"),
    ANSI_BLUE("\u001B[34m");

    private final String value;

    private AnsiColor(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}

