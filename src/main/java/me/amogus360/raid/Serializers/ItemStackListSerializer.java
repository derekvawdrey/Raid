package me.amogus360.raid.Serializers;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ItemStackListSerializer implements JsonSerializer<List<ItemStack>>, JsonDeserializer<List<ItemStack>> {
    @Override
    public JsonElement serialize(List<ItemStack> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray jsonArray = new JsonArray();
        for (ItemStack itemStack : src) {
            JsonObject jsonItemStack = new JsonObject();
            jsonItemStack.addProperty("type", itemStack.getType().toString());
            jsonItemStack.addProperty("amount", itemStack.getAmount());
            ItemMeta meta = itemStack.getItemMeta();
            if (meta.hasDisplayName()) {
                jsonItemStack.addProperty("displayName", meta.getDisplayName());
            }
            if (meta.hasLore()) {
                jsonItemStack.add("lore", context.serialize(meta.getLore()));
            }

            // Serialize enchantments
            if (meta.hasEnchants()) {
                JsonObject enchantmentsObject = new JsonObject();
                for (Enchantment enchantment : meta.getEnchants().keySet()) {
                    enchantmentsObject.addProperty(enchantment.getKey().getKey(), meta.getEnchantLevel(enchantment));
                }
                jsonItemStack.add("enchants", enchantmentsObject);
            }

            jsonArray.add(jsonItemStack);
        }
        return jsonArray;
    }

    @Override
    public List<ItemStack> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<ItemStack> itemStackList = new ArrayList<>();
        if (json.isJsonArray()) {
            JsonArray jsonArray = json.getAsJsonArray();
            for (JsonElement element : jsonArray) {
                if (element.isJsonObject()) {
                    JsonObject jsonItemStack = element.getAsJsonObject();
                    Material type = Material.valueOf(jsonItemStack.get("type").getAsString());
                    int amount = jsonItemStack.get("amount").getAsInt();
                    ItemStack itemStack = new ItemStack(type, amount);
                    ItemMeta meta = Bukkit.getItemFactory().getItemMeta(type);
                    if (jsonItemStack.has("displayName")) {
                        meta.setDisplayName(jsonItemStack.get("displayName").getAsString());
                    }
                    if (jsonItemStack.has("lore")) {
                        meta.setLore(context.deserialize(jsonItemStack.get("lore"), List.class));
                    }

                    // Deserialize enchantments
                    if (jsonItemStack.has("enchants")) {
                        JsonObject enchantmentsObject = jsonItemStack.getAsJsonObject("enchants");
                        for (String enchantmentKey : enchantmentsObject.keySet()) {
                            Enchantment enchantment = Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(enchantmentKey));
                            int level = enchantmentsObject.get(enchantmentKey).getAsInt();
                            if (enchantment != null) {
                                meta.addEnchant(enchantment, level, true);
                            }
                        }
                    }

                    itemStack.setItemMeta(meta);
                    itemStackList.add(itemStack);
                }
            }
        }
        return itemStackList;
    }
}
