package it.polimi.ingsw.am49.view.gui.controllers;

import javafx.scene.image.Image;

/**
 * Represents information about a game room, including the room name, the number of players inside, and the maximum capacity.
 */
public class RoomInfoItem {
    private final String roomName;
    private final int playersInside;
    private final int maxCapacity;

    /**
     * Constructs a new RoomInfoItem with the specified room name, maximum capacity, and number of players inside.
     *
     * @param roomName     the name of the room
     * @param maxCapacity  the maximum capacity of the room
     * @param playersInside the number of players currently inside the room
     */
    public RoomInfoItem(String roomName, int maxCapacity, int playersInside) {
        this.roomName = roomName;
        this.playersInside = playersInside;
        this.maxCapacity = maxCapacity;
    }

    /**
     * @return the name of the room
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * @return the number of players currently inside the room
     */
    public int getPlayersInside() {
        return playersInside;
    }

    /**
     * @return the maximum capacity of the room
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }
}

