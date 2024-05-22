package it.polimi.ingsw.am49.view.tui.textures;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualCard;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles loading textures from disk for the TUI version of the game
 */
public class TuiTextureManager {

    private final Map<Integer, TuiTexture> textures;

    private static TuiTextureManager instance;

    private TuiTextureManager() {
        this.textures = new HashMap<>();
        try {
            for (int i = 1; i <= 102; i++)
                this.textures.put(i, this.loadTexture(i));
        } catch (IOException e) {
            // TODO: handle exception
            throw new RuntimeException(e);
        }
    }

    public static TuiTextureManager getInstance() {
        if (TuiTextureManager.instance == null)
            TuiTextureManager.instance = new TuiTextureManager();
        return TuiTextureManager.instance;
    }

    private TuiTexture loadTexture(int id) throws IOException {
        ColoredChar[][] front;
        ColoredChar[][] back;

        String fileName = "/it/polimi/ingsw/am49/textures/tui/" + id + ".txt";
        String backFileName;

        try (InputStream inputStream = TuiTextureManager.class.getResourceAsStream(fileName);) {
            if (inputStream == null)
                throw new IOException("Couldn't load front tui texture for card with id + " + id);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line = reader.readLine();
                backFileName = BackTexture.getFileName(line);
                front = this.readTexture(reader);
            }
        }

        if (backFileName == null)
            throw new IOException("Couldn't find back tui texture path for card with id + " + id);

        try (InputStream inputStream = TuiTextureManager.class.getResourceAsStream(backFileName);) {
            if (inputStream == null)
                throw new IOException("Couldn't load back tui texture for card with id + " + id);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                back = this.readTexture(reader);
            }
        }

        return new TuiTexture(front, back);
    }

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

    public ColoredChar[][] getTexture(int id, boolean flipped) {
        TuiTexture texture = this.textures.get(id);
        if (flipped) return texture.getBackBuffer();
        return texture.getFrontBuffer();
    }
}
