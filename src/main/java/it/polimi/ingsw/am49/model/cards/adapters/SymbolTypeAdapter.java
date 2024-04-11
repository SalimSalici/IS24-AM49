package it.polimi.ingsw.am49.model.cards.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.am49.model.enumerations.Symbol;

import java.lang.reflect.Type;

public class SymbolTypeAdapter implements JsonDeserializer<Symbol> {
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
