package me.amogus360.raid;

import me.amogus360.raid.DAO.*;

import java.sql.Connection;

public class DataAccessManager {

    private final FactionDao factionDao;
    private final PlayerAccountDao playerAccountDao;
    private final LandClaimDao landClaimDao;
    private final BlockInfoDao blockInfoDao;
    private final BossBarDataAccess bossBarDataAccess;
    public DataAccessManager(Connection connection){
        this.factionDao = new FactionDao(connection);
        this.playerAccountDao = new PlayerAccountDao(connection);
        this.landClaimDao = new LandClaimDao(connection);
        this.blockInfoDao = new BlockInfoDao(connection);

        this.bossBarDataAccess = new BossBarDataAccess();
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

    public BlockInfoDao getBlockInfoDao(){ return blockInfoDao;}
    public BossBarDataAccess getBossBarDataAccess(){return bossBarDataAccess;}
}
