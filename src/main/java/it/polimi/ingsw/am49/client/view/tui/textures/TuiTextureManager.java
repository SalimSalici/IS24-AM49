package it.polimi.ingsw.am49.client.view.tui.textures;

import it.polimi.ingsw.am49.common.util.Log;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles loading textures from disk for the TUI version of the game.
 */
public class TuiTextureManager {

    /**
     * A map to store textures with their corresponding IDs.
     */
    private final Map<Integer, TuiTexture> textures;

    /**
     * A map to store background textures.
     */
    private final Map<BackTexture, ColoredChar[][]> backTextures;

    /**
     * The singleton instance of the TuiTextureManager.
     */
    private static TuiTextureManager instance;

    /**
     * Private constructor to initialize the texture manager.
     * Loads all the textures from the disk and stores them in maps.
     */
    private TuiTextureManager() {
        this.backTextures = new HashMap<>();
        try {
            for (BackTexture bt : BackTexture.values()) {
                try (InputStream inputStream = TuiTextureManager.class.getResourceAsStream(bt.getValue())) {
                    if (inputStream == null)
                        throw new IOException("Couldn't load front back texture for " + bt.name() + " " + bt.getValue());
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                        this.backTextures.put(bt, this.readTexture(reader));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error. Could not load textures. " + e.getMessage());
            System.exit(1);
        }

        this.textures = new HashMap<>();
        try {
            for (int i = 1; i <= 102; i++)
                this.textures.put(i, this.loadTexture(i));
        } catch (IOException e) {
            System.out.println("Error. Could not load textures. " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Returns the singleton instance of the TuiTextureManager.
     *
     * @return the singleton instance
     */
    public static TuiTextureManager getInstance() {
        if (TuiTextureManager.instance == null)
            TuiTextureManager.instance = new TuiTextureManager();
        return TuiTextureManager.instance;
    }

    /**
     * Loads a texture with the specified ID from the disk.
     *
     * @param id the ID of the texture to load
     * @return the loaded TuiTexture
     * @throws IOException if an I/O error occurs while loading the texture
     */
    private TuiTexture loadTexture(int id) throws IOException {
        ColoredChar[][] front;
        ColoredChar[][] back;

        String fileName = "/it/polimi/ingsw/am49/textures/tui/" + id + ".txt";

        try (InputStream inputStream = TuiTextureManager.class.getResourceAsStream(fileName);) {
            if (inputStream == null)
                throw new IOException("Couldn't load front tui texture for card with id + " + id);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line = reader.readLine();
                front = this.readTexture(reader);
                back = this.backTextures.get(BackTexture.valueOf(line));
            }
        }

        return new TuiTexture(front, back);
    }

    /**
     * Reads a texture from a BufferedReader.
     *
     * @param reader the BufferedReader to read from
     * @return a 2D array of ColoredChar representing the texture
     * @throws IOException if an I/O error occurs while reading the texture
     */
    private ColoredChar[][] readTexture(BufferedReader reader) throws IOException {
        String line;
        String[] charBuffer = new String[5];
        String[] colorBuffer = new String[5];
        ColoredChar[][] buffer = new ColoredChar[5][15];

        for (int i = 0; i < 5; i++) {   //reads the characters that compose the texture
            line = reader.readLine();
            charBuffer[i] = line;
        }

        for (int i = 0; i < 5; i++) {   //reads the colors of each character of the texture
            line = reader.readLine();
            colorBuffer[i] = line;
        }

        for (int i = 0; i < charBuffer.length; i++) {   //assigns the correct color to every character
            for (int j = 0; j < charBuffer[i].length(); j++) {
                buffer[i][j] = new ColoredChar(charBuffer[i].charAt(j), AnsiColor.getColorForChar(colorBuffer[i].charAt(j)));
            }
        }

        return buffer;
    }

    /**
     * Retrieves the texture with the specified ID, optionally flipped.
     *
     * @param id the ID of the texture to retrieve
     * @param flipped whether to retrieve the flipped version of the texture
     * @return a 2D array of ColoredChar representing the texture
     */
    public ColoredChar[][] getTexture(int id, boolean flipped) {
        Log.getLogger().info("Texture requested. Id: " + id + " - flipped: " + flipped);
        TuiTexture texture = this.textures.get(id);
        if (texture == null) return null;
        if (flipped) return texture.getBackBuffer();
        return texture.getFrontBuffer();
    }

    /**
     * Retrieves the background texture for the specified BackTexture.
     *
     * @param backTexture the BackTexture to retrieve
     * @return a 2D array of ColoredChar representing the background texture
     */
    public ColoredChar[][] getBackTexture(BackTexture backTexture) {
        return this.backTextures.get(backTexture);
    }
}
