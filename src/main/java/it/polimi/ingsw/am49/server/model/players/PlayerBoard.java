package it.polimi.ingsw.am49.server.model.players;

import it.polimi.ingsw.am49.common.CommonConfig;
import it.polimi.ingsw.am49.server.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.server.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.common.enumerations.RelativePosition;
import it.polimi.ingsw.am49.common.enumerations.Symbol;
import it.polimi.ingsw.am49.common.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.common.util.Pair;

import java.io.Serializable;
import java.util.*;

/**
 * The personal board of the player, where he places the cards
 */
public class PlayerBoard implements Serializable {

    /**
     * The board of every player is saver in a matrix of boardtiles.
     * @see BoardTile
     */
    private final BoardTile[][] board;

    /**
     * Is used to save the order in witch the cards are placed on the board to facilitate handing of the cards.
     */
    private final List<BoardTile> placementOrder;

    /**
     * Saves the starter tile for each player.
     */
    private final BoardTile starterTile;

    /**
     * Is a Map&lt;{@link Symbol}, {@link Integer}&gt; used to keep track of the resources that are available on the board.
     */
    private final Map<Symbol, Integer> availableResources;

    /**
     * Constructor for the PlayerBoard class.
     * @param starterCard takes as parameter the starter card as it is the starting point to place other cards.
     */
    public PlayerBoard(StarterCard starterCard) {
        this.placementOrder = new ArrayList<>();
        this.board = new BoardTile[CommonConfig.boardMatrixHeight][CommonConfig.boardMatrixWidth];
        this.starterTile = new BoardTile(starterCard, CommonConfig.starterCardRow, CommonConfig.starterCardCol, this);
        this.board[CommonConfig.starterCardRow][CommonConfig.starterCardCol] = this.starterTile;
        this.placementOrder.add(this.starterTile);
        this.availableResources = new HashMap<>();
        for (Symbol s : Symbol.values()) {
            this.availableResources.put(s, 0);
        }
        this.updateAvailableResources();
    }

    /**
     * Updates the availableResources Map every time that a card is added.
     */
    public void updateAvailableResources() {
        for (Symbol s : Symbol.values()) {
            this.availableResources.put(s, 0);
        }

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

    /**
     * Getter for availableResources. At the moment is not used.
     * @return the map that tracks the available resources.
     */
    public Map<Symbol, Integer> getAvailableResources() {
        return new HashMap<>(availableResources);
    }

    /**
     * Getter for the placementOrder list.
     * @return the placementOrder.
     */
    public List<BoardTile> getPlacementOrder() {
        return Collections.unmodifiableList(placementOrder);
    }

    /**
     * Getter for the starter tile.
     * @return the starter tile.
     */
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
    public BoardTile placeTile(PlaceableCard card, int parentRow, int parentCol, RelativePosition relativePosition) throws InvalidActionException {
        Pair<Integer, Integer> coords = this.getCoords(relativePosition, parentRow, parentCol);
        if (!this.isPlaceableTile(coords.first, coords.second)) throw new InvalidActionException("You cannot place a card here.");

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
        if (tr != null && tr.getCard().getActiveBl().equals(Symbol.FORBIDDEN)) return false;
        if (tl != null && tl.getCard().getActiveBr().equals(Symbol.FORBIDDEN)) return false;
        if (br != null && br.getCard().getActiveTl().equals(Symbol.FORBIDDEN)) return false;
        if (bl != null && bl.getCard().getActiveTr().equals(Symbol.FORBIDDEN)) return false;

        return true;
    }

    /**
     * This method checks if the player has enough resources in his player board to pay the cost of the card.
     * For card with no cost the method returns true.
     * @param card is a {@link PlaceableCard} containing all the information regarding cards that can be played
     * @return true if the cost is met false otherwise
     */
    public boolean isCardCostMet(PlaceableCard card){
        Map<Symbol, Integer> cardCost = card.getPrice();

        for(Map.Entry<Symbol, Integer> entry : cardCost.entrySet()){
            Symbol symbol = entry.getKey();
            Integer valueCardCost = entry.getValue();

            if( valueCardCost == 0){
                continue;
            }

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

    /**
     * @return true if the board is deadlocked, meaning that no cards can be placed (any placement
     * would cover a forbidden corner), false otherwise
     */
    public boolean isDeadlocked() {
        for (BoardTile tile : this.getPlacementOrder()) {
            for (RelativePosition pos : RelativePosition.values()) {
                Pair<Integer, Integer> coords = this.getCoords(pos, tile.getRow(), tile.getCol());
                if (this.isPlaceableTile(coords.first, coords.second)) return false;
            }
        }
        return true;
    }

    /**
     * Calculates and returns the coordinates of a card based on a specified relative position to another card's coordinates.
     *
     * @param relativePosition the relative position to the current card
     * @param row the row index of the current card in the grid
     * @param col the column index of the current card in the grid
     * @return a Pair object containing the row and column indices of the card at the calculated relative position
     */
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
}
