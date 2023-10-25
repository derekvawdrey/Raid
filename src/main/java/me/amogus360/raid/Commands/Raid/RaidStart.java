package me.amogus360.raid.Commands.Raid;
import me.amogus360.raid.CommandManager.CommandManager;
import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.Model.FactionInfo;
import me.amogus360.raid.Utilities.RaidBossUtilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Date;

public class RaidStart extends RaidCommand {

    public RaidStart(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }

    //TODO: for rewarding what do we do if a player (who is a friend with another player) purposefully suffocates their entity so the raiding faction gets the resources
    @Override
    public void execute(CommandSender sender, String[] args, CommandManager commandManager) {
        if (!(sender instanceof Player)) {
            String factionName = args[0];
            FactionInfo factionInfo = commandManager.getDataAccessManager().getFactionDao().getFactionInfoByName(factionName);

            if (factionInfo != null) {
                MessageManager.sendGlobalMessage(plugin, "God has declared a raid on " + factionInfo.getFactionName());
                MessageManager.sendGlobalMessage(plugin, "The raid will begin 15 minutes from now, if other factions want to join, do /raid join [faction_name].");
                commandManager.getDataAccessManager().getRaidDao().addRaid(0, factionInfo.getFactionId(), commandManager.getDataAccessManager().getRaidDao().getTimeToStartRaid().toString(), commandManager.getDataAccessManager().getRaidDao().getTimeToEndRaid().toString());
            }
            return;
        }

        if (args.length != 1) {
            this.tellUsage(sender);
            return;
        }

        Player player = (Player) sender;
        if (!commandManager.getDataAccessManager().getFactionDao().isPlayerFactionOwner(player.getUniqueId()) &&
                !commandManager.getDataAccessManager().getFactionDao().hasFactionTitle(player.getUniqueId(), "officer")) {
            MessageManager.sendMessage(player, "You are not an officer or owner of a faction, you can't declare a raid!");
            return;
        }

        String factionName = args[0];
        FactionInfo factionInfo = commandManager.getDataAccessManager().getFactionDao().getFactionInfoByName(factionName);
        FactionInfo raiderFactionInfo = commandManager.getDataAccessManager().getFactionDao().getFactionInfoByPlayerUUID(player.getUniqueId());

        if (factionInfo != null && raiderFactionInfo != null) {
            if (factionInfo.getFactionName().equals(raiderFactionInfo.getFactionName())) {
                MessageManager.sendMessage(sender, "You can't declare a raid against yourself!");
                return;
            }


            // Check if a raid has already been declared against the target faction
            if (commandManager.getDataAccessManager().getRaidDao().isRaidDeclared(factionInfo.getFactionId())) {
                MessageManager.sendMessage(sender, "A raid has already been declared against " + factionInfo.getFactionName() + ". You cannot declare another one.");
                return;
            }


            MessageManager.sendGlobalMessage(plugin, "The faction " + raiderFactionInfo.getFactionName() + " has declared a raid on " + factionInfo.getFactionName());
            MessageManager.sendGlobalMessage(plugin, "The raid will begin 15 minutes from now, if other factions want to join, do /raid join [faction_name].");
            commandManager.getDataAccessManager().getRaidDao().addRaid(raiderFactionInfo.getFactionId(), factionInfo.getFactionId(), commandManager.getDataAccessManager().getRaidDao().getTimeToStartRaid().toString(), commandManager.getDataAccessManager().getRaidDao().getTimeToEndRaid().toString());
        }else{
            MessageManager.sendMessage(player, "That faction doesn't exist");
        }
    }



}
