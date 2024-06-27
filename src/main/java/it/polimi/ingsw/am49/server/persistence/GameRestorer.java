package it.polimi.ingsw.am49.server.persistence;

import it.polimi.ingsw.am49.server.ServerConfig;
import it.polimi.ingsw.am49.server.model.Game;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the persistence of game states, providing methods to save, load, and delete game instances.
 */
public class GameRestorer {
    /**
     * Saves the given game state to a file with the specified name.
     *
     * @param name the name of the game to save
     * @param game the game instance to save
     */
    public static void saveGame(String name, Game game) {
        if (!ServerConfig.persistence) return;

        String filename = ServerConfig.savedGamesPath + name + ".cn";

        File directory = new File(ServerConfig.savedGamesPath);
        if (!directory.exists())
            if (!directory.mkdirs())
                return;

        try (FileOutputStream fileOut = new FileOutputStream(filename); ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(game);
            System.out.println("Game saved in file: " + filename);
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }

    /**
     * Loads all saved game states from the persistence directory.
     *
     * @return a map containing the names and corresponding game instances of all saved games
     */
    public static Map<String, Game> loadAllGames() {
        HashMap<String, Game> games = new HashMap<>();

        if (!ServerConfig.persistence) return games;

        File dir = new File(ServerConfig.savedGamesPath);

        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                System.err.println("Failed to create the directory: " + ServerConfig.savedGamesPath);
                return games;
            }
            System.out.println("Created directory: " + ServerConfig.savedGamesPath);
            return games;
        } else if (!dir.isDirectory()) {
            System.err.println("The specified path is not a directory: " + ServerConfig.savedGamesPath);
            return games;
        }

        File[] files = dir.listFiles((dir1, name) -> name.toLowerCase().endsWith(".cn"));

        if (files == null || files.length == 0) {
            System.out.println("No saved games to load.");
            return games;
        }

        for (File file : files) {
            String fileName = file.getName();
            String roomName = fileName.substring(0, fileName.length() - 3);
            Game game = GameRestorer.loadGame(roomName);
            if (game == null) continue;
            games.put(roomName, game);
        }

        return games;
    }

    /**
     * Loads the game state with the specified name from a file.
     *
     * @param name the name of the game to load
     * @return the loaded game instance, or null if loading fails
     */
    public static Game loadGame(String name) {
        String filename = ServerConfig.savedGamesPath + name + ".cn";

        try (FileInputStream fileIn = new FileInputStream(filename); ObjectInputStream in = new ObjectInputStream(fileIn)) {
            Game game = (Game) in.readObject();
            System.out.println("Game loaded from file: " + filename);
            return game;
        } catch (IOException e) {
            System.out.println("Error reading game file: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Game class not found: " + e.getMessage());
        }
        return null;
    }

    /**
     * Deletes the saved game state file with the specified name.
     *
     * @param name the name of the game to delete
     */
    public static void deleteGame(String name) {
        if (!ServerConfig.persistence) return;
        try {
            Files.delete(Path.of(ServerConfig.savedGamesPath + name + ".cn"));
        } catch (IOException ignored) {}
    }
}
