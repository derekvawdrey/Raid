package me.amogus360.raid;

import me.amogus360.raid.Commands.Faction.*;
import me.amogus360.raid.Commands.Faction.Claim.ClaimLandCommand;
import me.amogus360.raid.Commands.Faction.Claim.ShowLandClaimsCommand;
import me.amogus360.raid.Commands.MobTest;
import me.amogus360.raid.Commands.Money.AddMoneyCommand;
import me.amogus360.raid.Commands.Money.MoneyCommand;
import me.amogus360.raid.Commands.Money.SendMoneyCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.plugin.java.JavaPlugin;

public class RaidCommandManager implements CommandExecutor {

    private final JavaPlugin plugin;
    private final DataAccessManager dataAccessManager;

    public RaidCommandManager(JavaPlugin plugin, DataAccessManager dataAccessManager) {
        this.plugin = plugin;
        this.dataAccessManager = dataAccessManager;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Display a help message or handle it as needed
            MessageManager.sendMessage(sender,"Usage: /raid <subcommand>");
            return true;
        }

        // Determine the subcommand and delegate to the appropriate class
        String subcommand = args[0].toLowerCase();
        // Remove the first element (subcommand) from the args array
        String[] newArgs = removeOneArg(args);
        if(subcommand.equals("mob")){
            new MobTest(plugin, "/raid mob").execute(sender, newArgs, this);
        }
        else if (subcommand.equals("money")) {
            // Handle /raid money
            if(newArgs.length < 1) new MoneyCommand(plugin, "/raid money").execute(sender, newArgs,this);
            else if(newArgs[0].equals("add")) new AddMoneyCommand(plugin, "/raid money add [player_name] [amount]").execute(sender, removeOneArg(newArgs), this);
            else if(newArgs[0].equals("send")) new SendMoneyCommand(plugin, "/raid money send [player_name] [amount]").execute(sender, removeOneArg(newArgs),this);

        }else if(subcommand.equals("faction")){
            if(newArgs.length > 0){
                if(newArgs[0].equals("create")) new CreateFactionCommand(plugin,"/raid faction create [faction_name]").execute(sender,removeOneArg(newArgs),this);
                else if(newArgs[0].equals("info")) new FactionInfoCommand(plugin,"/raid faction info").execute(sender,removeOneArg(newArgs),this);
                else if(newArgs[0].equals("land")){
                    String[] landArgs = removeOneArg(newArgs);
                    if(landArgs[0].equals("claim")) new ClaimLandCommand(plugin, "/raid faction land claim").execute(sender,removeOneArg(newArgs),this);
                    else if(landArgs[0].equals("show")) new ShowLandClaimsCommand(plugin, "/raid faction show").execute(sender,removeOneArg(newArgs),this);
                }else if(newArgs[0].equals("invites")){
                    String[] inviteArgs = removeOneArg(newArgs);
                    if(inviteArgs[0].equals("send")) new InviteFactionCommand(plugin, "/raid faction invites send [player]").execute(sender,removeOneArg(inviteArgs),this);
                    else if(inviteArgs[0].equals("accept")) new AcceptInvitationCommand(plugin, "/raid faction invites accept [faction-name]").execute(sender,removeOneArg(inviteArgs),this);
                    else if(inviteArgs[0].equals("show")) new ListInvitesCommand(plugin, "/raid faction invite show").execute(sender,removeOneArg(inviteArgs),this);
                }else if(newArgs[0].equals("leave")){
                     new LeaveFactionCommand(plugin, "/raid faction leave").execute(sender,removeOneArg(newArgs),this);
                }
            }else{
                // Send a help message containing all usages
            }
        }
        else {
            // Unknown subcommand
            MessageManager.sendMessage(sender,"Unknown subcommand: " + subcommand);
        }

        return true;
    }

    private String[] removeOneArg(String[] args){
        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, args.length - 1);
        return newArgs;
    }

    public DataAccessManager getDataAccessManager(){
        return this.dataAccessManager;
    }
}
