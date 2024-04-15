package it.polimi.ingsw.am49.model.cards.adapters;

import com.google.gson.*;
import it.polimi.ingsw.am49.model.cards.placeables.GoldCard;
import it.polimi.ingsw.am49.model.cards.placeables.PlacementPointsStrategy;
import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.model.enumerations.Symbol;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class GoldCardTypeAdapter implements JsonDeserializer<GoldCard> {

    @Override
    public GoldCard deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject card = json.getAsJsonObject();
        JsonElement pointsStrategyJson = card.getAsJsonObject("pointsStrategy");
        Map<String, JsonElement> priceJson = card.getAsJsonObject("price").asMap();
        Map<Symbol, Integer> price = new HashMap<>();
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
                price
        );
    }
}
