package it.polimi.ingsw.am49.view.tui.renderers;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualBoard;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualCard;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualTile;
import it.polimi.ingsw.am49.model.enumerations.RelativePosition;
import it.polimi.ingsw.am49.view.tui.textures.AnsiColor;
import it.polimi.ingsw.am49.view.tui.textures.ColoredChar;
import it.polimi.ingsw.am49.view.tui.textures.TuiTextureManager;

import java.util.ArrayList;
import java.util.List;

/**
 * The TuiBoardRenderer class is responsible for rendering a player's board on the TUI.
 */
public class TuiBoardRenderer {

    private final VirtualBoard virtualBoard;
    private final TuiCardRenderer renderer;

    /**
     * Constructs a TuiBoardRenderer with the specified virtual board.
     *
     * @param virtualBoard the virtual board to render
     */
    public TuiBoardRenderer(VirtualBoard virtualBoard) {
        this.virtualBoard = virtualBoard;
        this.renderer = new TuiCardRenderer(140, 30);
    }

    /**
     * Draws the neighbourhood of tiles around the specified tile coordinates.
     *
     * @param row the row of the center tile
     * @param col the column of the center tile
     */
    public void drawNeighbourhood(int row, int col) {
        this.renderer.clear();

        List<VirtualTile> neighbourhood = new ArrayList<>();
        VirtualTile centerTile = this.virtualBoard.getTile(row, col);
        if (centerTile == null) return;

        this.addToNeighbourhood(neighbourhood, centerTile);

        int centerDisplayRow = this.renderer.getHeight() / 2;
        int centerDisplayCol = this.renderer.getWidth() / 2;
        int expandedRow = centerTile.getExpandedRow();
        int expandedCol = centerTile.getExpandedCol();
        neighbourhood.stream().sorted().forEach((virtualTile) -> {
            int r = virtualTile.getRow();
            int c = virtualTile.getCol();
            int currExpandedRow = virtualTile.getExpandedRow();
            int currExpandedCol = virtualTile.getExpandedCol();
            int rowOffset = currExpandedRow - expandedRow;
            int colOffset = currExpandedCol - expandedCol;
            VirtualCard card = this.virtualBoard.getTile(r, c).getCard();

            this.renderer.draw(
                    TuiTextureManager.getInstance().getTexture(card.id(), card.flipped()),
                    centerDisplayCol + colOffset * 12,
                    centerDisplayRow + rowOffset * 3
            );
        });

        this.renderer.drawPixel(new ColoredChar('^', AnsiColor.ANSI_WHITE), centerDisplayRow + 2, centerDisplayCol);
    }

    /**
     * Prints the board with a border around it.
     */
    public void printBoard() {
        this.renderer.printWithBorder();
    }

    /**
     * Adds a tile and its neighbours to the neighbourhood list.
     *
     * @param neighbourhood the list of tiles in the neighbourhood
     * @param tile the tile to add
     */
    private void addToNeighbourhood(List<VirtualTile> neighbourhood, VirtualTile tile) {
        if (tile == null || neighbourhood.contains(tile)) return;
        neighbourhood.add(tile);
        this.addToNeighbourhood(neighbourhood, tile.getNeighbourTile(RelativePosition.TOP_LEFT));
        this.addToNeighbourhood(neighbourhood, tile.getNeighbourTile(RelativePosition.TOP_RIGHT));
        this.addToNeighbourhood(neighbourhood, tile.getNeighbourTile(RelativePosition.BOTTOM_LEFT));
        this.addToNeighbourhood(neighbourhood, tile.getNeighbourTile(RelativePosition.BOTTOM_RIGHT));
    }

}
