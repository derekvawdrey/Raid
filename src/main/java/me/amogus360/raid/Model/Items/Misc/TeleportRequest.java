package me.amogus360.raid.Model.Items.Misc;

import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeleportRequest {
    private final Player activator;
    private Player target;

    public TeleportRequest(Player activator) {
        this.activator = activator;
    }

    public void setTarget(Player target) {
        this.target = target;
    }
    public boolean sendTeleportationRequest(String targetName) {
        Player target = Bukkit.getPlayerExact(targetName);

        if (target != null && target.isOnline()) {
            setTarget(target);

            // Build the clickable message for accepting
            TextComponent acceptMessage = new TextComponent(ChatColor.YELLOW + "Click [ACCEPT]");
            acceptMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + activator.getName()));

            // Build the clickable message for denying
            TextComponent denyMessage = new TextComponent(ChatColor.RED + "click [DENY]");
            denyMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny " + activator.getName()));

            // Combine the messages
            TextComponent message = new TextComponent(ChatColor.GREEN + activator.getName() + " requested to teleport to you. ");
            message.addExtra(acceptMessage);
            message.addExtra(ChatColor.GREEN + " or ");
            message.addExtra(denyMessage);

            // Send the message
            MessageManager.sendMessage(target, message);

            MessageManager.sendMessage(activator, ChatColor.GREEN + "Teleportation request sent to " + target.getName());
            return true;
        } else {
            MessageManager.sendMessage(activator, ChatColor.RED + "Player not found or not online.");
            return false;
        }
    }

    public boolean waitingForDecision(){
        return this.target != null;
    }

    public void handleTeleportationResponse(boolean accepted, DataAccessManager dataAccessManager) {
        if (accepted) {
            activator.teleport(target.getLocation());
            MessageManager.sendMessage(activator, ChatColor.GREEN + "Teleportation successful!");
        } else {
            MessageManager.sendMessage(activator, ChatColor.RED + "Teleportation request denied.");
        }
        dataAccessManager.getItemDao().removeFromTeleportRequest(activator.getName());
    }
}
