package me.amogus360.raid.Serializers;

import com.google.gson.*;

import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemStackListSerializer implements JsonSerializer<List<ItemStack>>, JsonDeserializer<List<ItemStack>> {
    @Override
    public JsonElement serialize(List<ItemStack> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray jsonArray = new JsonArray();
        for (ItemStack itemStack : src) {
            jsonArray.add(new JsonPrimitive(itemStack.serialize().toString()));
        }
        return jsonArray;
    }

    @Override
    public List<ItemStack> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<ItemStack> itemStackList = new ArrayList<>();
        if (json.isJsonArray()) {
            JsonArray jsonArray = json.getAsJsonArray();
            for (JsonElement element : jsonArray) {
                if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                    String itemStackString = element.getAsString();
                    JsonObject itemStackJson = JsonParser.parseString(itemStackString).getAsJsonObject();
                    Map<String, Object> itemStackMap = context.deserialize(itemStackJson, Map.class);
                    itemStackList.add(ItemStack.deserialize(itemStackMap));
                }
            }
        }
        return itemStackList;
    }

}
