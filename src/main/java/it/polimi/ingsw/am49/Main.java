package it.polimi.ingsw.am49;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualBoard;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualCard;
import it.polimi.ingsw.am49.view.tui.TuiBoard;

public class Main {

    public static void main(String[] args) {
        VirtualCard starterCard = new VirtualCard(86, false);
        VirtualBoard vb = new VirtualBoard();

        vb.placeCard(new VirtualCard(86, false), 25, 25);
        vb.placeCard(new VirtualCard(43, false), 25, 26);
        vb.placeCard(new VirtualCard(65, false), 25, 24);
        vb.placeCard(new VirtualCard(32, false), 26, 24);
        vb.placeCard(new VirtualCard(1, false), 26, 26);
        vb.placeCard(new VirtualCard(1, false), 25, 27);
        vb.placeCard(new VirtualCard(1, false), 26, 27);
        vb.placeCard(new VirtualCard(1, false), 27, 28);

        TuiBoard tuiBoard = new TuiBoard(vb);
        tuiBoard.drawNeighbourhood(4, 4);
        tuiBoard.printBoard();
    }

}
