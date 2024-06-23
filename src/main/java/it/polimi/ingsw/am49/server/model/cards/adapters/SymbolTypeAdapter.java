package it.polimi.ingsw.am49.server.model.cards.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.am49.common.enumerations.Symbol;

import java.lang.reflect.Type;

/**
 * This class provides a custom deserializer for the {@link Symbol} enum using Gson.
 */
public class SymbolTypeAdapter implements JsonDeserializer<Symbol> {
    /**
     * Deserializes the JSON element into a {@link Symbol} enum.
     * 
     * @param json The JSON data being deserialized.
     * @param typeOfT The type of the Object to deserialize to.
     * @param context Context for deserialization.
     * @return The deserialized {@link Symbol} enum.
     * @throws JsonParseException if the provided JSON cannot be converted to {@link Symbol}.
     */
    @Override
    public Symbol deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String jsonValue = json.getAsString();
        try {
            return Symbol.valueOf(jsonValue);
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Unexpected value for enum type Symbol: " + jsonValue);
        }
    }
}
