package it.polimi.ingsw.am49.model.cards.adapters;

import com.google.gson.*;
import it.polimi.ingsw.am49.model.cards.placeables.BasicPointsStrategy;
import it.polimi.ingsw.am49.model.cards.placeables.CornersPointsStrategy;
import it.polimi.ingsw.am49.model.cards.placeables.ItemPointsStrategy;
import it.polimi.ingsw.am49.model.cards.placeables.PlacementPointsStrategy;
import it.polimi.ingsw.am49.model.enumerations.Item;

import java.lang.reflect.Type;

public class PlacementPointsStrategyTypeAdapter implements JsonSerializer<PlacementPointsStrategy>, JsonDeserializer<PlacementPointsStrategy> {

    @Override
    public JsonElement serialize(PlacementPointsStrategy src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", src.getClass().getSimpleName());
        if (src instanceof ItemPointsStrategy)
            jsonObject.addProperty("item", ((ItemPointsStrategy)src).getItem().name());

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
            case "ItemPointsStrategy" -> {
                Item item = Item.valueOf(jsonObj.get("item").getAsString());
                yield new ItemPointsStrategy(item);
            }
            case "CornersPointsStrategy" -> new CornersPointsStrategy();
            case "BasicPointsStrategy" -> new BasicPointsStrategy();
            default -> throw new JsonParseException("Unknown PlacementPointsStrategy type: " + name);
        };
    }
}