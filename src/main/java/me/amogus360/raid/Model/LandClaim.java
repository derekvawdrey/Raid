package me.amogus360.raid.Model;

public class LandClaim {
    private int id;
    private int factionId;
    private int x;
    private int z;

    public LandClaim(int id, int factionId, int x, int z) {
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
