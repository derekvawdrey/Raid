package me.amogus360.raid.Model;

public class RaidInfo {
    private int raidId;
    private int attackingFactionId;
    private int defendingFactionId;
    private String startTime;
    private String endTime;
    private String defendingFactionName;

    public RaidInfo(int raidId, int attackingFactionId, int defendingFactionId, String defendingFactionName, String startTime, String endTime) {
        this.raidId = raidId;
        this.attackingFactionId = attackingFactionId;
        this.defendingFactionId = defendingFactionId;
        this.defendingFactionName = defendingFactionName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getRaidId() {
        return raidId;
    }

    public int getAttackingFactionId() {
        return attackingFactionId;
    }

    public int getDefendingFactionId() {
        return defendingFactionId;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getDefendingFactionName(){
        return defendingFactionName;
    }

    @Override
    public String toString() {
        return "RaidInfo{" +
                "raidId=" + raidId +
                ", attackingFactionId=" + attackingFactionId +
                ", defendingFactionId=" + defendingFactionId +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
