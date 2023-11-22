package me.amogus360.raid.CommandManager;

import me.amogus360.raid.Commands.Faction.*;
import me.amogus360.raid.Commands.Faction.Invite.AcceptInvitationCommand;
import me.amogus360.raid.Commands.Faction.Invite.InviteFactionCommand;
import me.amogus360.raid.Commands.Faction.Invite.ListInvitesCommand;
import me.amogus360.raid.Commands.Faction.RaidBoss.UpdateFactionBossLocationCommand;
import me.amogus360.raid.Commands.Faction.Spawn.TeleportSpawnCommand;
import me.amogus360.raid.Commands.Faction.Spawn.UpdateSpawnLocationCommand;
import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DataAccessManager;
import org.bukkit.plugin.java.JavaPlugin;

public class InviteCommandManager extends CommandManager {


    public InviteCommandManager(JavaPlugin plugin, DataAccessManager dataAccessManager) {
        super(plugin, dataAccessManager, "invite"); // Call the superclass constructor
        registerSubCommands();
    }

    @Override
    protected void registerSubCommands() {
        registerSubCommand("send", new InviteFactionCommand(plugin, "/invite send [player]"));
        registerSubCommand("accept", new AcceptInvitationCommand(plugin, "/invite accept [faction_name]"));
        registerSubCommand("list", new ListInvitesCommand(plugin, "/invite list"));
    }

    @Override
    protected void registerSubCommand(String name, RaidCommand subCommand) {
        subCommands.put(name.toLowerCase(), subCommand);
    }

}
