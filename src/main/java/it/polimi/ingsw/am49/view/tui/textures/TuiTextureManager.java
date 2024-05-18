package it.polimi.ingsw.am49.view.tui.textures;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualCard;

import java.io.*;

/**
 * This class handles loading textures from disk for the TUI version of the game
 */
public class TuiTextureManager {

    /**
     * Rapresents the texture of a card where every character has the correct color
     */
    private static ColoredChar[][] buffer;
    private static TuiTextureManager instance;

    public TuiTextureManager() {
        buffer = new ColoredChar[5][15];
    }

    public static TuiTextureManager getInstance() {
        if (TuiTextureManager.instance == null)
            TuiTextureManager.instance = new TuiTextureManager();
        return TuiTextureManager.instance;
    }

    public static ColoredChar[][] getTexture(VirtualCard card) {

        String fileName = "/it/polimi/ingsw/am49/textures/tui/" + card.id() + ".txt";

        //If the card is flipped reads the first line of the front of the card and throw that retreaves the correct
        //file name for the back using the BackTesture enum
        if (card.flipped()) {
            try (InputStream is = TuiTextureManager.class.getResourceAsStream(fileName);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line = reader.readLine();
                fileName = BackTexture.getFileName(line);
                if (fileName == null || fileName.isEmpty()) {
                    throw new FileNotFoundException("File not found");
                }
            } catch (IOException e) {
                System.out.println("Error in reading file " + e.getMessage());
            }
        }

        //reads the .txt texture file and creates the texture in the buffer
        try (InputStream is = TuiTextureManager.class.getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            String[] charBuffer = new String[5];
            String[] colorBuffer = new String[5];

            //if the card is not flipped the first line of the .txt file, that contains the info reguarding
            //the correct back texture is descarded.
            if (!card.flipped()) {
                line = reader.readLine();
            }
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

        } catch (IOException e) {
            System.out.println("Error in reading file " + e.getMessage());
        }
        return buffer;
    }
}
