package it.polimi.ingsw.am49.client.view.tui.renderers;

import it.polimi.ingsw.am49.client.view.tui.textures.AnsiColor;
import it.polimi.ingsw.am49.client.view.tui.textures.ColoredChar;

/**
 * The TuiCardRenderer class is responsible for rendering cards on the TUI.
 */
public class TuiCardRenderer {
    private final ColoredChar[][] buffer;

    private final int width;
    private final int height;

    /**
     * Constructs a TuiCardRenderer with the specified width and height.
     *
     * @param width  the width of the buffer
     * @param height the height of the buffer
     */
    public TuiCardRenderer(int width, int height) {
        this.width = width;
        this.height = height;
        this.buffer = new ColoredChar[height][width];
        this.clear();
    }

    /**
     * Clears the buffer by filling it with spaces and resetting the color.
     */
    public void clear() {
        for (int i = 0; i < buffer.length; i++) {
            for (int j = 0; j < buffer[i].length; j++) {
                buffer[i][j] = new ColoredChar(' ', AnsiColor.ANSI_RESET);
            }
        }
    }

    /**
     * Draws a texture on the buffer at the specified coordinates.
     *
     * @param texture the texture to draw
     * @param x       the x-coordinate where the texture should be drawn
     * @param y       the y-coordinate where the texture should be drawn
     */
    public void draw(ColoredChar[][] texture, int x, int y) {

        if (texture == null) return;

        int textureHeight = texture.length;
        int textureWidth = texture[0].length;

        x -= 7;
        y -= 2;

        for (int i = 0; i < textureHeight; i++) {
            int boardRow = y + i;
            if (boardRow < 0 || boardRow >= buffer.length) {
                continue; // Skip rows outside the board
            }

            for (int j = 0; j < textureWidth; j++) {
                int boardCol = x + j;
                if (boardCol < 0 || boardCol >= buffer[boardRow].length) {
                    continue; // Skip columns outside the board
                }

                buffer[boardRow][boardCol] = texture[i][j];
            }
        }
    }

    /**
     * Draws a single {@link ColoredChar } at the given postion.
     * @param c   the colored character
     * @param row the row where to print the character
     * @param col the column where to print the character
     */
    public void drawPixel(ColoredChar c, int row, int col) {
        if (row < this.height && col < this.width)
            this.buffer[row][col] = c;
    }

    /**
     * Prints the buffer to the console.
     */
    public void print() {
        for (ColoredChar[] row : buffer) {
            StringBuilder line = new StringBuilder();
            for (ColoredChar cell : row) {
                line.append(cell);
            }
            System.out.println(line);
        }
    }

    /**
     * Prints the buffer to the console with a border around it.
     */
    public void printWithBorder() {
        String horBorder = "-".repeat(this.width + 2);
        System.out.println(horBorder);

        for (ColoredChar[] row : buffer) {
            StringBuilder line = new StringBuilder();
            line.append("|");
            for (ColoredChar cell : row) {
                line.append(cell);
            }
            line.append("|");
            System.out.println(line);
        }

        System.out.println(horBorder);
    }

    /**
     * Returns the width of the buffer.
     *
     * @return the width of the buffer
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the height of the buffer
     */
    public int getHeight() {
        return height;
    }
}
