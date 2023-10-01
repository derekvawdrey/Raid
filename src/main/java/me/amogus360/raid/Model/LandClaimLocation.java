package me.amogus360.raid.Model;

public class LandClaimLocation {
    private String worldName;
    private int x;
    private int y;
    private int z;

    public LandClaimLocation(int x, int y, int z, String worldName) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
    }

    // Getter for worldName
    public String getWorldName() {
        return worldName;
    }

    // Getter for x
    public int getX() {
        return x;
    }

    // Getter for y
    public int getY() {
        return y;
    }

    // Getter for z
    public int getZ() {
        return z;
    }
}
