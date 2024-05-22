package it.polimi.ingsw.am49.view.tui;

import it.polimi.ingsw.am49.view.tui.textures.AnsiColor;
import it.polimi.ingsw.am49.view.tui.textures.ColoredChar;

public class TuiCardRenderer {
    private final ColoredChar[][] buffer;

    private final int width;
    private final int height;

    public TuiCardRenderer(int width, int height) {
        this.width = width;
        this.height = height;
        this.buffer = new ColoredChar[height][width];
        this.clear();
    }

    public void clear() {
        for (int i = 0; i < buffer.length; i++) {
            for (int j = 0; j < buffer[i].length; j++) {
                buffer[i][j] = new ColoredChar(' ', AnsiColor.ANSI_RESET);
            }
        }
    }

    // Draw a card on the board at specific coordinates
    public void draw(ColoredChar[][] texture, int x, int y) {
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

    public void print() {
        for (ColoredChar[] row : buffer) {
            StringBuilder line = new StringBuilder();
            for (ColoredChar cell : row) {
                line.append(cell);
            }
            System.out.println(line);
        }
    }

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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
