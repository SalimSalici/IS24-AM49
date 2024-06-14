package it.polimi.ingsw.am49.view.gui.controllers;
import javafx.scene.image.Image;

public class EndGameInfoItem {
    private final String username;
    private final int rank;
    private final int points;
    private final int completedOb;
    private final Image totemImage;

    public EndGameInfoItem(String username, int rank, int points, int completedOb, Image totemImage) {
        this.username = username;
        this.points = points;
        this.rank = rank;
        this.completedOb = completedOb;
        this.totemImage = totemImage;
    }

    public String getusername() {
        return username;
    }
    public String getpoints() {
        return Integer.toString(points);
    }
    public String getCompletedOb() {
        return Integer.toString(completedOb);
    }
    public Image getTotemImage() {
        return totemImage;
    }
    public String getRank() {return Integer.toString(rank);}
}

