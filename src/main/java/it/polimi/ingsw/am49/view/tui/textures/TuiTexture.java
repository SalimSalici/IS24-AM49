package it.polimi.ingsw.am49.view.tui.textures;

public class TuiTexture {
    private ColoredChar[][] frontBuffer;
    private ColoredChar[][] backBuffer;

    public TuiTexture(ColoredChar[][] frontBuffer, ColoredChar[][] backBuffer) {
        this.frontBuffer = frontBuffer;
        this.backBuffer = backBuffer;
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
}
