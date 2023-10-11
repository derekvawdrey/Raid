package me.amogus360.raid.DAO;

import me.amogus360.raid.Model.FactionInfo;
import me.amogus360.raid.Model.RaidInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RaidDao {
    private Connection connection;
    private List<RaidInfo> onGoingRaids;

    public RaidDao(Connection connection) {
        this.connection = connection;
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
