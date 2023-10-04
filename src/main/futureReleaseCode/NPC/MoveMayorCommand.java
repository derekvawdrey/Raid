package me.amogus360.raid.Commands.Faction.NPC;

import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.RaidCommandManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MoveMayorCommand extends RaidCommand {
    public MoveMayorCommand(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }
    @Override
    public void execute(CommandSender sender, String[] args, RaidCommandManager commandManager) {
        if (!(sender instanceof Player)) {
            MessageManager.sendMessage(sender,"This command can only be used by players.");
            return;
        }

        Player player = (Player) sender;
        DataAccessManager dataAccessManager = commandManager.getDataAccessManager();
        // Check if the player's location is within a claimed land
        if (!dataAccessManager.getLandClaimDao().isClaimed(player.getLocation())) {
            MessageManager.sendMessage(player,"You cannot move the Mayor NPC outside of claimed land.");
            return;
        }

        String npcTitle = "Mayor";
        int factionId = dataAccessManager.getFactionDao().getFactionIdByPlayerUUID(player.getUniqueId());
        int npcId = dataAccessManager.getNpcDataDao().getNpcIdForTitleAndFaction(npcTitle, factionId);
        NPC mayor = CitizensAPI.getNPCRegistry().getById(npcId);
        mayor.setMoveDestination(player.getLocation());
        mayor.teleport(player.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        dataAccessManager.getNpcDataDao().updateNpcCoordinates(npcId, player.getLocation());
        return;
    }
}