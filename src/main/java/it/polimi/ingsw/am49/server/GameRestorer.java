package it.polimi.ingsw.am49.server;

import it.polimi.ingsw.am49.server.model.Game;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class GameRestorer {
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

    public static Map<String, Game> loadAllGames() {
        HashMap<String, Game> games = new HashMap<>();

        if (!ServerConfig.persistence) return games;

        File dir = new File(ServerConfig.savedGamesPath);

        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("The specified path is not a directory or does not exist.");
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

    public static void deleteGame(String name) {
        if (!ServerConfig.persistence) return;
        try {
            Files.delete(Path.of(ServerConfig.savedGamesPath + name + ".cn"));
        } catch (IOException ignored) {}
    }
}
