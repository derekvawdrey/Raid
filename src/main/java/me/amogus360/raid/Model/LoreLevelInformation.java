package me.amogus360.raid.Model;

public class LoreLevelInformation {
    private String enhancementType;
    private int level;

    public LoreLevelInformation(String enhancementType, int level) {
        this.enhancementType = enhancementType;
        this.level = level;
    }

    public String getEnhancementType() {
        return enhancementType;
    }

    public int getLevel() {
        return level;
    }
}