package it.polimi.ingsw.am49.view.tui.renderers;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualBoard;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualCard;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualTile;
import it.polimi.ingsw.am49.model.enumerations.RelativePosition;
import it.polimi.ingsw.am49.view.tui.textures.TuiTextureManager;

import java.util.ArrayList;
import java.util.List;

public class TuiBoardRenderer {

    private final VirtualBoard virtualBoard;
    private final TuiCardRenderer renderer;

    public TuiBoardRenderer(VirtualBoard virtualBoard) {
        this.virtualBoard = virtualBoard;
        this.renderer = new TuiCardRenderer(140, 30);
    }

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
    }

    public void printBoard() {
        this.renderer.printWithBorder();
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
