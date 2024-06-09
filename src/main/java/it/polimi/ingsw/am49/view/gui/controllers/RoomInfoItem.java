package it.polimi.ingsw.am49.view.gui.controllers;

import javafx.scene.image.Image;

public class RoomInfoItem {
    private final String roomName;
    private final int playersInside;
    private final int maxCapacity;

    public RoomInfoItem(String roomName, int maxCapacity, int playersInside) {
        this.roomName = roomName;
        this.playersInside = playersInside;
        this.maxCapacity = maxCapacity;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getPlayersInside() {
        return playersInside;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }
}

