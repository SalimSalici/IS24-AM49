package it.polimi.ingsw.am49.client;

import it.polimi.ingsw.am49.chat.ChatMSG;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Client extends Remote {
    public void roomUpdate(RoomInfo roomInfo, String message) throws RemoteException;
    public void receiveGameUpdate(GameUpdate gameUpdate) throws RemoteException;
    public void playerDisconnected(String username) throws RemoteException;
    public void startHeartbeat() throws RemoteException;
    public void stopHeartbeat() throws RemoteException;
    public void receiveChatMessage(ChatMSG msg) throws RemoteException;
}
