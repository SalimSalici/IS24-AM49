package it.polimi.ingsw.am49.server;

public abstract class ServerConfig {

    /**
     * Whether to activate the "Persistence" additional functionality or not.
     */
    public static boolean persistence = false;

    /**
     * The location of where to save and reload games following a server crash.
     */
    public static final String savedGamesPath = "./saved_games/";

    /**
     * How long a room being restored should wait for users to join back before deleting itself, in seconds.
     */
    public static final int restoringRoomTimeout = 60;

    /**
     * How long can a user's last ping be before disconnecting them, in seconds.
     */
    public static final int clientHeartbeatTimeout = 5;
}
