package me.amogus360.raid.Model;

public class LandClaim {
    private int claimId;
    private int factionId;
    private String factionName;
    private LandClaimLocation location; // Use LandClaimLocation instead of Location

    public LandClaim(int claimId, int factionId, String factionName, LandClaimLocation location) {
        this.claimId = claimId;
        this.factionId = factionId;
        this.factionName = factionName;
        this.location = location;
    }

    public int getClaimId() {
        return this.claimId;
    }

    public int getFactionId() {
        return this.factionId;
    }

    public String getFactionString() {
        return this.factionName;
    }

    public LandClaimLocation getLocation() {
        return location;
    }
}
