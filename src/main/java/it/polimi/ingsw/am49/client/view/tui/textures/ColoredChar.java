package it.polimi.ingsw.am49.client.view.tui.textures;

import it.polimi.ingsw.am49.client.ClientConfig;

/**
 * Represents a character with an associated ANSI color for the TUI version of the game.
 */
public class ColoredChar {

    /**
     * The character to be displayed.
     */
    private final char character;

    /**
     * The ANSI color associated with the character.
     */
    private final AnsiColor color;

    /**
     * Constructs a new ColoredChar with the specified character and color.
     *
     * @param character the character to be displayed
     * @param color the ANSI color associated with the character
     */
    public ColoredChar(char character, AnsiColor color) {
        this.character = character;
        this.color = color;
    }

    /**
     * Returns the character to be displayed.
     *
     * @return the character
     */
    public char getCharacter() {
        return this.character;
    }

    /**
     * Returns the ANSI color associated with the character.
     *
     * @return the ANSI color
     */
    public AnsiColor getAnsiColor() {
        return this.color;
    }

    /**
     * Returns a string representation of the ColoredChar.
     * If colors are enabled in the client configuration, the string includes ANSI color codes.
     * Otherwise, it returns only the character.
     *
     * @return the string representation of the ColoredChar
     */
    @Override
    public String toString() {
        if (ClientConfig.getColors())
            return this.color.toString() + character + AnsiColor.ANSI_RESET;
        else
            return String.valueOf(character);
    }
}