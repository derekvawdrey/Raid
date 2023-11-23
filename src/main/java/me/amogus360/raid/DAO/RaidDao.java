package me.amogus360.raid.DAO;

import me.amogus360.raid.Model.FactionInfo;
import me.amogus360.raid.Model.RaidInfo;
import me.amogus360.raid.Utilities.LandClaimChunkUtilities;
import org.bukkit.Location;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;

public class RaidDao {
    private Connection connection;
    private List<RaidInfo> onGoingRaids;

    public RaidDao(Connection connection) {
        this.connection = connection;
    }

    public LocalDateTime getTimeToStartRaid(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        TemporalAmount duration = java.time.Duration.of(1, ChronoUnit.MINUTES);
        return currentDateTime.plus(duration);
    }
    public LocalDateTime getTimeToEndRaid(){
        LocalDateTime currentDateTime = getTimeToStartRaid();
        TemporalAmount duration = java.time.Duration.of(5, ChronoUnit.MINUTES);
        return currentDateTime.plus(duration);
    }
    public LocalDateTime getTimeToRebuildBlock(){
        return getTimeToStartRaid();
    }
    public Timestamp getTimestampToRebuildBlock(){
        LocalDateTime localDateTime = getTimeToRebuildBlock();
        Timestamp timestamp = Timestamp.valueOf(localDateTime);
        return timestamp;
    }


    public boolean isRaidOngoingForLocation(Location location) {
        int factionId = getFactionIdAtLocation(location);

        if (factionId != -1) {
            return isFactionInOngoingRaid(factionId);
        }

        return false;
    }

