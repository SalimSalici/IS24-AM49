package it.polimi.ingsw.am49.view.gui.controllers;
import javafx.scene.image.Image;

/**
 * Represents the information of a player at the end of the game.
 * This includes the player's username, rank, points, completed objectives, and totem image.
 */
public class EndGameInfoItem {
    private final String username;
    private final int rank;
    private final int points;
    private final int completedOb;
    private final Image totemImage;

    /**
     * Constructs a new EndGameInfoItem with the specified details.
     *
     * @param username     the username of the player
     * @param rank         the rank of the player
     * @param points       the points scored by the player
     * @param completedOb  the number of completed objectives by the player
     * @param totemImage   the image of the player's totem
     */
    public EndGameInfoItem(String username, int rank, int points, int completedOb, Image totemImage) {
        this.username = username;
        this.points = points;
        this.rank = rank;
        this.completedOb = completedOb;
        this.totemImage = totemImage;
    }

    /**
     * @return the username of the player
     */
    public String getusername() {
        return username;
    }


    /**
     * @return the points scored by the player
     */
    public String getpoints() {
        return Integer.toString(points);
    }

    /**
     * @return the number of completed objectives
     */
    public String getCompletedOb() {
        return Integer.toString(completedOb);
    }

    /**
     * @return the image of the player's totem
     */
    public Image getTotemImage() {
        return totemImage;
    }

    /**
     * @return the rank of the player
     */
    public String getRank() {return Integer.toString(rank);}
}

