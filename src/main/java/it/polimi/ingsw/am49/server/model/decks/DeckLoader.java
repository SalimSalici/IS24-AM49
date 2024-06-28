package it.polimi.ingsw.am49.server.model.decks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.am49.server.model.cards.adapters.GoldCardTypeAdapter;
import it.polimi.ingsw.am49.server.model.cards.adapters.ObjectiveCardTypeAdapter;
import it.polimi.ingsw.am49.server.model.cards.adapters.PlacementPointsStrategyTypeAdapter;
import it.polimi.ingsw.am49.server.model.cards.adapters.SymbolTypeAdapter;
import it.polimi.ingsw.am49.server.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.common.enumerations.Symbol;
import it.polimi.ingsw.am49.common.util.Log;
import it.polimi.ingsw.am49.server.model.cards.placeables.GoldCard;
import it.polimi.ingsw.am49.server.model.cards.placeables.PlacementPointsStrategy;
import it.polimi.ingsw.am49.server.model.cards.placeables.ResourceCard;
import it.polimi.ingsw.am49.server.model.cards.placeables.StarterCard;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * This class creates the immutable decks by reading the JSON files in the resources. The immutable decks are used as
 * a starting point to create different shuffled decks for every individual game.
 * The class is implemented with the Singleton design pattern to ensure that only one DeckLoader instance is created.
 * See the following path for resources: {@code src/main/resources/}.
 */
public class DeckLoader {

    /**
     * Singleton instance of DeckLoader.
     */
    private static DeckLoader instance;

    /**
     * Immutable deck of resource cards.
     * @see ImmutableDeck
     */
    private final ImmutableDeck<ResourceCard> resourceCardImmutableDeck;

    /**
     * Immutable deck of gold cards.
     * @see ImmutableDeck
     */
    private final ImmutableDeck<GoldCard> goldCardImmutableDeck;

    /**
     * Immutable deck of starter cards.
     * @see ImmutableDeck
     */
    private final ImmutableDeck<StarterCard> starterCardImmutableDeck;

    /**
     * Immutable deck of objective cards.
     * @see ImmutableDeck
     */
    private final ImmutableDeck<ObjectiveCard> objectiveCardImmutableDeck;

    /**
     * Gson object with custom type adapters for JSON serialization and deserialization.
     */
    private final Gson gson;

    /**
     * Constructs a new DeckLoader instance. It initializes the Gson object with custom type adapters
     * for the different card types to handle JSON serialization and deserialization.
     * The Gson object is set to pretty printing to enhance readability.
     */
    private DeckLoader() {
        Log.getLogger().info("Loading cards from jsons.");
        this.gson = new GsonBuilder()
                .registerTypeAdapter(PlacementPointsStrategy.class, new PlacementPointsStrategyTypeAdapter())
                .registerTypeAdapter(GoldCard.class, new GoldCardTypeAdapter())
                .registerTypeAdapter(Symbol.class, new SymbolTypeAdapter())
                .registerTypeAdapter(ObjectiveCard.class, new ObjectiveCardTypeAdapter())
                .serializeNulls()
                .setPrettyPrinting()
                .create();
        this.resourceCardImmutableDeck = new ImmutableDeck<>(this.loadResourcesFromJson());
        Log.getLogger().info("Resource cards loaded.");
        this.goldCardImmutableDeck = new ImmutableDeck<>(this.loadGoldsFromJson());
        Log.getLogger().info("Gold cards loaded.");
        this.starterCardImmutableDeck = new ImmutableDeck<>(this.loadStartersFromJson());
        Log.getLogger().info("Starter cards loaded.");
        this.objectiveCardImmutableDeck = new ImmutableDeck<>(this.loadObjectivesFromJson());
        Log.getLogger().info("Objective cards loaded.");
    }

    /**
     * Gets the singleton instance of DeckLoader.
     * @return the DeckLoader instance if present, creates and returns it otherwise.
     */
    public static DeckLoader getInstance() {
        if (DeckLoader.instance != null) return instance;
        DeckLoader.instance = new DeckLoader();
        return DeckLoader.instance;
    }

    /**
     * Gets the Gson object with custom type adapters for card objects.
     * @return the Gson object
     */
    public Gson getGson() {
        return this.gson;
    }

    /**
     * Makes a copy of each resource card and creates an array containing all the resource cards.
     * @return the resource card array
     */
    public GameDeck<ResourceCard> getNewResourceDeck() {
        return new GameDeck<>(this.resourceCardImmutableDeck.getCardsCopy().toArray(ResourceCard[]::new));
    }

    /**
     * Makes a copy of each gold card and creates an array containing all the gold cards.
     * @return the gold card array
     */
    public GameDeck<GoldCard> getNewGoldDeck() {
        return new GameDeck<>(this.goldCardImmutableDeck.getCardsCopy().toArray(GoldCard[]::new));
    }

