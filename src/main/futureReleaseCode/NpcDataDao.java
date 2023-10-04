package me.amogus360.raid.DAO;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NpcDataDao {
    private final Connection connection;

    public NpcDataDao(Connection connection) {
        this.connection = connection;
    }

    // Task 1: Return NPC IDs for a given faction_id
    public List<Integer> getNpcIdsForFaction(int factionId) {
        List<Integer> npcIds = new ArrayList<>();
        String query = "SELECT npc_id FROM npc_data WHERE faction_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, factionId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int npcId = resultSet.getInt("npc_id");
                npcIds.add(npcId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return npcIds;
    }

    // Task 2: Return faction_id when given an NPC id
    public int getFactionIdForNpc(int npcId) {
        int factionId = -1; // Default value if not found
        String query = "SELECT faction_id FROM npc_data WHERE npc_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, npcId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                factionId = resultSet.getInt("faction_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return factionId;
    }

    // Task 3: Return NPC ID when given a title and faction_id
    public int getNpcIdForTitleAndFaction(String npcTitle, int factionId) {
        int npcId = -1; // Default value if not found
        String query = "SELECT npc_id FROM npc_data WHERE npc_title = ? AND faction_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, npcTitle);
            preparedStatement.setInt(2, factionId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                npcId = resultSet.getInt("npc_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return npcId;
    }


    // Add an NPC to the database
    public boolean addNpc(int factionId, String npcName, int npcId, String npcTitle, Location location) {
        String insertQuery = "INSERT INTO npc_data (name, faction_id, npc_id, npc_title, x, y, z, world) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, npcName);
            preparedStatement.setInt(2, factionId);
            preparedStatement.setInt(3, npcId);
            preparedStatement.setString(4, npcTitle);

            // Extract location information
            preparedStatement.setDouble(5, location.getX());
            preparedStatement.setDouble(6, location.getY());
            preparedStatement.setDouble(7, location.getZ());
            preparedStatement.setString(8, location.getWorld().getName());

            int rowsAffected = preparedStatement.executeUpdate();

            // Check if the NPC was added successfully (1 row affected)
            return rowsAffected == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean updateNpcTitle(int npcId, String newTitle) {
        // Check if the new title is valid (either "Mayor" or "Support")
        if (!newTitle.equals("Mayor") && !newTitle.equals("Support")) {
            return false;
        }

        String updateQuery = "UPDATE npc_data SET title = ? WHERE npc_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, newTitle);
            preparedStatement.setInt(2, npcId);

            int rowsAffected = preparedStatement.executeUpdate();

            // Check if the title was updated successfully (1 row affected)
            return rowsAffected == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Function to update the name of an NPC by its npc_id
    public boolean updateNpcName(int npcId, String newName) {
        String updateQuery = "UPDATE npc_data SET name = ? WHERE npc_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, newName);
            preparedStatement.setInt(2, npcId);

            int rowsAffected = preparedStatement.executeUpdate();

            // Check if the NPC name was updated successfully (1 row affected)
            return rowsAffected == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Function to update the faction_id of an NPC by its npc_id
    public boolean updateNpcFaction(int npcId, int newFactionId) {
        String updateQuery = "UPDATE npc_data SET faction_id = ? WHERE npc_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setInt(1, newFactionId);
            preparedStatement.setInt(2, npcId);

            int rowsAffected = preparedStatement.executeUpdate();

            // Check if the faction_id was updated successfully (1 row affected)
            return rowsAffected == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateNpcCoordinates(int npcId, Location newLocation) {
        String updateQuery = "UPDATE npc_data SET x = ?, y = ?, z = ?, world = ? WHERE npc_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setDouble(1, newLocation.getX());
            preparedStatement.setDouble(2, newLocation.getY());
            preparedStatement.setDouble(3, newLocation.getZ());
            preparedStatement.setString(4, newLocation.getWorld().getName());
            preparedStatement.setInt(5, npcId);

            int rowsAffected = preparedStatement.executeUpdate();

            // Check if the coordinates were updated successfully (1 row affected)
            return rowsAffected == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
