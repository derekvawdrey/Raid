package me.amogus360.raid.CommandManager;

import me.amogus360.raid.Commands.Faction.CreateFactionCommand;
import me.amogus360.raid.Commands.Faction.DeleteFactionCommand;
import me.amogus360.raid.Commands.Faction.FactionInfoCommand;
import me.amogus360.raid.Commands.Faction.Invite.AcceptInvitationCommand;
import me.amogus360.raid.Commands.Faction.Invite.InviteFactionCommand;
import me.amogus360.raid.Commands.Faction.Invite.ListInvitesCommand;
import me.amogus360.raid.Commands.Faction.LeaveFactionCommand;
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
        registerSubCommand("land claim", new ClaimLandCommand(plugin, "/faction land claim"));
        registerSubCommand("land show", new ShowLandClaimsCommand(plugin, "/faction land show"));
        registerSubCommand("invite send", new InviteFactionCommand(plugin, "/faction invite send [player]"));
        registerSubCommand("invite accept", new AcceptInvitationCommand(plugin, "/faction invite accept [faction-name]"));
        registerSubCommand("invite show", new ListInvitesCommand(plugin, "/faction invite show"));
        registerSubCommand("leave", new LeaveFactionCommand(plugin, "/faction leave"));
        registerSubCommand("delete", new DeleteFactionCommand(plugin, "/faction delete"));
        registerSubCommand("setspawn", new UpdateSpawnLocationCommand(plugin, "/faction setspawn"));
        registerSubCommand("setbosslocation", new UpdateFactionBossLocationCommand(plugin, "/faction setbosslocation"));
        registerSubCommand("spawn", new TeleportSpawnCommand(plugin, "/faction spawn"));
    }

    @Override
    protected void registerSubCommand(String name, RaidCommand subCommand) {
        subCommands.put(name.toLowerCase(), subCommand);
    }

}
