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
            MessageManager.sendMessage(sender,"Only players can use this command.");
            return;
        }


        if(args.length != 1){
            this.tellUsage(sender);
        }
        Player player = (Player) sender;
        if(!commandManager.getDataAccessManager().getFactionDao().isPlayerFactionOwner(player.getUniqueId())
                && !commandManager.getDataAccessManager().getFactionDao().hasFactionTitle(player.getUniqueId(), "officer")){
            MessageManager.sendMessage(player, "You are not an officer or owner of a faction, you can't declare a raid!");
        }

        String factionName = args[0];
        FactionInfo factionInfo = commandManager.getDataAccessManager().getFactionDao().getFactionInfoByName(factionName);
        FactionInfo raiderFactionInfo = commandManager.getDataAccessManager().getFactionDao().getFactionInfoByPlayerUUID(player.getUniqueId());
        //TODO: check if the faction has already been raided today.
        //TODO: Check if the person is someone who can declare a raid
        //TODO: add a tax to the player to start the raid
        //TODO: Stop someone from declaring a raid on themselves
        //TODO: Stop another raid from being decalred at the same time
        if(factionInfo != null && raiderFactionInfo != null){
            LocalDateTime currentDateTime = LocalDateTime.now();
            TemporalAmount duration = java.time.Duration.of(2, ChronoUnit.MINUTES);
            TemporalAmount duration_end = java.time.Duration.of(4, ChronoUnit.MINUTES);
            // Calculate the future date and time
            LocalDateTime futureDateTime = currentDateTime.plus(duration);


            MessageManager.sendGlobalMessage(plugin, "The faction " + raiderFactionInfo.getFactionName() + " has declared a raid on " + factionInfo.getFactionName());
            MessageManager.sendGlobalMessage(plugin, "The raid will begin 15 minutes from now, if other factions want to join do /raid join [faction_name].");
            commandManager.getDataAccessManager().getRaidDao().addRaid(raiderFactionInfo.getFactionId(), factionInfo.getFactionId(), currentDateTime.plus(duration).toString(), currentDateTime.plus(duration_end).toString());

        }

    }


}