    /**
     * Makes a copy of each starter card and creates an array containing all the starter cards.
     * @return the starter card array
     */
    public GameDeck<StarterCard> getNewStarterDeck() {
        return new GameDeck<>(this.starterCardImmutableDeck.getCardsCopy().toArray(StarterCard[]::new));
    }

    /**
     * Makes a copy of each objective card and creates an array containing all the objective cards.
     * @return the objective card array
     */
    public GameDeck<ObjectiveCard> getNewObjectiveDeck() {
        return new GameDeck<>(this.objectiveCardImmutableDeck.getCardsCopy().toArray(ObjectiveCard[]::new));
    }

    /**
     * Gets a new resource card by its ID.
     * @param id the ID of the requested card
     * @return a copy of the card with the specified ID, or null if it doesn't exist
     */
    public ResourceCard getNewResourceCardById(int id) {
        return this.resourceCardImmutableDeck.getCardCopyById(id);
    }

    /**
     * Gets a new gold card by its ID.
     * @param id the ID of the requested card
     * @return a copy of the card with the specified ID, or null if it doesn't exist
     */
    public GoldCard getNewGoldCardById(int id) {
        return this.goldCardImmutableDeck.getCardCopyById(id);
    }

    /**
     * Gets a new objective card by its ID.
     * @param id the ID of the requested card
     * @return a copy of the card with the specified ID, or null if it doesn't exist
     */
    public ObjectiveCard getNewObjectiveCardById(int id) {
        return this.objectiveCardImmutableDeck.getCardCopyById(id);
    }

    /**
     * Gets a new starter card by its ID.
     * @param id the ID of the requested card
     * @return a copy of the card with the specified ID, or null if it doesn't exist
     */
    public StarterCard getNewStarterCardById(int id) {
        return this.starterCardImmutableDeck.getCardCopyById(id);
    }

    /**
     * Finds the path to the JSON file containing the resource cards and reads it as a stream that is converted into a list of cards.
     * @return the list of resource cards
     * See the following path for the resourcesCards JSON file: {@code src/main/resources/resourceCards.json}.
     */
    private List<ResourceCard> loadResourcesFromJson() {

        String resourcePath = "/it/polimi/ingsw/am49/model/decks/resourceCards.json";

        try (InputStream is = DeckLoader.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                return Arrays.stream(this.gson.fromJson(reader, ResourceCard[].class)).toList();
            }
        } catch (IOException | IllegalArgumentException ex) {
            Log.getLogger().severe("loadResourceFromJson() exception: " + ex.getMessage());
            System.exit(1);
            return null;
        }
    }

    /**
     * Finds the path to the JSON file containing the gold cards and reads it as a stream that is converted into a list of cards.
     * @return the list of gold cards
     * See the following path for the goldCards JSON file: {@code src/main/resources/goldCards.json}.
     */
    private List<GoldCard> loadGoldsFromJson() {

        String resourcePath = "/it/polimi/ingsw/am49/model/decks/goldCards.json";

        try (InputStream is = DeckLoader.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                return Arrays.stream(this.gson.fromJson(reader, GoldCard[].class)).toList();
            }
        } catch (IOException | IllegalArgumentException ex) {
            Log.getLogger().severe("loadResourceFromJson() exception: " + ex.getMessage());
            System.exit(1);
            return null;
        }
    }

    /**
     * Finds the path to the JSON file containing the starter cards and reads it as a stream that is converted into a list of cards.
     * @return the list of starter cards
     * See the following path for the starterCards JSON file: {@code src/main/resources/starterCards.json}.
     */
    private List<StarterCard> loadStartersFromJson() {

        String resourcePath = "/it/polimi/ingsw/am49/model/decks/starterCards.json";

        try (InputStream is = DeckLoader.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                return Arrays.stream(this.gson.fromJson(reader, StarterCard[].class)).toList();
            }
        } catch (IOException | IllegalArgumentException ex) {
            Log.getLogger().severe("loadResourceFromJson() exception: " + ex.getMessage());
            System.exit(1);
            return null;
        }
    }

    /**
     * Finds the path to the JSON file containing the objective cards and reads it as a stream that is converted into a list of cards.
     * @return the list of objective cards
     * See the following path for the objectiveCards JSON file: {@code src/main/resources/objectiveCards.json}.
     */
    private List<ObjectiveCard> loadObjectivesFromJson() {

        String resourcePath = "/it/polimi/ingsw/am49/model/decks/objectiveCards.json";

        try (InputStream is = DeckLoader.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                return Arrays.stream(this.gson.fromJson(reader, ObjectiveCard[].class)).toList();
            }
        } catch (IOException | IllegalArgumentException ex) {
            Log.getLogger().severe("loadResourceFromJson() exception: " + ex.getMessage());
            System.exit(1);
            return null;
        }
    }
}