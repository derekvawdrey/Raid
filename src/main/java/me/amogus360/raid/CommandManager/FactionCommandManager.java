package me.amogus360.raid.CommandManager;

import me.amogus360.raid.Commands.Faction.*;
import me.amogus360.raid.Commands.Faction.Invite.AcceptInvitationCommand;
import me.amogus360.raid.Commands.Faction.Invite.InviteFactionCommand;
import me.amogus360.raid.Commands.Faction.Invite.ListInvitesCommand;
import me.amogus360.raid.Commands.Faction.RaidBoss.UpdateFactionBossLocationCommand;
import me.amogus360.raid.Commands.Faction.Spawn.TeleportSpawnCommand;
import me.amogus360.raid.Commands.Faction.Spawn.UpdateSpawnLocationCommand;
import me.amogus360.raid.Commands.LandClaim.ClaimLandCommand;
import me.amogus360.raid.Commands.LandClaim.ShowLandClaimsCommand;
import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class FactionCommandManager extends CommandManager {


    public FactionCommandManager(JavaPlugin plugin, DataAccessManager dataAccessManager) {
        super(plugin, dataAccessManager, "faction"); // Call the superclass constructor
        registerSubCommands();
    }

    @Override
    protected void registerSubCommands() {
        // Register your subcommands here
        registerSubCommand("create", new CreateFactionCommand(plugin, "/faction create [faction_name]"));
        registerSubCommand("info", new FactionInfoCommand(plugin, "/faction info"));
        registerSubCommand("leave", new LeaveFactionCommand(plugin, "/faction leave"));
        registerSubCommand("delete", new DeleteFactionCommand(plugin, "/faction delete"));
        registerSubCommand("setspawn", new UpdateSpawnLocationCommand(plugin, "/faction setspawn"));
        registerSubCommand("setbosslocation", new UpdateFactionBossLocationCommand(plugin, "/faction setbosslocation"));
        registerSubCommand("spawn", new TeleportSpawnCommand(plugin, "/faction spawn"));
        registerSubCommand("deposit", new DepositFactionCommand(plugin, "/faction deposit [amount]"));
    }

    @Override
    protected void registerSubCommand(String name, RaidCommand subCommand) {
        subCommands.put(name.toLowerCase(), subCommand);
    }

}
