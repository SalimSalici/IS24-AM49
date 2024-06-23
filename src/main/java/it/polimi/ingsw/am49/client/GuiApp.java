// package it.polimi.ingsw.am49.client;
//
//import it.polimi.ingsw.am49.controller.gameupdates.ChatMSG;
//import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
//import it.polimi.ingsw.am49.controller.room.RoomInfo;
//import java.rmi.RemoteException;
//import it.polimi.ingsw.am49.view.tui.scenes.InvalidSceneException;
//import it.polimi.ingsw.am49.view.gui.GuiEntry;
//import it.polimi.ingsw.am49.view.gui.GuiManager;
//
///**
// * The GuiApp class represents the client application with a graphical user interface (GUI).
// * It handles room updates, game updates, and initializes the GUI.
// */
//public class GuiApp extends ClientApp {
//    public GuiApp() throws RemoteException {
//    }
//
////    private GuiManager manager;
////    private final String[] args;
////
////    /**
////     * Constructor for the GuiApp class.
////     *
////     * @param args the command line arguments
////     * @throws RemoteException if a remote communication error occurs
////     */
////    public GuiApp(String[] args) throws RemoteException {
////        this.args = args;
////    }
////
////    /**
////     * Initializes the GUI application.
////     * This method starts the JavaFX application and sets up the GUI manager.
////     */
////    @Override
////    public void initialize(boolean gui) {
////        // Start JavaFX
////        manager = new GuiManager(this);
////        GuiEntry.guiManagerInstance = manager;
////        GuiEntry.main(args);
////    }
////
////    /**
////     * Updates the room information in the GUI.
////     *
////     * @param roomInfo the current room information
////     * @param message the message to be displayed in the room
////     */
////    @Override
////    public void roomUpdate(RoomInfo roomInfo, String message) {
////        try {
////            this.manager.getCurrentController().roomUpdate(roomInfo, message);
////        } catch (InvalidSceneException e) {
////            System.out.println("Invalid Scene");
////            throw new RuntimeException(e);
////        }
////    }
////
////    /**
////     * Receives and processes a game update.
////     *
////     * @param gameUpdate the game update to be processed
////     */
////    @Override
////    public void receiveGameUpdate(GameUpdate gameUpdate) {
////        super.receiveGameUpdate(gameUpdate);
////        try {
////            this.manager.getCurrentController().gameUpdate(gameUpdate);
////        } catch (InvalidSceneException e) {
////            System.out.println("Game update received during wrong scene.");
////            throw new RuntimeException(e);
////        }
////    }
//}
