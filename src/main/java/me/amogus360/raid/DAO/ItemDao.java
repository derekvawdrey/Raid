package me.amogus360.raid.DAO;

import me.amogus360.raid.Model.Items.Misc.TeleportRequest;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemDao {

    private final Map<String, TeleportRequest> teleportRequests = new HashMap<>();
    private final Connection connection;

    public ItemDao(Connection connection) {
        this.connection = connection;
    }

    public boolean putIntoTeleportRequests(String activator_name, TeleportRequest teleportRequest){
        teleportRequests.put(activator_name, teleportRequest);
        return true;
    }

    public TeleportRequest getTeleportRequest(String activator_name){
        return teleportRequests.get(activator_name);
    }

    public boolean playerExistsInTeleportRequests(String activator_name){
        return teleportRequests.containsKey(activator_name);
    }

    public boolean removeFromTeleportRequest(String activator_name){
        teleportRequests.remove(activator_name);
        return true;
    }

}
