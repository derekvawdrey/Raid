package me.amogus360.raid;

import me.amogus360.raid.DAO.FactionDao;
import me.amogus360.raid.DAO.LandClaimDao;
import me.amogus360.raid.DAO.PlayerAccountDao;

import java.sql.Connection;

public class DataAccessManager {

    private final FactionDao factionDao;
    private final PlayerAccountDao playerAccountDao;
    private final LandClaimDao landClaimDao;

    public DataAccessManager(Connection connection){
        this.factionDao = new FactionDao(connection);
        this.playerAccountDao = new PlayerAccountDao(connection);
        this.landClaimDao = new LandClaimDao(connection);
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
}
