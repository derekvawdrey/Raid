package me.amogus360.raid.Utilities;

import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.Model.ItemGlow;
import me.amogus360.raid.Model.LoreLevelInformation;
import me.amogus360.raid.Model.RaidTools.WeaponHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RaidToolsUtils {

    public static ItemStack returnItemStackForWeaponHandler(DataAccessManager dataAccessManager, WeaponHandler weaponHandler){
        if(weaponHandler.getItemType() == WeaponHandler.ItemType.Enhancement){
            String activationLore = weaponHandler.getActivationLore();

            ItemStack weaponItem = new ItemStack(Material.QUARTZ); // Create an ItemStack with your desired material
            ItemMeta itemMeta = weaponItem.getItemMeta();

            // Create a sample lore string to match against the item's lore
            String sampleLore = RaidToolsUtils.createLoreString(activationLore, 1);

            // Set the lore of the displayed item
            itemMeta.setLore(Collections.singletonList(sampleLore));
            itemMeta.setDisplayName(weaponHandler.getItemName());
            NamespacedKey key = new NamespacedKey(dataAccessManager.getPlugin(), dataAccessManager.getPlugin().getDescription().getName());
            ItemGlow glow = new ItemGlow(key);
            itemMeta.addEnchant(glow, 1, true);
            weaponItem.setItemMeta(itemMeta);
            return weaponItem;
        }else if(weaponHandler.getItemType() == WeaponHandler.ItemType.SingleUse){
            String activationLore = weaponHandler.getActivationLore();

            ItemStack weaponItem = new ItemStack(Material.BLAZE_POWDER); // Create an ItemStack with your desired material
            ItemMeta itemMeta = weaponItem.getItemMeta();

            // Create a sample lore string to match against the item's lore
            String sampleLore = RaidToolsUtils.createLoreString(activationLore, 1);

            // Set the lore of the displayed item
            itemMeta.setLore(Collections.singletonList(sampleLore));
            itemMeta.setDisplayName(weaponHandler.getItemName());
            NamespacedKey key = new NamespacedKey(dataAccessManager.getPlugin(), dataAccessManager.getPlugin().getDescription().getName());
            ItemGlow glow = new ItemGlow(key);
            itemMeta.addEnchant(glow, 1, true);
            weaponItem.setItemMeta(itemMeta);
            return weaponItem;
        }else if(weaponHandler.getItemType() == WeaponHandler.ItemType.RaidingTool){
            String activationLore = weaponHandler.getActivationLore();

            ItemStack weaponItem = new ItemStack(Material.GOAT_HORN); // Create an ItemStack with your desired material
            ItemMeta itemMeta = weaponItem.getItemMeta();

            // Create a sample lore string to match against the item's lore
            String sampleLore = RaidToolsUtils.createLoreString(activationLore, 1);

            // Set the lore of the displayed item
            itemMeta.setLore(Collections.singletonList(sampleLore));
            itemMeta.setDisplayName(weaponHandler.getItemName());
            NamespacedKey key = new NamespacedKey(dataAccessManager.getPlugin(), dataAccessManager.getPlugin().getDescription().getName());
            ItemGlow glow = new ItemGlow(key);
            itemMeta.addEnchant(glow, 1, true);
            weaponItem.setItemMeta(itemMeta);
            return weaponItem;
        }


        // Just a filler for now
        String activationLore = weaponHandler.getActivationLore();

        ItemStack weaponItem = new ItemStack(Material.APPLE); // Create an ItemStack with your desired material
        ItemMeta itemMeta = weaponItem.getItemMeta();

        // Create a sample lore string to match against the item's lore
        String sampleLore = RaidToolsUtils.createLoreString(activationLore, 1);

        // Set the lore of the displayed item
        itemMeta.setLore(Collections.singletonList(sampleLore));
        itemMeta.setDisplayName(weaponHandler.getItemName());
        NamespacedKey key = new NamespacedKey(dataAccessManager.getPlugin(), dataAccessManager.getPlugin().getDescription().getName());
        ItemGlow glow = new ItemGlow(key);
        itemMeta.addEnchant(glow, 1, true);
        weaponItem.setItemMeta(itemMeta);
        return weaponItem;

    }

    public static List<Block> onTNTExplode(Location source, List<Block> blocks) {
        int radius = (int) Math.ceil(7);


        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location loc = new Location(source.getWorld(), x + source.getX(), y + source.getY(), z + source.getZ());
                    if (source.distance(loc) <= radius) {
                        if(isBlockToRemove(loc.getBlock().getType())) blocks.add(loc.getBlock());
                    }
                }
            }
        }
        return blocks;
    }

    public static boolean isBlockToRemove(Material material) {
        // Define which block types should be removed during the explosion
        Material[] removableBlocks = {
                Material.OBSIDIAN, // Remove obsidian
                Material.WATER,
                Material.LAVA,
                Material.CRYING_OBSIDIAN,
                Material.END_CRYSTAL,
                Material.REDSTONE,
                Material.STRING,
                Material.REDSTONE_TORCH,
                Material.REDSTONE_WALL_TORCH,
                Material.COMPARATOR,
                Material.REPEATER,
                Material.STICKY_PISTON,
                Material.PISTON,
                Material.BAMBOO_BUTTON,
                Material.ACACIA_BUTTON,
                Material.CHERRY_BUTTON,
                Material.BIRCH_BUTTON,
                Material.JUNGLE_BUTTON,
                Material.CRIMSON_BUTTON,
                Material.MANGROVE_BUTTON,
                Material.POLISHED_BLACKSTONE_BUTTON,
                Material.STONE_BUTTON,
                Material.WARPED_BUTTON,
                Material.LEVER
                // Add more block types as needed
        };

        for (Material blockType : removableBlocks) {
            if (material == blockType) {
                return true;
            }
        }

        return false;
    }


    public static boolean hasTNT(Player player) {
        ItemStack[] inventory = player.getInventory().getContents();
        for (ItemStack itemStack : inventory) {
            if (itemStack != null && itemStack.getType() == Material.TNT) {
                return true;
            }
        }
        return false;
    }

    public static void consumeTNT(Player player) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (itemStack != null && itemStack.getType() == Material.TNT) {
                itemStack.setAmount(itemStack.getAmount() - 1);
                if (itemStack.getAmount() <= 0) {
                    player.getInventory().setItem(i, new ItemStack(Material.AIR));
                }
                return;
            }
        }
    }

    public static List<LoreLevelInformation> getLoreLevels(ItemMeta itemMeta) {
        List<LoreLevelInformation> loreLevelInformations = new ArrayList<>();

        if (itemMeta.hasLore()) {
            List<String> lore = itemMeta.getLore();

            for (String loreLine : lore) {

                LoreLevelInformation loreInfo = parseLoreLine(loreLine);
                if (loreInfo != null) {
                    loreLevelInformations.add(loreInfo);
                }
            }
        }

        return loreLevelInformations;
    }

    public static String createLoreString(String enhancementType, int level) {
        // Convert the level to a Roman numeral
        String romanNumeral = convertArabicToRoman(level);

        // Create the lore string with the enhancement type and Roman numeral
        String loreString = enhancementType + " " + romanNumeral;

        return loreString;
    }

    private static String convertArabicToRoman(int number) {
        if (number < 1 || number > 10) {
            throw new IllegalArgumentException("Number out of Roman numeral range (1-10)");
        }

        String[] romanNumerals = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return romanNumerals[number - 1];
    }


    public static LoreLevelInformation parseLoreLine(String loreLine) {
        // Define a regular expression pattern to match the enhancement type and level
        Pattern pattern = Pattern.compile("^(§[a-zA-Z\\s]+)\\s([IVXLCDM]+)$");
        Matcher matcher = pattern.matcher(loreLine);

        if (matcher.find()) {

            // TODO: Insert things to avoid

            String enhancementType = matcher.group(1).trim();
            String romanNumeral = matcher.group(2).trim();
            int level = convertRomanToArabic(romanNumeral);
            return new LoreLevelInformation(enhancementType, level);
        }

        return null;
    }

    private static int convertRomanToArabic(String romanNumeral) {
        // You need to implement a method to convert Roman numerals to Arabic numbers.
        // Here's a simple example, but you can find more robust implementations online.
        int result = 0;
        int previousValue = 0;

        for (int i = romanNumeral.length() - 1; i >= 0; i--) {
            char currentChar = romanNumeral.charAt(i);
            int currentValue = getValueOfRomanNumeral(currentChar);

            if (currentValue < previousValue) {
                result -= currentValue;
            } else {
                result += currentValue;
            }

            previousValue = currentValue;
        }

        return result;
    }

    private static int getValueOfRomanNumeral(char numeral) {
        switch (numeral) {
            case 'I':
                return 1;
            case 'V':
                return 5;
            case 'X':
                return 10;
            case 'L':
                return 50;
            case 'C':
                return 100;
            case 'D':
                return 500;
            case 'M':
                return 1000;
            default:
                return 0;
        }
    }


}
