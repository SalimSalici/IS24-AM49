package it.polimi.ingsw.am49.model.cards.adapters;

import com.google.gson.*;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectivePointsStrategy;
import it.polimi.ingsw.am49.model.cards.objectives.PatternObjectiveStrategy;
import it.polimi.ingsw.am49.model.cards.objectives.SymbolsObjectiveStrategy;

import java.lang.reflect.Type;

public class ObjectiveCardTypeAdapter implements JsonDeserializer<ObjectiveCard>, JsonSerializer<ObjectiveCard> {

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
