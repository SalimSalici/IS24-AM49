package it.polimi.ingsw.am49.client;

import it.polimi.ingsw.am49.client.connectors.ConnectorType;

public class ClientConfig {

    public static ConnectorType connectionType = null;
    public static String serverHost = null;
    public static Integer serverPort = null;
    private static boolean colors = true;

    /**
     * Disables the color display in the text-based user interface.
     */
    public static void disableColors() {
        colors = false;
    }

    public static boolean getColors() {
        return colors;
    }
}
