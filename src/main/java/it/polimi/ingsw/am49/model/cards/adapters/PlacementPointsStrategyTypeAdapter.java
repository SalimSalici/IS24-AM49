package it.polimi.ingsw.am49.model.cards.adapters;

import com.google.gson.*;
import it.polimi.ingsw.am49.model.cards.placeables.BasicPointsStrategy;
import it.polimi.ingsw.am49.model.cards.placeables.CornersPointsStrategy;
import it.polimi.ingsw.am49.model.cards.placeables.SymbolsPointsStrategy;
import it.polimi.ingsw.am49.model.cards.placeables.PlacementPointsStrategy;
import it.polimi.ingsw.am49.model.enumerations.Symbol;

import java.lang.reflect.Type;

public class PlacementPointsStrategyTypeAdapter implements JsonSerializer<PlacementPointsStrategy>, JsonDeserializer<PlacementPointsStrategy> {

    @Override
    public JsonElement serialize(PlacementPointsStrategy src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", src.getClass().getSimpleName());
        if (src instanceof SymbolsPointsStrategy)
            jsonObject.addProperty("symbol", ((SymbolsPointsStrategy)src).getSymbol().name());

        return jsonObject;
    }

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