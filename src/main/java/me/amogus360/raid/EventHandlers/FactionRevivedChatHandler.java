package me.amogus360.raid.EventHandlers;

import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.Model.FactionInfo;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class FactionRevivedChatHandler implements Listener {

    private final DataAccessManager dataAccessManager;
    public FactionRevivedChatHandler(DataAccessManager dataAccessManager) {
        this.dataAccessManager = dataAccessManager;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String playerName = event.getPlayer().getName();
        String originalMessage = event.getMessage();
        FactionInfo factionInfo = dataAccessManager.getFactionDao().getFactionInfoByPlayerUUID(event.getPlayer().getUniqueId());
        String factionName = "";
        String modifiedFormat = MessageManager.getPluginPrefixColors("- Nomad -") + " " + playerName + ": "+ event.getMessage();
        if(factionInfo != null) {
            factionName = factionInfo.getFactionName();
            modifiedFormat = MessageManager.getPluginPrefixColors(factionInfo.getFactionName()) + " " + playerName + ": " + event.getMessage();
        }

        event.setFormat(modifiedFormat);
    }


}
