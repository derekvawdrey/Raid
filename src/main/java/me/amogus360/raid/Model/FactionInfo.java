package me.amogus360.raid.Model;

import java.util.UUID;

public class FactionInfo {
    private int factionId;
    private String factionName;
    private int factionOwnerId;
    private int factionMoney;

    public FactionInfo(int factionId, String factionName, int factionOwnerId, int factionMoney) {
        this.factionId = factionId;
        this.factionName = factionName;
        this.factionOwnerId = factionOwnerId;
        this.factionMoney = factionMoney;
    }

    public int getFactionId() {
        return factionId;
    }
    public int getFactionMoney(){return factionMoney; }
    public String getFactionName() {
        return factionName;
    }

    public int factionOwnerId() {
        return factionOwnerId;
    }

    @Override
    public String toString() {
        return "FactionInfo{" +
                "factionId=" + factionId +
                ", factionName='" + factionName + '\'' +
                ", factionOwnerUUID=" + factionOwnerId +
                '}';
    }
}
