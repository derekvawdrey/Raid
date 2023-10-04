package me.amogus360.raid.Utilities;

import me.amogus360.raid.DataAccessManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import javax.xml.crypto.Data;

public class NpcUtilities {

    public static void createMayor(DataAccessManager dataAccessManager, Location location, int faction_id, String name) {

        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
        npc.spawn(location);
        dataAccessManager.getNpcDataDao().addNpc(faction_id, name, npc.getId(), "Mayor", location);

    }

}
