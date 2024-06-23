package it.polimi.ingsw.am49.server.model.cards.adapters;

import com.google.gson.*;
import it.polimi.ingsw.am49.server.model.cards.placeables.BasicPointsStrategy;
import it.polimi.ingsw.am49.server.model.cards.placeables.CornersPointsStrategy;
import it.polimi.ingsw.am49.server.model.cards.placeables.SymbolsPointsStrategy;
import it.polimi.ingsw.am49.server.model.cards.placeables.PlacementPointsStrategy;
import it.polimi.ingsw.am49.common.enumerations.Symbol;

import java.lang.reflect.Type;

/**
 * A type adapter for the PlacementPointsStrategy interface for Gson serialization and deserialization.
 */
public class PlacementPointsStrategyTypeAdapter implements JsonSerializer<PlacementPointsStrategy>, JsonDeserializer<PlacementPointsStrategy> {

    /**
     * Serializes a PlacementPointsStrategy object to JSON.
     * @param src The source PlacementPointsStrategy object to serialize.
     * @param typeOfSrc The specific type of src.
     * @param context The serialization context.
     * @return A JsonElement representing the serialized form of src.
     */
    @Override
    public JsonElement serialize(PlacementPointsStrategy src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", src.getClass().getSimpleName());
        if (src instanceof SymbolsPointsStrategy)
            jsonObject.addProperty("symbol", ((SymbolsPointsStrategy)src).getSymbol().name());

        return jsonObject;
    }

    /**
     * Deserializes a JSON element into a PlacementPointsStrategy object.
     * @param json The JSON data being deserialized.
     * @param typeOfT The type of the Object to deserialize to.
     * @param context The deserialization context.
     * @return A PlacementPointsStrategy object deserialized from the JSON string.
     * @throws JsonParseException if json is not in the expected format of a PlacementPointsStrategy.
     */
    @Override
    public PlacementPointsStrategy deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObj;
        String name;
        try {
            jsonObj = json.getAsJsonObject(); // Get the class name from JSON
            name = jsonObj.get("name").getAsString();
        } catch (IllegalStateException ex) {
            throw new JsonParseException("Malformed placePoints attribute");
        }

        return switch (name) {
            case "SymbolsPointsStrategy" -> {
                Symbol symbol = Symbol.valueOf(jsonObj.get("symbol").getAsString());
                yield new SymbolsPointsStrategy(symbol);
            }
            case "CornersPointsStrategy" -> new CornersPointsStrategy();
            case "BasicPointsStrategy" -> new BasicPointsStrategy();
            default -> throw new JsonParseException("Unknown PlacementPointsStrategy type: " + name);
        };
    }
}