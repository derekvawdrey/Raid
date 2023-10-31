package me.amogus360.raid.Commands.Item;

import me.amogus360.raid.CommandManager.CommandManager;
import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.Model.Gui.ItemEnhanceGui;
import me.amogus360.raid.Model.Gui.RaidShopGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemEnhance extends RaidCommand {
    public ItemEnhance(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }
    @Override
    public void execute(CommandSender sender, String[] args, CommandManager commandManager) {
        if (!(sender instanceof Player)) {
            MessageManager.sendMessage(sender,"Only players can use this command.");
            return;
        }

        // Your logic for /raid money here
        Player player = (Player) sender;
        // Open up the raid shop gui
        new ItemEnhanceGui(player,commandManager.getDataAccessManager()).open();
    }


}
