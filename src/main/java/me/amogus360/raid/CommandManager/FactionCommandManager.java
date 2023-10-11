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
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class FactionCommandManager extends CommandManager {


    public FactionCommandManager(JavaPlugin plugin, DataAccessManager dataAccessManager) {
        super(plugin, dataAccessManager);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Display a help message or handle it as needed
            MessageManager.sendMessage(sender,"Usage: /faction <subcommand>");
            return true;
        }

        // Determine the subcommand and delegate to the appropriate class
        String subCommand = args[0].toLowerCase();
        // Remove the first element (subcommand) from the args array
        if(subCommand.equals("create")) new CreateFactionCommand(plugin,"/faction create [faction_name]").execute(sender,removeOneArg(args),this);
        else if(subCommand.equals("info")) new FactionInfoCommand(plugin,"/faction info").execute(sender,removeOneArg(args),this);
        else if(subCommand.equals("land")){
            String[] landArgs = removeOneArg(args);
            if(landArgs[0].equals("claim")) new ClaimLandCommand(plugin, "/faction land claim").execute(sender,removeOneArg(landArgs),this);
            else if(landArgs[0].equals("show")) new ShowLandClaimsCommand(plugin, "/faction land show").execute(sender,removeOneArg(landArgs),this);
        }else if(subCommand.equals("invite")){
            String[] inviteArgs = removeOneArg(args);
            if(inviteArgs[0].equals("send")) new InviteFactionCommand(plugin, "/faction invite send [player]").execute(sender,removeOneArg(inviteArgs),this);
            else if(inviteArgs[0].equals("accept")) new AcceptInvitationCommand(plugin, "/faction invite accept [faction-name]").execute(sender,removeOneArg(inviteArgs),this);
            else if(inviteArgs[0].equals("show")) new ListInvitesCommand(plugin, "/faction invite show").execute(sender,removeOneArg(inviteArgs),this);
        }else if(subCommand.equals("leave")){
             new LeaveFactionCommand(plugin, "/faction leave").execute(sender,removeOneArg(args),this);
        }else if(subCommand.equals("delete")){
            new DeleteFactionCommand(plugin, "/faction delete").execute(sender,removeOneArg(args),this);
        }else if(subCommand.equals("setspawn")){
            new UpdateSpawnLocationCommand(plugin, "/faction setspawn").execute(sender,removeOneArg(args),this);
        }else if(subCommand.equals("setbosslocation")){
            new UpdateFactionBossLocationCommand(plugin, "/faction setbosslocation").execute(sender,removeOneArg(args),this);
        }else if(subCommand.equals("spawn")){
            new TeleportSpawnCommand(plugin, "/faction spawn").execute(sender, removeOneArg(args),this);
        }
        else {
            // Unknown subcommand
            MessageManager.sendMessage(sender,"Unknown subcommand: " + subCommand);
        }


        return true;
    }

}
