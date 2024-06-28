package it.polimi.ingsw.am49.client.view.tui.textures;

import it.polimi.ingsw.am49.client.ClientConfig;

/**
 * Represents a texture for the TUI version of the game, containing both front and back buffers.
 */
public class TuiTexture {

    /**
     * The buffer representing the front side of the texture.
     */
    private ColoredChar[][] frontBuffer;

    /**
     * The buffer representing the back side of the texture.
     */
    private ColoredChar[][] backBuffer;

    /**
     * Constructs a new TuiTexture with the specified front and back buffers.
     * If colors are disabled in the client configuration, applies textual colors to the buffers.
     *
     * @param frontBuffer the front buffer of the texture
     * @param backBuffer the back buffer of the texture
     */
    public TuiTexture(ColoredChar[][] frontBuffer, ColoredChar[][] backBuffer) {
        this.frontBuffer = frontBuffer;
        this.backBuffer = backBuffer;

        if (!ClientConfig.getColors()) {
            this.textualColorOnBuffer(frontBuffer);
            this.textualColorOnBuffer(backBuffer);
        }
    }

    /**
     * Returns the front buffer of the texture.
     *
     * @return the front buffer
     */
    public ColoredChar[][] getFrontBuffer() {
        return frontBuffer;
    }

    /**
     * Sets the front buffer of the texture.
     *
     * @param frontBuffer the new front buffer
     */
    public void setFrontBuffer(ColoredChar[][] frontBuffer) {
        this.frontBuffer = frontBuffer;
    }

    /**
     * Returns the back buffer of the texture.
     *
     * @return the back buffer
     */
    public ColoredChar[][] getBackBuffer() {
        return backBuffer;
    }

    /**
     * Sets the back buffer of the texture.
     *
     * @param backBuffer the new back buffer
     */
    public void setBackBuffer(ColoredChar[][] backBuffer) {
        this.backBuffer = backBuffer;
    }

    /**
     * Applies textual color to the specified buffer if colors are disabled.
     *
     * @param buffer the buffer to apply textual color to
     */
    private void textualColorOnBuffer(ColoredChar[][] buffer) {
        String textualColor = buffer[0][0].getAnsiColor().toTextualColor();
        int row = 0;
        int col = 5;
        for (int i = 0; i < textualColor.length() && i < buffer[row].length; i++) {
            buffer[row][col + i] = new ColoredChar(textualColor.charAt(i), AnsiColor.ANSI_RESET);
        }
    }
}
