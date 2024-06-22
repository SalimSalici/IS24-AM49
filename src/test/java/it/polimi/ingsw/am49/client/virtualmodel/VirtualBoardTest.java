package it.polimi.ingsw.am49.client.virtualmodel;

import static org.junit.jupiter.api.Assertions.*;




import it.polimi.ingsw.am49.model.enumerations.RelativePosition;
import it.polimi.ingsw.am49.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

    public class VirtualBoardTest {

        private VirtualBoard board;
        private VirtualCard card;

        @BeforeEach
        public void setUp() {
            board = new VirtualBoard();
            card = new VirtualCard(1, false);
        }

        @Test
        public void testPlaceCard() {
            VirtualTile tile = board.placeCard(card, 10, 10);
            assertEquals(tile, board.getTile(10, 10));
            assertEquals(tile.getCard(), card);
            assertEquals(10, tile.getRow());
            assertEquals(10, tile.getCol());
            assertEquals(1, tile.getzIndex());
        }

        @Test
        public void testGetTile() {
            board.placeCard(card, 10, 10);
            assertNotNull(board.getTile(10, 10));
            assertNull(board.getTile(0, 0));
        }

        @Test
        public void testGetStarterTile() {
            VirtualTile tile = board.placeCard(card, 10, 10);
            assertEquals(tile, board.getStarterTile());
        }

        @Test
        public void testGetCoords() {
            Pair<Integer, Integer> coords = VirtualBoard.getCoords(RelativePosition.TOP, 10, 10);
            assertEquals(new Pair<>(9, 10), coords);

            coords = VirtualBoard.getCoords(RelativePosition.BOTTOM, 10, 10);
            assertEquals(new Pair<>(11, 10), coords);

            coords = VirtualBoard.getCoords(RelativePosition.TOP_LEFT, 10, 10);
            assertEquals(new Pair<>(9, 9), coords);

            coords = VirtualBoard.getCoords(RelativePosition.TOP_RIGHT, 10, 10);
            assertEquals(new Pair<>(9, 11), coords);

            coords = VirtualBoard.getCoords(RelativePosition.BOTTOM_LEFT, 10, 10);
            assertEquals(new Pair<>(10, 9), coords);

            coords = VirtualBoard.getCoords(RelativePosition.BOTTOM_RIGHT, 10, 10);
            assertEquals(new Pair<>(10, 11), coords);

            coords = VirtualBoard.getCoords(RelativePosition.LEFT, 10, 10);
            assertEquals(new Pair<>(10, 8), coords);

            coords = VirtualBoard.getCoords(RelativePosition.RIGHT, 10, 10);
            assertEquals(new Pair<>(10, 12), coords);
        }

        @Test
        public void testGetOrderedTilesList() {
            VirtualTile tile1 = board.placeCard(card, 10, 10);
            VirtualTile tile2 = board.placeCard(card, 20, 20);
            VirtualTile tile3 = board.placeCard(card, 30, 30);

            List<VirtualTile> orderedTiles = board.getOrderedTilesList();

            assertEquals(3, orderedTiles.size());
            assertEquals(tile1, orderedTiles.get(0));
            assertEquals(tile2, orderedTiles.get(1));
            assertEquals(tile3, orderedTiles.get(2));
        }
    }


