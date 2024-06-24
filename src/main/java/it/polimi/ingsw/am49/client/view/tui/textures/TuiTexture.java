package it.polimi.ingsw.am49.client.view.tui.textures;

import it.polimi.ingsw.am49.client.ClientConfig;
import it.polimi.ingsw.am49.config.StaticConfig;

public class TuiTexture {
    private ColoredChar[][] frontBuffer;
    private ColoredChar[][] backBuffer;

    public TuiTexture(ColoredChar[][] frontBuffer, ColoredChar[][] backBuffer) {
        this.frontBuffer = frontBuffer;
        this.backBuffer = backBuffer;

        if (!ClientConfig.getColors()) {
            this.textualColorOnBuffer(frontBuffer);
            this.textualColorOnBuffer(backBuffer);
        }
    }

    public ColoredChar[][] getFrontBuffer() {
        return frontBuffer;
    }

    public void setFrontBuffer(ColoredChar[][] frontBuffer) {
        this.frontBuffer = frontBuffer;
    }

    public ColoredChar[][] getBackBuffer() {
        return backBuffer;
    }

    public void setBackBuffer(ColoredChar[][] backBuffer) {
        this.backBuffer = backBuffer;
    }

    private void textualColorOnBuffer(ColoredChar[][] buffer) {
        String textualColor = buffer[0][0].getAnsiColor().toTextualColor();
        int row = 0;
        int col = 5;
        for (int i = 0; i < textualColor.length() && i < buffer[row].length; i++) {
            buffer[row][col + i] = new ColoredChar(textualColor.charAt(i), AnsiColor.ANSI_RESET);
        }
    }
}
