package me.amogus360.raid.Model;

import java.util.UUID;

public class FactionInfo {
    private int factionId;
    private String factionName;
    private UUID factionOwnerUUID;

    public FactionInfo(int factionId, String factionName, UUID factionOwnerUUID) {
        this.factionId = factionId;
        this.factionName = factionName;
        this.factionOwnerUUID = factionOwnerUUID;
    }

    public int getFactionId() {
        return factionId;
    }

    public String getFactionName() {
        return factionName;
    }

    public UUID getFactionOwnerUUID() {
        return factionOwnerUUID;
    }

    @Override
    public String toString() {
        return "FactionInfo{" +
                "factionId=" + factionId +
                ", factionName='" + factionName + '\'' +
                ", factionOwnerUUID=" + factionOwnerUUID +
                '}';
    }
}
