package it.polimi.ingsw.am49.client;

import it.polimi.ingsw.am49.controller.RoomInfo;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Client extends Remote {
    public void playerJoinedYourRoom(RoomInfo room, String username) throws RemoteException;
    public void playerLeftYourRoom(RoomInfo room, String username) throws RemoteException;
    public void receiveGameUpdate(GameUpdate gameUpdate) throws RemoteException;
    public void playerDisconnected(String username) throws RemoteException;
    public void ping() throws RemoteException;
}