    private int getFactionIdAtLocation(Location location) {
        Location convertedChunkCoordinate = LandClaimChunkUtilities.convertToChunkCoordinate(location);
        int chunk_x = (int) convertedChunkCoordinate.getX();
        int chunk_z = (int) convertedChunkCoordinate.getZ();
        String querySQL = "SELECT faction_id FROM land_claims WHERE chunk_x = ? AND chunk_z = ? AND chunk_world = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setInt(1, chunk_x);
            preparedStatement.setInt(2, chunk_z);
            preparedStatement.setString(3, convertedChunkCoordinate.getWorld().getName());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("faction_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Return -1 if no faction ID is found for the location.
    }


    // Function to add a faction to the raid_participants table based on faction id and raid id.
    public void addParticipantToRaid(int raidId, int factionId) {
        String sql = "INSERT INTO raid_participants (raid_id, faction_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, raidId);
            statement.setInt(2, factionId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isFactionInOngoingRaid(int factionId) {
        String sql = "SELECT COUNT(*) FROM raids WHERE (defending_faction_id = ?) AND in_progress = 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, factionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // If count is greater than 0, the faction is involved in an ongoing raid.
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // If an error occurs or no ongoing raids are found for the faction, assume the faction is not in an ongoing raid.
    }

    // Function to add a raid to the raids table.
    public void addRaid(int attackingFactionId, int defendingFactionId, String startTime, String endTime) {
        String sql = "INSERT INTO raids (attacking_faction_id, defending_faction_id, start_time, end_time) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, attackingFactionId);
            statement.setInt(2, defendingFactionId);
            statement.setString(3, startTime);
            statement.setString(4, endTime);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void startRaid(int raidId) {
        try {
            String updateSql = "UPDATE raids SET in_progress = 1 WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
                preparedStatement.setInt(1, raidId);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Raid with ID " + raidId + " has been started.");
                } else {
                    System.out.println("Raid with ID " + raidId + " not found or could not be started.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<RaidInfo> getTimeToStartRaids(LocalDateTime currentTime) {
        List<RaidInfo> ongoingRaids = new ArrayList<>();
        String sql = "SELECT raids.*, faction.name AS defending_faction_name " +
                "FROM raids " +
                "INNER JOIN faction ON raids.defending_faction_id = faction.id " +
                "WHERE start_time <= ? AND end_time >= ? AND in_progress = 0";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, currentTime.toString());
            statement.setString(2, currentTime.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int raidId = resultSet.getInt("id");
                    int attackingFactionId = resultSet.getInt("attacking_faction_id");
                    int defendingFactionId = resultSet.getInt("defending_faction_id");
                    String startTime = resultSet.getString("start_time");
                    String endTime = resultSet.getString("end_time");
                    String defendingFactionName = resultSet.getString("defending_faction_name");

                    RaidInfo raidInfo = new RaidInfo(raidId, attackingFactionId, defendingFactionId, defendingFactionName, startTime, endTime);
                    ongoingRaids.add(raidInfo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ongoingRaids;
    }

    public boolean isRaidDeclared(int targetFactionId) {
        String sql = "SELECT COUNT(*) FROM raids WHERE defending_faction_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, targetFactionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // If count is greater than 0, a raid has been declared against the target faction.
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // If an error occurs or no raids are found, assume no raid has been declared.
    }

    public List<RaidInfo> getUpcomingRaids() {
        List<RaidInfo> upcomingRaids = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();

        String sql = "SELECT raids.*, faction.name AS defending_faction_name " +
                "FROM raids " +
                "INNER JOIN faction ON raids.defending_faction_id = faction.id " +
                "WHERE start_time > ? AND in_progress = 0";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, currentTime.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int raidId = resultSet.getInt("id");
                    int attackingFactionId = resultSet.getInt("attacking_faction_id");
                    int defendingFactionId = resultSet.getInt("defending_faction_id");
                    String startTime = resultSet.getString("start_time");
                    String endTime = resultSet.getString("end_time");
                    String defendingFactionName = resultSet.getString("defending_faction_name");

                    RaidInfo raidInfo = new RaidInfo(raidId, attackingFactionId, defendingFactionId, defendingFactionName, startTime, endTime);
                    upcomingRaids.add(raidInfo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return upcomingRaids;
    }



    public List<RaidInfo> getOngoingRaids(LocalDateTime currentTime) {
        List<RaidInfo> ongoingRaids = new ArrayList<>();
        String sql = "SELECT raids.*, faction.name AS defending_faction_name " +
                "FROM raids " +
                "INNER JOIN faction ON raids.defending_faction_id = faction.id " +
                "WHERE start_time <= ? AND end_time >= ? AND in_progress = 1";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, currentTime.toString());
            statement.setString(2, currentTime.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int raidId = resultSet.getInt("id");
                    int attackingFactionId = resultSet.getInt("attacking_faction_id");
                    int defendingFactionId = resultSet.getInt("defending_faction_id");
                    String startTime = resultSet.getString("start_time");
                    String endTime = resultSet.getString("end_time");
                    String defendingFactionName = resultSet.getString("defending_faction_name");

                    RaidInfo raidInfo = new RaidInfo(raidId, attackingFactionId, defendingFactionId, defendingFactionName, startTime, endTime);
                    ongoingRaids.add(raidInfo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ongoingRaids;
    }

    public List<RaidInfo> getEndedRaids(LocalDateTime currentTime) {
        List<RaidInfo> endedRaids = new ArrayList<>();
        String sql = "SELECT raids.*, faction.name AS defending_faction_name " +
                "FROM raids " +
                "INNER JOIN faction ON raids.defending_faction_id = faction.id " +
                "WHERE end_time <= ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, currentTime.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int raidId = resultSet.getInt("id");
                    int attackingFactionId = resultSet.getInt("attacking_faction_id");
                    int defendingFactionId = resultSet.getInt("defending_faction_id");
                    String startTime = resultSet.getString("start_time");
                    String endTime = resultSet.getString("end_time");
                    String defendingFactionName = resultSet.getString("defending_faction_name");

                    RaidInfo raidInfo = new RaidInfo(raidId, attackingFactionId, defendingFactionId, defendingFactionName, startTime, endTime);
                    endedRaids.add(raidInfo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return endedRaids;
    }

    public void updateBossKilled(int defendingFactionId, boolean bossKilledValue) {
        String updateQuery = "UPDATE raids SET boss_killed = ? WHERE defending_faction_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            int bossValue = bossKilledValue ? 1 : 0;
            preparedStatement.setInt(1, bossValue);
            preparedStatement.setInt(2, defendingFactionId);
            int rowsUpdated = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isRaidBossDead(int raidId) {
        try {
            String query = "SELECT boss_killed FROM raids WHERE id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, raidId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int bossKilled = resultSet.getInt("boss_killed");
                        return bossKilled == 1;
                    } else {
                        // Raid with the specified ID not found
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately
            return false;
        }
    }



    public void stopRaid(int raidId) {
        // First, delete the raid entry from the raids table.
        String deleteRaidSQL = "DELETE FROM raids WHERE id = ?";
        try (PreparedStatement raidStatement = connection.prepareStatement(deleteRaidSQL)) {
            raidStatement.setInt(1, raidId);
            raidStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Now remove all raid participants associated with the raid.
        String deleteParticipantsSQL = "DELETE FROM raid_participants WHERE raid_id = ?";
        try (PreparedStatement participantsStatement = connection.prepareStatement(deleteParticipantsSQL)) {
            participantsStatement.setInt(1, raidId);
            participantsStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isFactionParticipatingInRaid(int raidId, int factionId) {
        String sql = "SELECT COUNT(*) FROM raid_participants WHERE raid_id = ? AND faction_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, raidId);
            statement.setInt(2, factionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // If count is greater than 0, the faction is participating in the raid.
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // If an error occurs or no rows are found, assume the faction is not participating.
    }

    public RaidInfo getRaidByDefendingFactionId(int defendingFactionId) {
        String sql = "SELECT raids.*, faction.name AS defending_faction_name " +
                "FROM raids " +
                "INNER JOIN faction ON raids.defending_faction_id = faction.id " +
                "WHERE defending_faction_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, defendingFactionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int raidId = resultSet.getInt("id");
                    int attackingFactionId = resultSet.getInt("attacking_faction_id");
                    String startTime = resultSet.getString("start_time");
                    String endTime = resultSet.getString("end_time");
                    String defendingFactionName = resultSet.getString("defending_faction_name");

                    // Create a RaidInfo object with the defending faction name.
                    RaidInfo raidInfo = new RaidInfo(raidId, attackingFactionId, defendingFactionId, defendingFactionName, startTime, endTime);
                    return raidInfo;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Return null if no raid is found for the defendingFactionId.
    }






}
