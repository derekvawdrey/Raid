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
import org.bukkit.plugin.java.JavaPlugin;

public class LandCommandManager extends CommandManager {


    public LandCommandManager(JavaPlugin plugin, DataAccessManager dataAccessManager) {
        super(plugin, dataAccessManager, "land"); // Call the superclass constructor
        registerSubCommands();
    }

    @Override
    protected void registerSubCommands() {
        // Register your subcommands here
        registerSubCommand("claim", new ClaimLandCommand(plugin, "/land claim"));
        registerSubCommand("show", new ShowLandClaimsCommand(plugin, "/land show"));
    }

    @Override
    protected void registerSubCommand(String name, RaidCommand subCommand) {
        subCommands.put(name.toLowerCase(), subCommand);
    }

}
