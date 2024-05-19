package it.polimi.ingsw.am49.view.tui;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualBoard;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualTile;
import it.polimi.ingsw.am49.model.enumerations.RelativePosition;
import it.polimi.ingsw.am49.view.tui.textures.TuiTextureManager;

import java.util.ArrayList;
import java.util.List;

public class TuiBoard {

    private final VirtualBoard virtualBoard;
    private final TuiBoardRenderer boardRenderer;

    public TuiBoard(VirtualBoard virtualBoard) {
        this.virtualBoard = virtualBoard;
        this.boardRenderer = new TuiBoardRenderer(140, 30);
    }

    public void drawNeighbourhood(int row, int col) {
        this.boardRenderer.clearBoard();

        List<VirtualTile> neighbourhood = new ArrayList<>();
        VirtualTile centerTile = this.virtualBoard.getTile(row, col);
        if (centerTile == null) return;

        this.addToNeighbourhood(neighbourhood, centerTile);

        int centerDisplayRow = this.boardRenderer.getHeight() / 2;
        int centerDisplayCol = this.boardRenderer.getWidth() / 2;
        int expandedRow = centerTile.getExpandedRow();
        int expandedCol = centerTile.getExpandedCol();
        neighbourhood.stream().sorted().forEach((virtualTile) -> {
            int r = virtualTile.getRow();
            int c = virtualTile.getCol();
            int currExpandedRow = virtualTile.getExpandedRow();
            int currExpandedCol = virtualTile.getExpandedCol();
            int rowOffset = currExpandedRow - expandedRow;
            int colOffset = currExpandedCol - expandedCol;

            this.boardRenderer.draw(
                    TuiTextureManager.getTexture(this.virtualBoard.getTile(r, c).getCard()),
                    centerDisplayCol + colOffset * 12,
                    centerDisplayRow + rowOffset * 3
            );
        });
    }

    public void printBoard() {
        this.boardRenderer.printBoard();
    }

    private void addToNeighbourhood(List<VirtualTile> neighbourhood, VirtualTile tile) {
        if (tile == null || neighbourhood.contains(tile)) return;
        neighbourhood.add(tile);
        this.addToNeighbourhood(neighbourhood, tile.getNeighbourTile(RelativePosition.TOP_LEFT));
        this.addToNeighbourhood(neighbourhood, tile.getNeighbourTile(RelativePosition.TOP_RIGHT));
        this.addToNeighbourhood(neighbourhood, tile.getNeighbourTile(RelativePosition.BOTTOM_LEFT));
        this.addToNeighbourhood(neighbourhood, tile.getNeighbourTile(RelativePosition.BOTTOM_RIGHT));
    }

}
