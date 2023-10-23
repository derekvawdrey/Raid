package me.amogus360.raid.Commands.Raid;

import me.amogus360.raid.CommandManager.CommandManager;
import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.Model.FactionInfo;
import me.amogus360.raid.Model.RaidInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.util.List;

public class RaidJoin extends RaidCommand {

    public RaidJoin(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }

    @Override
    public void execute(CommandSender sender, String[] args, CommandManager commandManager) {
        if (!(sender instanceof Player)) {
            MessageManager.sendMessage(sender, "Only players can use this command.");
            return;
        }

        if (args.length != 1) {
            this.tellUsage(sender);
            return;
        }

        Player player = (Player) sender;
        String factionName = args[0];
        FactionInfo raiderFactionInfo = commandManager.getDataAccessManager().getFactionDao().getFactionInfoByPlayerUUID(player.getUniqueId());

        if (raiderFactionInfo == null) {
            MessageManager.sendMessage(player, "You are not a member of any faction. Join a faction to participate in raids.");
            return;
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        List<RaidInfo> upAndComing = commandManager.getDataAccessManager().getRaidDao().getUpcomingRaids();

        // Check if the faction is already participating in a raid
        for (RaidInfo raid : upAndComing) {
            if (raid.getAttackingFactionId() == raiderFactionInfo.getFactionId()) {
                MessageManager.sendMessage(player, "Your faction is already participating in a raid.");
                return;
            }
        }

        FactionInfo defendingFactionInfo = commandManager.getDataAccessManager().getFactionDao().getFactionInfoByName(factionName);

        if (defendingFactionInfo == null) {
            MessageManager.sendMessage(player, "The specified faction does not exist.");
            return;
        }

        // Check if the defending faction is the same as the raider's faction
        if (raiderFactionInfo.getFactionId() == defendingFactionInfo.getFactionId()) {
            MessageManager.sendMessage(player, "Your faction cannot join a raid against itself.");
            return;
        }

        // Check if the defending faction is currently being raided
        for (RaidInfo raid : upAndComing) {
            if (raid.getDefendingFactionId() == defendingFactionInfo.getFactionId()) {
                LocalDateTime raidStartTime = LocalDateTime.parse(raid.getStartTime());
                if (currentDateTime.isBefore(raidStartTime)) {
                    // Add the raider to the raid
                    commandManager.getDataAccessManager().getRaidDao().addParticipantToRaid(raid.getRaidId(), raiderFactionInfo.getFactionId());
                    MessageManager.sendMessage(player, "You have joined the raid against " + factionName + ".");
                } else {
                    MessageManager.sendMessage(player, "The raid has already started, and you cannot join it.");
                }
                return;
            }
        }

        MessageManager.sendMessage(player, "No raid has been declared against this faction");
    }

}
