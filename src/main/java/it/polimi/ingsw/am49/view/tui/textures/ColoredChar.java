package it.polimi.ingsw.am49.view.tui.textures;

public class ColoredChar {
    private final char character;
    private final AnsiColor color;

    public ColoredChar(char character, AnsiColor color) {
        this.character = character;
        this.color = color;
    }

    public char getCharacter() {
        return this.character;
    }

    public String getColorCode() {
        return this.color.toString();
    }

    @Override
    public String toString() {
        return this.color.toString() + character + AnsiColor.ANSI_RESET;
    }
}