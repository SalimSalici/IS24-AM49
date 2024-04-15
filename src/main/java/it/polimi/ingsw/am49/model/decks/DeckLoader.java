package it.polimi.ingsw.am49.model.decks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.am49.model.cards.adapters.GoldCardTypeAdapter;
import it.polimi.ingsw.am49.model.cards.adapters.ObjectiveCardTypeAdapter;
import it.polimi.ingsw.am49.model.cards.adapters.PlacementPointsStrategyTypeAdapter;
import it.polimi.ingsw.am49.model.cards.adapters.SymbolTypeAdapter;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.cards.placeables.*;
import it.polimi.ingsw.am49.model.enumerations.Symbol;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * This class creates the immutable decks by reading the JSON files in the resouces. The immutable decks are used as
 * a starting point to create different shuffled decks for every individual game.
 * The class is implemented with the Singleton design pattern to ensure that only one DeckLoder istance in created.
 * See the following path for resources: {@code src/main/resources/}.
 */
public class DeckLoader {

    private static DeckLoader instance;
    /**
     * There is an immutable deck for each deck in the game.
     * @see it.polimi.ingsw.am49.model.decks.ImmutableDeck
     */
    private final ImmutableDeck<ResourceCard> resourceCardImmutableDeck;
    private final ImmutableDeck<GoldCard> goldCardImmutableDeck;
    private final ImmutableDeck<StarterCard> starterCardImmutableDeck;
    private final ImmutableDeck<ObjectiveCard> objectiveCardImmutableDeck;

    private final Gson gson;

    /**
     * Constructs a new DeckLoader instance. It initializes the Gson object with custom type adapters
     * for the different card types to handle JSON serialization and deserialization.
     * The Gson object is set to Pretty printing to enhance redability.
     */
    private DeckLoader() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(PlacementPointsStrategy.class, new PlacementPointsStrategyTypeAdapter())
                .registerTypeAdapter(GoldCard.class, new GoldCardTypeAdapter())
                .registerTypeAdapter(Symbol.class, new SymbolTypeAdapter())
                .registerTypeAdapter(ObjectiveCard.class, new ObjectiveCardTypeAdapter())
                .serializeNulls()
                .setPrettyPrinting()
                .create();
        this.resourceCardImmutableDeck = new ImmutableDeck<>(this.loadResourcesFromJson());
        this.goldCardImmutableDeck = new ImmutableDeck<>(this.loadGoldsFromJson());
        this.starterCardImmutableDeck = new ImmutableDeck<>(this.loadStartersFromJson());
        this.objectiveCardImmutableDeck = new ImmutableDeck<>(this.loadObjectivesFromJson());
    }

    /**
     * @return the DeckLoader istance if present, creats and returns it otherwise.
     */
    public static DeckLoader getInstance() {
        if (DeckLoader.instance != null) return instance;
        DeckLoader.instance = new DeckLoader();
        return DeckLoader.instance;
    }

    /**
     * @return the Gson object with custom type adapters for card objects.
     */
    public Gson getGson() {
        return this.gson;
    }

    /**
     * Makes a copy of each resource card and creates an array containing all the resource cards.
     * @return the resource card array.
     */
    public GameDeck<ResourceCard> getNewResourceDeck() {
        return new GameDeck<>(this.resourceCardImmutableDeck.getCardsCopy().toArray(ResourceCard[]::new));
    }

    /**
     * Makes a copy of each gold card and creates an array containing all the gold cards.
     * @return the gold card array.
     */
    public GameDeck<GoldCard> getNewGoldDeck() {
        return new GameDeck<>(this.goldCardImmutableDeck.getCardsCopy().toArray(GoldCard[]::new));
    }

    /**
     * Makes a copy of each starter card and creates an array containing all the starter cards.
     * @return the starter card array.
     */
    public GameDeck<StarterCard> getNewStarterDeck() {
        return new GameDeck<>(this.starterCardImmutableDeck.getCardsCopy().toArray(StarterCard[]::new));
    }

    /**
     * Makes a copy of each objective card and creates an array containing all the objective cards.
     * @return the objective card array.
     */
    public GameDeck<ObjectiveCard> getNewObjectiveDeck() {
        return new GameDeck<>(this.objectiveCardImmutableDeck.getCardsCopy().toArray(ObjectiveCard[]::new));
    }

    /**
     * @param id the id of the requested card
     * @return a copy of the card with the specified id, or null if it doesn't exist
     */
    public ResourceCard getNewResourceCardById(int id) {
        return this.resourceCardImmutableDeck.getCardCopyById(id);
    }

    /**
     * @param id the id of the requested card
     * @return a copy of the card with the specified id, or null if it doesn't exist
     */
    public GoldCard getNewGoldCardById(int id) {
        return this.goldCardImmutableDeck.getCardCopyById(id);
    }

    /**
     * @param id the id of the requested card
     * @return a copy of the card with the specified id, or null if it doesn't exist
     */
    public ObjectiveCard getNewObjectiveCardById(int id) {
        return this.objectiveCardImmutableDeck.getCardCopyById(id);
    }

    /**
     * @param id the id of the requested card
     * @return a copy of the card with the specified id, or null if it doesn't exist
     */
    public StarterCard getNewStarterCardById(int id) {
        return this.starterCardImmutableDeck.getCardCopyById(id);
    }

    /**
     * Finds the path to the JSON file containing the cards and reads it as a stream that is converted into a list of cards.
     * @return the list of cards.
     * See the following path for the resourcesCards JSON file: {@code src/main/resources/resourceCards.jason}.
     */
    private List<ResourceCard> loadResourcesFromJson() {
        String filePath = Objects.requireNonNull(DeckLoader.class.getResource("resourceCards.json")).getPath();


        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return Arrays.stream(this.gson.fromJson(reader, ResourceCard[].class)).toList();
        } catch (IOException ex) {
            //noinspection CallToPrintStackTrace
            ex.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    /**
     * Finds the path to the JSON file containing the cards and reads it as a stream that is converted into a list of cards.
     * @return the list of cards.
     * See the following path for the goldCards JSON file: {@code src/main/resources/goldCards.jason}.
     */
    private List<GoldCard> loadGoldsFromJson() {
        String filePath = Objects.requireNonNull(DeckLoader.class.getResource("goldCards.json")).getPath();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return Arrays.stream(this.gson.fromJson(reader, GoldCard[].class)).toList();
        } catch (IOException ex) {
            //noinspection CallToPrintStackTrace
            ex.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    /**
     * Finds the path to the JSON file containing the cards and reads it as a stream that is converted into a list of cards.
     * @return the list of cards.
     * See the following path for the starterCards JSON file: {@code src/main/resources/starterCards.jason}.
     */
    private List<StarterCard> loadStartersFromJson() {
        String filePath = Objects.requireNonNull(DeckLoader.class.getResource("starterCards.json")).getPath();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return Arrays.stream(this.gson.fromJson(reader, StarterCard[].class)).toList();
        } catch (IOException ex) {
            //noinspection CallToPrintStackTrace
            ex.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    /**
     * Finds the path to the JSON file containing the cards and reads it as a stream that is converted into a list of cards.
     * @return the list of cards.
     * See the following path for the objectiveCards JSON file: {@code src/main/resources/objectiveCards.jason}.
     */
    private List<ObjectiveCard> loadObjectivesFromJson() {
        String filePath = Objects.requireNonNull(DeckLoader.class.getResource("objectiveCards.json")).getPath();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return Arrays.stream(this.gson.fromJson(reader, ObjectiveCard[].class)).toList();
        } catch (IOException ex) {
            //noinspection CallToPrintStackTrace
            ex.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
