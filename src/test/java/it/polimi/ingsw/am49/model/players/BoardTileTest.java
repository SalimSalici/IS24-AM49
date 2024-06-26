package it.polimi.ingsw.am49.model.players;

import it.polimi.ingsw.am49.common.enumerations.RelativePosition;
import it.polimi.ingsw.am49.common.enumerations.Symbol;
import it.polimi.ingsw.am49.common.util.Pair;
import it.polimi.ingsw.am49.server.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.server.model.players.BoardTile;
import it.polimi.ingsw.am49.server.model.players.PlayerBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BoardTileTest {

    @Mock
    private PlaceableCard card;
    @Mock
    private PlayerBoard board;

    private BoardTile boardTile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(card.getActiveTl()).thenReturn(Symbol.INKWELL);
        when(card.getActiveTr()).thenReturn(Symbol.FORBIDDEN);
        when(card.getActiveBl()).thenReturn(Symbol.EMPTY);
        when(card.getActiveBr()).thenReturn(Symbol.BUGS);
        when(card.getActiveCenterResources()).thenReturn(new LinkedList<>());

        boardTile = new BoardTile(card, 5, 5, board);
    }

    @Test
    void testGetCard() {
        assertEquals(card, boardTile.getCard());
    }

    @Test
    void testGetRow() {
        assertEquals(5, boardTile.getRow());
    }

    @Test
    void testGetColumn() {
        assertEquals(5, boardTile.getCol());
    }

    @Test
    void testGetCoords() {
        when(board.getCoords(RelativePosition.TOP, 5, 5)).thenReturn(new Pair<>(4, 5));
        when(board.getCoords(RelativePosition.TOP_LEFT, 5, 5)).thenReturn(new Pair<>(4, 4));
        when(board.getCoords(RelativePosition.TOP_RIGHT, 5, 5)).thenReturn(new Pair<>(4, 6));
        when(board.getCoords(RelativePosition.BOTTOM, 5, 5)).thenReturn(new Pair<>(6, 5));
        when(board.getCoords(RelativePosition.BOTTOM_RIGHT, 5, 5)).thenReturn(new Pair<>(6, 6));
        when(board.getCoords(RelativePosition.BOTTOM_LEFT, 5, 5)).thenReturn(new Pair<>(6, 4));
        when(board.getCoords(RelativePosition.LEFT, 5, 5)).thenReturn(new Pair<>(5, 4));
        when(board.getCoords(RelativePosition.RIGHT, 5, 5)).thenReturn(new Pair<>(5, 6));

        when(board.getCoords(RelativePosition.TOP_LEFT, 4, 4)).thenReturn(new Pair<>(3, 3));
        when(board.getCoords(RelativePosition.TOP_RIGHT, 4, 4)).thenReturn(new Pair<>(3, 5));
        when(board.getCoords(RelativePosition.BOTTOM_LEFT, 4, 4)).thenReturn(new Pair<>(5, 3));
        when(board.getCoords(RelativePosition.BOTTOM_RIGHT, 4, 4)).thenReturn(new Pair<>(5, 5));

        // Assertions for all when calls
        boardTile = new BoardTile(card, 5, 5, board);

        assertEquals(new Pair<>(4, 5), boardTile.getCoords(RelativePosition.TOP));
        assertEquals(new Pair<>(4, 4), boardTile.getCoords(RelativePosition.TOP_LEFT));
        assertEquals(new Pair<>(4, 6), boardTile.getCoords(RelativePosition.TOP_RIGHT));
        assertEquals(new Pair<>(6, 5), boardTile.getCoords(RelativePosition.BOTTOM));
        assertEquals(new Pair<>(6, 6), boardTile.getCoords(RelativePosition.BOTTOM_RIGHT));
        assertEquals(new Pair<>(6, 4), boardTile.getCoords(RelativePosition.BOTTOM_LEFT));
        assertEquals(new Pair<>(5, 4), boardTile.getCoords(RelativePosition.LEFT));
        assertEquals(new Pair<>(5, 6), boardTile.getCoords(RelativePosition.RIGHT));

        boardTile = new BoardTile(card, 4, 4, board);

        assertEquals(new Pair<>(3, 3), boardTile.getCoords(RelativePosition.TOP_LEFT));
        assertEquals(new Pair<>(3, 5), boardTile.getCoords(RelativePosition.TOP_RIGHT));
        assertEquals(new Pair<>(5, 3), boardTile.getCoords(RelativePosition.BOTTOM_LEFT));
        assertEquals(new Pair<>(5, 5), boardTile.getCoords(RelativePosition.BOTTOM_RIGHT));
    }

    @Test
    void testGetNeighbourTile() {
        BoardTile neighbourTileTop = mock(BoardTile.class);
        BoardTile neighbourTileTopLeft = mock(BoardTile.class);
        BoardTile neighbourTileTopRight = mock(BoardTile.class);
        BoardTile neighbourTileBottom = mock(BoardTile.class);
        BoardTile neighbourTileBottomLeft = mock(BoardTile.class);
        BoardTile neighbourTileBottomRight = mock(BoardTile.class);
        BoardTile neighbourTileLeft = mock(BoardTile.class);
        BoardTile neighbourTileRight = mock(BoardTile.class);

        when(board.getCoords(RelativePosition.TOP, 5, 5)).thenReturn(new Pair<>(4, 5));
        when(board.getTile(4, 5)).thenReturn(neighbourTileTop);

        when(board.getCoords(RelativePosition.TOP_LEFT, 5, 5)).thenReturn(new Pair<>(4, 4));
        when(board.getTile(4, 4)).thenReturn(neighbourTileTopLeft);

        when(board.getCoords(RelativePosition.TOP_RIGHT, 5, 5)).thenReturn(new Pair<>(4, 6));
        when(board.getTile(4, 6)).thenReturn(neighbourTileTopRight);

        when(board.getCoords(RelativePosition.BOTTOM, 5, 5)).thenReturn(new Pair<>(6, 5));
        when(board.getTile(6, 5)).thenReturn(neighbourTileBottom);

        when(board.getCoords(RelativePosition.BOTTOM_LEFT, 5, 5)).thenReturn(new Pair<>(6, 4));
        when(board.getTile(6, 4)).thenReturn(neighbourTileBottomLeft);

        when(board.getCoords(RelativePosition.BOTTOM_RIGHT, 5, 5)).thenReturn(new Pair<>(6, 6));
        when(board.getTile(6, 6)).thenReturn(neighbourTileBottomRight);

        when(board.getCoords(RelativePosition.LEFT, 5, 5)).thenReturn(new Pair<>(5, 4));
        when(board.getTile(5, 4)).thenReturn(neighbourTileLeft);

        when(board.getCoords(RelativePosition.RIGHT, 5, 5)).thenReturn(new Pair<>(5, 6));
        when(board.getTile(5, 6)).thenReturn(neighbourTileRight);

        assertEquals(neighbourTileTop, boardTile.getNeighbourTile(RelativePosition.TOP));
        assertEquals(neighbourTileTopLeft, boardTile.getNeighbourTile(RelativePosition.TOP_LEFT));
        assertEquals(neighbourTileTopRight, boardTile.getNeighbourTile(RelativePosition.TOP_RIGHT));
        assertEquals(neighbourTileBottom, boardTile.getNeighbourTile(RelativePosition.BOTTOM));
        assertEquals(neighbourTileBottomLeft, boardTile.getNeighbourTile(RelativePosition.BOTTOM_LEFT));
        assertEquals(neighbourTileBottomRight, boardTile.getNeighbourTile(RelativePosition.BOTTOM_RIGHT));
        assertEquals(neighbourTileLeft, boardTile.getNeighbourTile(RelativePosition.LEFT));
        assertEquals(neighbourTileRight, boardTile.getNeighbourTile(RelativePosition.RIGHT));
    }

    @Test
    void testCoverTr() {

        boardTile.coverTr();

        assertEquals(new HashMap<Symbol, Integer>() {{
            put(Symbol.INKWELL, 1);
            put(Symbol.FORBIDDEN, 0);
            put(Symbol.EMPTY, 1);
            put(Symbol.BUGS, 1);
            put(Symbol.WOLVES, 0);
            put(Symbol.LEAVES, 0);
            put(Symbol.MANUSCRIPT, 0);
            put(Symbol.MUSHROOMS, 0);
            put(Symbol.QUILL, 0);
        }}, boardTile.getActiveSymbols());
    }

    @Test
    void testCoverTl() {
        boardTile.coverTl();
        assertEquals(new HashMap<Symbol, Integer>() {{
            put(Symbol.INKWELL, 0);
            put(Symbol.FORBIDDEN, 1);
            put(Symbol.EMPTY, 1);
            put(Symbol.BUGS, 1);
            put(Symbol.WOLVES, 0);
            put(Symbol.LEAVES, 0);
            put(Symbol.MANUSCRIPT, 0);
            put(Symbol.MUSHROOMS, 0);
            put(Symbol.QUILL, 0);
        }}, boardTile.getActiveSymbols());
    }

    @Test
    void testCoverBr() {
        boardTile.coverBr();
        assertEquals(new HashMap<Symbol, Integer>() {{
            put(Symbol.INKWELL, 1);
            put(Symbol.FORBIDDEN, 1);
            put(Symbol.EMPTY, 1);
            put(Symbol.BUGS, 0);
            put(Symbol.WOLVES, 0);
            put(Symbol.LEAVES, 0);
            put(Symbol.MANUSCRIPT, 0);
            put(Symbol.MUSHROOMS, 0);
            put(Symbol.QUILL, 0);
        }}, boardTile.getActiveSymbols());
    }

    @Test
    void testCoverBl() {
        boardTile.coverBl();
        assertEquals(new HashMap<Symbol, Integer>() {{
            put(Symbol.INKWELL, 1);
            put(Symbol.FORBIDDEN, 1);
            put(Symbol.EMPTY, 0);
            put(Symbol.BUGS, 1);
            put(Symbol.WOLVES, 0);
            put(Symbol.LEAVES, 0);
            put(Symbol.MANUSCRIPT, 0);
            put(Symbol.MUSHROOMS, 0);
            put(Symbol.QUILL, 0);
        }}, boardTile.getActiveSymbols());
    }
}
