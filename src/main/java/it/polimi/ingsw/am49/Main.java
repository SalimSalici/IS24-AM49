package it.polimi.ingsw.am49;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualBoard;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualCard;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.view.tui.TuiBoardRenderer;
import it.polimi.ingsw.am49.view.tui.TuiPlayerRenderer;

import java.util.List;

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

        TuiBoardRenderer tuiBoardRenderer = new TuiBoardRenderer(vb);
        tuiBoardRenderer.drawNeighbourhood(25, 25);
        tuiBoardRenderer.printBoard();

        VirtualPlayer player = new VirtualPlayer("Salim", Color.RED);
        player.setHand(List.of(1, 34, 65));
        player.setPersonalObjectiveId(101);
        TuiPlayerRenderer tuiPlayerRenderer = new TuiPlayerRenderer(player, false, List.of(98, 97));
        tuiPlayerRenderer.print();
    }

}
