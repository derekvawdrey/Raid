package me.amogus360.raid;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class MessageManager {

    // Method to send a prefixed message to a CommandSender
    public static void sendMessage(CommandSender sender, String message) {
        String prefix = ChatColor.DARK_RED + "[" + ChatColor.GOLD + "Raid" + ChatColor.DARK_RED + "]" + ChatColor.RESET;
        String styledMessage = ChatColor.YELLOW + ">> " + ChatColor.WHITE + message + ChatColor.YELLOW + " <<";
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
}
