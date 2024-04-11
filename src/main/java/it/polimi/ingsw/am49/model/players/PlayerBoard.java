package it.polimi.ingsw.am49.model.players;

import it.polimi.ingsw.am49.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.model.enumerations.RelativePosition;
import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.model.enumerations.Symbol;
import it.polimi.ingsw.am49.util.Pair;
import it.polimi.ingsw.am49.model.cards.placeables.GoldCard;
import java.util.*;

/**
 * The personal board of the player, where he places the cards
 */
public class PlayerBoard {

    private final BoardTile[][] board;
    private final List<BoardTile> placementOrder;
    private final BoardTile starterTile;
    private final Map<Symbol, Integer> availableResources;
    public PlayerBoard(StarterCard starterCard) {
        this.placementOrder = new ArrayList<>();
        this.board = new BoardTile[50][50];
        this.starterTile = new BoardTile(starterCard, 25, 25, this);
        this.board[25][25] = this.starterTile;
        this.placementOrder.add(this.starterTile);
        this.availableResources = new HashMap<>();
    }

    private void updateAvailableResources() {
        Map<Symbol, Integer> tileSymbols;
        for(BoardTile tile : this.getPlacementOrder()){
            tileSymbols = tile.getActiveSymbols();

            for(Map.Entry<Symbol, Integer> entry : tileSymbols.entrySet()){
                Symbol symbol = entry.getKey();
                Integer value = entry.getValue();

                availableResources.merge(symbol, value, Integer::sum);
            }
        }
    }

    public Map<Symbol, Integer> getAvailableResources() {
        return Collections.unmodifiableMap(availableResources);
    }

    public List<BoardTile> getPlacementOrder() {
        return Collections.unmodifiableList(placementOrder);
    }

    public BoardTile getStarterTile() {
        return starterTile;
    }

    /**
     * @return true if the tile in the given position hasn't been created
     */
    public boolean isTileFree(int row, int col) {
        return this.board[row][col] == null;
    }

    public BoardTile getTile(int row, int col) {
        return this.board[row][col];
    }

    /**
     * When a player places a card, a new tile is created in the corresponding position
     * @param parentRow,ParentCol are the coordinates of the card(tile) whose corner has been covered by the new card
     * @param relativePosition indicates which corner has been covered
     */
    public BoardTile placeTile(PlaceableCard card, int parentRow, int parentCol, RelativePosition relativePosition) throws Exception {
        Pair<Integer, Integer> coords = this.getCoords(relativePosition, parentRow, parentCol);
        if (!this.isPlaceableTile(coords.first, coords.second)) throw new Exception("Invalid tile");

        BoardTile newBoardTile = new BoardTile(card, coords.first, coords.second, this);
        this.board[coords.first][coords.second] = newBoardTile;

        BoardTile neighbour;
        if ((neighbour = newBoardTile.getNeighbourTile(RelativePosition.TOP_RIGHT)) != null)
            neighbour.coverBl();
        if ((neighbour = newBoardTile.getNeighbourTile(RelativePosition.TOP_LEFT)) != null)
            neighbour.coverBr();
        if ((neighbour = newBoardTile.getNeighbourTile(RelativePosition.BOTTOM_RIGHT)) != null)
            neighbour.coverTl();
        if ((neighbour = newBoardTile.getNeighbourTile(RelativePosition.BOTTOM_LEFT)) != null)
            neighbour.coverTr();

        this.placementOrder.add(newBoardTile);
        this.updateAvailableResources();
        return newBoardTile;
    }

    /**
     * @return true if one or more of the tiles around the given position have the corresponding corner free
     */
    public boolean isPlaceableTile(int row, int col) {
        if (this.board[row][col] != null) return false;

        Pair<Integer, Integer> trCoords = this.getCoords(RelativePosition.TOP_RIGHT, row, col);
        Pair<Integer, Integer> tlCoords = this.getCoords(RelativePosition.TOP_LEFT, row, col);
        Pair<Integer, Integer> brCoords = this.getCoords(RelativePosition.BOTTOM_RIGHT, row, col);
        Pair<Integer, Integer> blCoords = this.getCoords(RelativePosition.BOTTOM_LEFT, row, col);
        BoardTile tr = this.board[trCoords.first][trCoords.second];
        BoardTile tl = this.board[tlCoords.first][tlCoords.second];
        BoardTile br = this.board[brCoords.first][brCoords.second];
        BoardTile bl = this.board[blCoords.first][blCoords.second];

        if (tr == null && tl == null && br == null && bl == null) return false;
        if (tr != null && tr.getCard().getBl().equals(Symbol.FORBIDDEN)) return false;
        if (tl != null && tl.getCard().getBr().equals(Symbol.FORBIDDEN)) return false;
        if (br != null && br.getCard().getTl().equals(Symbol.FORBIDDEN)) return false;
        if (bl != null && bl.getCard().getTr().equals(Symbol.FORBIDDEN)) return false;

        return true;
    }

    public boolean isCardCostMet(GoldCard card){
        Map<Symbol, Integer> cardCost = card.getPriceAsSymbols();

        for(Map.Entry<Symbol, Integer> entry : cardCost.entrySet()){
            Symbol symbol = entry.getKey();
            Integer valueCardCost = entry.getValue();
            if(availableResources.containsKey(symbol)){
                Integer availableValue = availableResources.get(symbol);

                if(availableValue < valueCardCost){
                    return false;
                }
            }else {
                return false;
            }
        }
        return true;
    }

    public Pair<Integer, Integer> getCoords(RelativePosition relativePosition, int row, int col) {
        switch (relativePosition) {
            case TOP -> {
                return new Pair<>(row - 1, col);
            }
            case TOP_LEFT -> {
                if (col % 2 == 0)
                    return new Pair<>(row - 1, col - 1);
                return new Pair<>(row, col - 1);
            }
            case TOP_RIGHT -> {
                if (col % 2 == 0)
                    return new Pair<>(row - 1, col + 1);
                return new Pair<>(row, col + 1);
            }
            case BOTTOM -> {
                return new Pair<>(row + 1, col);
            }
            case BOTTOM_LEFT -> {
                if (col % 2 == 0)
                    return new Pair<>(row, col - 1);
                return new Pair<>(row + 1, col - 1);
            }
            case BOTTOM_RIGHT -> {
                if (col % 2 == 0)
                    return new Pair<>(row, col + 1);
                return new Pair<>(row + 1, col + 1);
            }
            case LEFT -> {
                return new Pair<>(row, col - 2);
            }
            case RIGHT -> {
                return new Pair<>(row, col + 2);
            }
        }
        return new Pair<>(row, col);
    }

    // TODO: this method was added mainly for debugging... could be removed in future
    public void printSimpleBoard() {
        for (int r = 0; r < this.board.length; r++) {
            for (int c = 0; c < this.board[r].length; c++) {
                char toPrint = this.board[r][c] == null ? '-' : 'O';
                System.out.print(toPrint);
            }
            System.out.print("\n");
        }
    }
}
