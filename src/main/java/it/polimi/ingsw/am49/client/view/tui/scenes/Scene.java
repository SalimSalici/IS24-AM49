package it.polimi.ingsw.am49.client.view.tui.scenes;

import it.polimi.ingsw.am49.client.ClientConfig;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.common.enumerations.Color;
import it.polimi.ingsw.am49.client.view.tui.SceneManager;
import it.polimi.ingsw.am49.client.view.tui.textures.AnsiColor;

/**
 * Abstract class representing a scene in the TUI.
 */
public abstract class Scene {

    protected final SceneManager sceneManager;
    protected String errorMessage = "";
    protected String infoMessage = "";

    /**
     * Constructor for Scene.
     * @param sceneManager The scene manager that manages scene transitions.
     */
    public Scene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    /**
     * Abstract method to print the view of the scene.
     */
    public abstract void printView();

    /**
     * Abstract method to handle user input within the scene.
     * @param input The user input as a String.
     */
    public abstract void handleInput(String input);

    /**
     * Method called when the scene gains focus.
     */
    public void focus() {}

    /**
     * Method called when the scene loses focus.
     */
    public void unfocus() {}

    /**
     * Clears the screen by printing new lines or escape characters based on configuration.
     */
    protected void clearScreen() {
        System.out.println("\n" + "-".repeat(150));
        if (ClientConfig.getColors())
            System.out.println("\033[H\033[2J");
        else System.out.print("\n".repeat(60));
    }

    /**
     * Prints a big ascii banner with the name of the game.
     */
    public void printBigHeader() {
        System.out.println("                              ...                         ..                                                           \n" +
                "                           xH88\"`~ .x8X                 dF                                                             \n" +
                "                         :8888   .f\"8888Hf        u.   '88bu.                    uL   ..                               \n" +
                "                        :8888>  X8L  ^\"\"`   ...ue888b  '*88888bu        .u     .@88b  @88R                             \n" +
                "                        X8888  X888h        888R Y888r   ^\"*8888N    ud8888.  '\"Y888k/\"*P                              \n" +
                "                        88888  !88888.      888R I888>  beWE \"888L :888'8888.    Y888L                                 \n" +
                "                        88888   %88888      888R I888>  888E  888E d888 '88%\"     8888                                 \n" +
                "                        88888 '> `8888>     888R I888>  888E  888E 8888.+\"        `888N                                \n" +
                "                        `8888L %  ?888   ! u8888cJ888   888E  888F 8888L       .u./\"888&                               \n" +
                "                         `8888  `-*\"\"   /   \"*888*P\"   .888N..888  '8888c. .+ d888\" Y888*\"                             \n" +
                "                           \"888.      :\"      'Y\"       `\"888*\"\"    \"88888%   ` \"Y   Y\"                                \n" +
                "                             `\"\"***~\"`                     \"\"         \"YP'                                             \n" +
                "                                                                                                                       \n" +
                "                                                                                                                       \n" +
                "                                                                                                                       \n" +
                "     ...     ...                      s                                                  ..    .           ...         \n" +
                "  .=*8888n..\"%888:                   :8                                            x .d88\"    @88>     .x888888hx    : \n" +
                " X    ?8888f '8888                  .88       x.    .        .u    .                5888R     %8P     d88888888888hxx  \n" +
                " 88x. '8888X  8888>        u       :888ooo  .@88k  z88u    .d88B :@8c        u      '888R      .     8\" ... `\"*8888%`  \n" +
                "'8888k 8888X  '\"*8h.    us888u.  -*8888888 ~\"8888 ^8888   =\"8888f8888r    us888u.    888R    .@88u  !  \"   ` .xnxx.    \n" +
                " \"8888 X888X .xH8    .@88 \"8888\"   8888      8888  888R     4888>'88\"  .@88 \"8888\"   888R   ''888E` X X   .H8888888%:  \n" +
                "   `8\" X888!:888X    9888  9888    8888      8888  888R     4888> '    9888  9888    888R     888E  X 'hn8888888*\"   > \n" +
                "  =~`  X888 X888X    9888  9888    8888      8888  888R     4888>      9888  9888    888R     888E  X: `*88888%`     ! \n" +
                "   :h. X8*` !888X    9888  9888   .8888Lu=   8888 ,888B .  .d888L .+   9888  9888    888R     888E  '8h.. ``     ..x8> \n" +
                "  X888xX\"   '8888..: 9888  9888   ^%888*    \"8888Y 8888\"   ^\"8888*\"    9888  9888   .888B .   888&   `88888888888888f  \n" +
                ":~`888f     '*888*\"  \"888*\"\"888\"    'Y\"      `Y\"   'YP        \"Y\"      \"888*\"\"888\"  ^*888%    R888\"   '%8888888888*\"   \n" +
                "    \"\"        `\"`     ^Y\"   ^Y'                                         ^Y\"   ^Y'     \"%       \"\"        ^\"****\"\"`     \n" +
                "\n");
        System.out.println(" ".repeat(23) + "by Salim Salici, Niccolò Benetti, Lorenzo Trombini, Matteo Scarlino");
    }

    /**
     * Refreshes the view if the current scene is focused.
     */
    protected void refreshView() {
        synchronized (this.sceneManager) {
            if (this.sceneManager.isFocused(this))
                this.printView();
        }
    }

    /**
     * Displays an informational message and refreshes the view.
     * @param message The message to display.
     */
    protected void showInfoMessage(String message) {
        this.infoMessage = message;
        this.refreshView();
    }

    /**
     * Displays a help message with an example and refreshes the view.
     * @param message The help message.
     * @param example An example to illustrate the message.
     */
    protected void showHelpMessage(String message, String example) {
        this.showInfoMessage(message + "\nExample: " + example);
    }

    /**
     * Displays an error message and refreshes the view.
     * @param message The error message.
     */
    public void showError(String message) {
        this.errorMessage = message;
        this.refreshView();
    }

    /**
     * Prints either an informational or error message based on the current state.
     */
    protected void printInfoOrError() {
        if (this.errorMessage.isEmpty())
            System.out.println(this.infoMessage);
        else if (ClientConfig.getColors())
            System.out.println(AnsiColor.ANSI_RED + errorMessage + AnsiColor.ANSI_RESET);
        else
            System.out.println(errorMessage);
        this.infoMessage = "";
        this.errorMessage = "";
    }

    /**
     * Returns the username colored based on the player's color setting.
     * @param player The virtual player whose username is to be colored.
     * @return The colored username or null if the player is null.
     */
    protected String getColoredUsername(VirtualPlayer player) {
        if (player == null) return null;
        String offline = player.getPlaying() ? "" : " (offline)";
        return this.getColoredUsername(player.getUsername(), player.getColor()) + offline;
    }

    /**
     * Returns the username colored based on the specified color.
     * @param username The username to color.
     * @param color The color to apply.
     * @return The colored username or the username with the color name appended if colors are disabled.
     */
    protected String getColoredUsername(String username, Color color) {
        if (color == null) return username;
        if (ClientConfig.getColors())
            return AnsiColor.fromColor(color) + username + AnsiColor.ANSI_RESET;
        else
            return username + "[" + color.name() + "]";
    }
}