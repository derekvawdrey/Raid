package me.amogus360.raid.Model;

public class LandClaim {
    // In a 7x7 radius around the center block
    public static int size = 7;
    private int id;
    private int factionId;
    private int x;
    private int z;
    private String factionName;

    public LandClaim(int id, int factionId, String factionName, int x, int z) {
        this.id = id;
        this.factionId = factionId;
        this.x = x;
        this.z = z;
    }

    public int getId() {
        return id;
    }

    public int getFactionId() {
        return factionId;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "LandClaim{" +
                "id=" + id +
                ", factionId=" + factionId +
                ", x=" + x +
                ", z=" + z +
                '}';
    }
}
