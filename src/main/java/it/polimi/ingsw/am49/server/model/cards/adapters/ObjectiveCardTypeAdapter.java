package it.polimi.ingsw.am49.server.model.cards.adapters;

import com.google.gson.*;
import it.polimi.ingsw.am49.server.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.server.model.cards.objectives.ObjectivePointsStrategy;
import it.polimi.ingsw.am49.server.model.cards.objectives.PatternObjectiveStrategy;
import it.polimi.ingsw.am49.server.model.cards.objectives.SymbolsObjectiveStrategy;

import java.lang.reflect.Type;

/**
 * A custom type adapter for {@link ObjectiveCard} objects that implements both {@link JsonSerializer} and {@link JsonDeserializer}.
 * This adapter helps in converting {@link ObjectiveCard} instances to and from JSON representations.
 */
public class ObjectiveCardTypeAdapter implements JsonDeserializer<ObjectiveCard>, JsonSerializer<ObjectiveCard> {

    /**
     * Serializes an {@link ObjectiveCard} instance into its JSON representation.
     * 
     * @param src The source {@link ObjectiveCard} that needs to be serialized.
     * @param typeOfSrc The specific generalized type of source object.
     * @param context The context of the JSON serialization process.
     * @return A {@link JsonElement} corresponding to the serialized JSON form of the {@link ObjectiveCard}.
     */
    @Override
    public JsonElement serialize(ObjectiveCard src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        ObjectivePointsStrategy strategy = src.getPointsStrategy();
        jsonObject.addProperty("id", src.getId());
        jsonObject.addProperty("points", src.getPoints());
        jsonObject.addProperty("name", strategy.getClass().getSimpleName());
        jsonObject.add("object", context.serialize(strategy, strategy.getClass()));

        return jsonObject;
    }

    /**
     * Deserializes a JSON element into an {@link ObjectiveCard} instance.
     * 
     * @param json The JSON data being deserialized.
     * @param typeOfT The type of the Object to deserialize to.
     * @param context Context for deserialization that is passed to a custom deserializer during invocation of its {@link JsonDeserializer#deserialize} method.
     * @return The deserialized {@link ObjectiveCard} object.
     * @throws JsonParseException if the JSON is not in the expected format or if an unknown strategy type is encountered.
     */
    @Override
    public ObjectiveCard deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        ObjectivePointsStrategy strategy = switch (jsonObject.get("name").getAsString()) {
            case "SymbolsObjectiveStrategy" -> context.deserialize(jsonObject.get("object"), SymbolsObjectiveStrategy.class);
            case "PatternObjectiveStrategy" -> context.deserialize(jsonObject.get("object"), PatternObjectiveStrategy.class);
            default -> throw new JsonParseException("Error parsing objectives");
        };
        return new ObjectiveCard(
                jsonObject.get("id").getAsInt(),
                jsonObject.get("points").getAsInt(),
                strategy
        );
    }
}
