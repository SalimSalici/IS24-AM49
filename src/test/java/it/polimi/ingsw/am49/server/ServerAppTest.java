package it.polimi.ingsw.am49.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import it.polimi.ingsw.am49.common.Client;
import it.polimi.ingsw.am49.common.exceptions.*;
import it.polimi.ingsw.am49.common.reconnectioninfo.RoomInfo;
import it.polimi.ingsw.am49.server.controller.room.Room;
import it.polimi.ingsw.am49.common.enumerations.Color;

import java.rmi.RemoteException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServerAppTest {

    private TestableServerApp serverApp;

    @Mock
    private Client mockClient1;
    @Mock
    private Client mockClient2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        serverApp = new TestableServerApp();
    }

    @Test
    void testFetchRooms() throws CreateRoomException, AlreadyInRoomException {
        serverApp.createRoom(mockClient1, "Room1", 2, "User1");
        serverApp.createRoom(mockClient2, "Room2", 3, "User2");

        List<RoomInfo> result = serverApp.fetchRooms(mockClient1);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(room -> room.roomName().equals("Room1")));
        assertTrue(result.stream().anyMatch(room -> room.roomName().equals("Room2")));
    }

    @Test
    void testCreateRoom_Success() throws AlreadyInRoomException, CreateRoomException {
        String roomName = "TestRoom";
        int numPlayers = 3;
        String creatorUsername = "TestUser";

        RoomInfo result = serverApp.createRoom(mockClient1, roomName, numPlayers, creatorUsername);

        assertNotNull(result);
        assertEquals(roomName, result.roomName());
        assertEquals(numPlayers, result.maxPlayers());
        assertEquals(1, result.playersToColors().size());
        assertEquals(1, serverApp.getRoomCount());
        assertEquals(1, serverApp.getClientToRoomCount());
    }

    @Test
    void testCreateRoom_AlreadyInRoom() throws AlreadyInRoomException, CreateRoomException {
        serverApp.createRoom(mockClient1, "ExistingRoom", 2, "User1");

        assertThrows(AlreadyInRoomException.class, () ->
                serverApp.createRoom(mockClient1, "NewRoom", 3, "User1")
        );
    }

    @Test
    void testJoinRoom_Success() throws AlreadyInRoomException, JoinRoomException, GameAlreadyStartedException, CreateRoomException {
        String roomName = "TestRoom";
        serverApp.createRoom(mockClient1, roomName, 2, "User1");

        RoomInfo result = serverApp.joinRoom(mockClient2, roomName, "User2");

        assertNotNull(result);
        assertEquals(roomName, result.roomName());
        assertEquals(2, result.playersToColors().size());
        assertTrue(result.playersToColors().containsKey("User1"));
        assertTrue(result.playersToColors().containsKey("User2"));
    }

    @Test
    void testReadyUp() throws RoomException, AlreadyInRoomException, CreateRoomException {
        serverApp.createRoom(mockClient1, "TestRoom", 2, "User1");

        RoomInfo result = serverApp.readyUp(mockClient1, Color.BLUE);

        assertNotNull(result);
        assertEquals(result.playersToColors().get("User1"), Color.BLUE);
    }

    @Test
    void testLeaveRoom() throws RemoteException, AlreadyInRoomException, CreateRoomException {
        serverApp.createRoom(mockClient1, "TestRoom", 2, "User1");

        boolean result = serverApp.leaveRoom(mockClient1);

        assertTrue(result);
        assertEquals(0, serverApp.getRoomCount());
        assertEquals(0, serverApp.getClientToRoomCount());
    }

    @Test
    void testDestroyRoom() throws AlreadyInRoomException, CreateRoomException {
        serverApp.createRoom(mockClient1, "TestRoom", 2, "User1");
        assertEquals(1, serverApp.getRoomCount());
        assertEquals(1, serverApp.getClientToRoomCount());

        Room room = serverApp.getRoomByName("TestRoom");

        serverApp.destroyRoom(room);

        assertEquals(0, serverApp.getRoomCount());
        assertEquals(0, serverApp.getClientToRoomCount());
    }

    private static class TestableServerApp extends ServerApp {
        int getRoomCount() {
            return rooms.size();
        }

        int getClientToRoomCount() {
            return clientsToRooms.size();
        }

        Room getRoomByName(String roomName) {
            return rooms.stream()
                    .filter(room -> room.getRoomName().equals(roomName))
                    .findFirst()
                    .orElse(null);
        }
    }
}