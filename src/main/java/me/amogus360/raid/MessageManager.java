package me.amogus360.raid;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class MessageManager {
    public static enum MessageType{
        ERROR,
        BAD_PERMISSION,

    }
    // Method to send a prefixed message to a CommandSender
    public static void sendMessage(CommandSender sender, String message) {
        String prefix = getPluginPrefixColors("Factions Revived");
        String styledMessage = ChatColor.WHITE + message + ChatColor.YELLOW + " <<";
        sender.sendMessage(prefix + " " + styledMessage);
    }

    // Method to send a prefixed message to a Player
    public static void sendMessage(Player player, String message) {
        sendMessage((CommandSender) player, message);
    }
    public static void sendGlobalMessage(Plugin plugin, String message){
        for(Player player: plugin.getServer().getOnlinePlayers()){
            sendMessage(player, message);
        }
    }

    public static String getPluginPrefixColors(String stringName){
        String prefix = ChatColor.WHITE + "[" + ChatColor.GOLD + stringName + ChatColor.WHITE + "]" + ChatColor.RESET;
        return prefix;
    }

    public static void sendMessageBasedOnType(Player player, MessageType messageType){

    }
}
