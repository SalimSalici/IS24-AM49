package it.polimi.ingsw.am49.view.tui.textures;

public class TuiTexture {
    private final ColoredChar[][] buffer;

    public TuiTexture(AnsiColor ansiColor) {
        this.buffer = new ColoredChar[5][15];
        initializeCard(ansiColor);
    }

    private void initializeCard(AnsiColor borderColor) {
        // Example: Create a card with varied colors
        AnsiColor emptySpaceColor = AnsiColor.ANSI_GREEN;
        String[] borders = {
                "+-------------+",
                "| W    3    '-|",
                "|      W      |",
                "| L  WWWWW  ,-|",
                "+-------------+"
        };

        for (int i = 0; i < borders.length; i++) {
            for (int j = 0; j < borders[i].length(); j++) {
                char currentChar = borders[i].charAt(j);
                AnsiColor color = (currentChar == '+' || currentChar == '-' || currentChar == '|') ? borderColor : emptySpaceColor;
                buffer[i][j] = new ColoredChar(currentChar, color);
            }
        }
    }

    public ColoredChar[][] getBuffer() {
        return this.buffer;
    }
}
