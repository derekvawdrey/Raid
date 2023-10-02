package me.amogus360.raid.Model.Block;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.amogus360.raid.Serializers.ItemStackListSerializer;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public class BlockInfo {
    private int x;
    private int y;
    private int z;
    private String material;
    private String blockData;
    private List<ItemStack> chestContents;

    private String worldName;

    // Getter and Setter methods for each field
    public int getX() {


        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getBlockData() {
        return blockData;
    }

    public void setBlockData(String blockData) {
        this.blockData = blockData;
    }

    public List<ItemStack> getChestContents() {
        return chestContents;
    }

    public void setChestContents(List<ItemStack> chestContents) {
        this.chestContents = chestContents;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public String toJson(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(new TypeToken<List<ItemStack>>() {}.getType(), new ItemStackListSerializer());
        Gson gson = gsonBuilder.create();
        return gson.toJson(this);
    }

    public static BlockInfo fromJson(String jsonString){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(new TypeToken<List<ItemStack>>() {}.getType(), new ItemStackListSerializer());
        Gson gson = gsonBuilder.create();
        BlockInfo blockInfo = gson.fromJson(jsonString, BlockInfo.class);
        return blockInfo;
    }

}
