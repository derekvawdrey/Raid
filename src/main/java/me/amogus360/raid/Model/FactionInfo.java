package me.amogus360.raid.Model;

import java.util.UUID;

public class FactionInfo {
    private int factionId;
    private String factionName;
    private int factionOwnerId;

    public FactionInfo(int factionId, String factionName, int factionOwnerId) {
        this.factionId = factionId;
        this.factionName = factionName;
        this.factionOwnerId = factionOwnerId;
    }

    public int getFactionId() {
        return factionId;
    }

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
