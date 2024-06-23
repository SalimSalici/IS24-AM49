package it.polimi.ingsw.am49.client.view.tui.textures;

import it.polimi.ingsw.am49.config.StaticConfig;

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

    public AnsiColor getAnsiColor() {
        return this.color;
    }

    @Override
    public String toString() {
        if (StaticConfig.tuiColors)
            return this.color.toString() + character + AnsiColor.ANSI_RESET;
        else
            return String.valueOf(character);
    }
}