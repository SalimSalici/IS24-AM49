package it.polimi.ingsw.am49.server.model.cards.adapters;

import com.google.gson.*;
import it.polimi.ingsw.am49.server.model.cards.placeables.GoldCard;
import it.polimi.ingsw.am49.server.model.cards.placeables.PlacementPointsStrategy;
import it.polimi.ingsw.am49.common.enumerations.Resource;
import it.polimi.ingsw.am49.common.enumerations.Symbol;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * A custom deserializer for {@link GoldCard} objects that implements {@link JsonDeserializer}.
 * This deserializer helps in converting JSON objects into {@link GoldCard} instances
 * considering the specific structure and requirements of the GoldCard class.
 */
public class GoldCardTypeAdapter implements JsonDeserializer<GoldCard> {

    /**
     * Deserializes the JSON representation of a {@link GoldCard}.
     * 
     * @param json The JSON data being deserialized.
     * @param typeOfT The type of the Object to deserialize to.
     * @param context Context for deserialization that is passed to a custom deserializer during invocation of its {@link JsonDeserializer#deserialize} method.
     * @return The deserialized {@link GoldCard} object.
     * @throws JsonParseException if the JSON is not in the expected format.
     */
    @Override
    public GoldCard deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject card = json.getAsJsonObject();
        JsonElement pointsStrategyJson = card.getAsJsonObject("pointsStrategy");
        Map<String, JsonElement> priceJson = card.getAsJsonObject("price").asMap();
        Map<Symbol, Integer> price = new HashMap<>();
        boolean isGoldCard = true;
        priceJson.forEach((k, v) -> price.put(Symbol.valueOf(k), v.getAsInt()));
        PlacementPointsStrategy pointsStrategy = context.deserialize(pointsStrategyJson, PlacementPointsStrategy.class);
        return new GoldCard(
                card.get("id").getAsInt(),
                context.deserialize(card.get("tr"), Symbol.class),
                context.deserialize(card.get("tl"), Symbol.class),
                context.deserialize(card.get("br"), Symbol.class),
                context.deserialize(card.get("bl"), Symbol.class),
                Resource.valueOf(card.get("resource").getAsString()),
                card.get("points").getAsInt(),
                pointsStrategy,
                price,
                isGoldCard
        );
    }
}
