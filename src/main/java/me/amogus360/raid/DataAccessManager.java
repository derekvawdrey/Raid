package me.amogus360.raid;

import me.amogus360.raid.DAO.*;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;

public class DataAccessManager {

    private final FactionDao factionDao;
    private final PlayerAccountDao playerAccountDao;
    private final LandClaimDao landClaimDao;
    private final BlockInfoDao blockInfoDao;
    private final BossBarDao bossBarDataAccess;

    private final RaidDao raidDao;

    private final ItemDao itemDao;

    private final JavaPlugin plugin;
    public DataAccessManager(Connection connection, JavaPlugin plugin){
        this.factionDao = new FactionDao(connection);
        this.playerAccountDao = new PlayerAccountDao(connection);
        this.landClaimDao = new LandClaimDao(connection);
        this.blockInfoDao = new BlockInfoDao(connection);
        this.bossBarDataAccess = new BossBarDao();
        this.itemDao = new ItemDao(connection);
        this.raidDao = new RaidDao(connection);
        this.plugin = plugin;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
    public FactionDao getFactionDao(){
        return factionDao;
    }
    public PlayerAccountDao getPlayerAccountDao(){
        return playerAccountDao;
    }

    public LandClaimDao getLandClaimDao(){
        return landClaimDao;
    }
    public ItemDao getItemDao(){ return itemDao;}
    public BlockInfoDao getBlockInfoDao(){ return blockInfoDao;}
    public BossBarDao getBossBarDataAccess(){return bossBarDataAccess;}
    public RaidDao getRaidDao() {
        return raidDao;
    }
}
