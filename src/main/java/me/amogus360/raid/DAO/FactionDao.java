package me.amogus360.raid.DAO;
import me.amogus360.raid.Model.FactionInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FactionDao {

    private final Connection connection;

    public FactionDao(Connection connection) {
        this.connection = connection;
    }

    public int createFaction(UUID ownerUUID, String factionName, Location location) {
        String insertFactionSQL = "INSERT INTO faction (name, owner_id, spawn_x, spawn_y, spawn_z, spawn_world, raid_boss_x, raid_boss_y, raid_boss_z, raid_boss_world) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertFactionSQL, Statement.RETURN_GENERATED_KEYS)) {
            // Set the faction name
            preparedStatement.setString(1, factionName);

            // Set the owner's UUID
            PlayerAccountDao playerAccountDao = new PlayerAccountDao(this.connection);
            preparedStatement.setInt(2, playerAccountDao.getPlayerIdByUUID(ownerUUID));

            preparedStatement.setDouble(3, location.getX());
            preparedStatement.setDouble(4, location.getY());
            preparedStatement.setDouble(5, location.getZ());
            preparedStatement.setString(6, location.getWorld().getName());
            preparedStatement.setDouble(7, location.getX());
            preparedStatement.setDouble(8, location.getY());
            preparedStatement.setDouble(9, location.getZ());
            preparedStatement.setString(10, location.getWorld().getName());
            // Execute the SQL statement to insert the new faction and retrieve the generated keys
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating faction failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the generated faction ID
                } else {
                    throw new SQLException("Creating faction failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void updateSpawnLocation(int factionId, Location location) {
        String updateSpawnLocationSQL = "UPDATE faction SET spawn_x = ?, spawn_y = ?, spawn_z = ?, spawn_world = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSpawnLocationSQL)) {
            preparedStatement.setDouble(1, location.getX());
            preparedStatement.setDouble(2, location.getY());
            preparedStatement.setDouble(3, location.getZ());
            preparedStatement.setString(4, location.getWorld().getName());
            preparedStatement.setInt(5, factionId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRaidBossLocation(int factionId, Location location) {
        String updateRaidBossLocationSQL = "UPDATE faction SET raid_boss_x = ?, raid_boss_y = ?, raid_boss_z = ?, raid_boss_world = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateRaidBossLocationSQL)) {
            preparedStatement.setDouble(1, location.getX());
            preparedStatement.setDouble(2, location.getY());
            preparedStatement.setDouble(3, location.getZ());
            preparedStatement.setString(4, location.getWorld().getName());
            preparedStatement.setInt(5, factionId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Location getSpawnLocation(int factionId){
        String updateRaidBossLocationSQL = "SELECT spawn_x, spawn_y, spawn_z, spawn_world FROM faction WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateRaidBossLocationSQL)) {
            preparedStatement.setInt(1, factionId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Check if there's at least one result and the faction_id is not null
                if(resultSet.next()){
                    double x = resultSet.getDouble("spawn_x");
                    double y =resultSet.getDouble("spawn_y");
                    double z =resultSet.getDouble("spawn_z");
                    String world = resultSet.getString("spawn_world");

                    return new Location(Bukkit.getWorld(world),x,y,z);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Location getRaidBossLocation(int factionId){
        String updateRaidBossLocationSQL = "SELECT raid_boss_x, raid_boss_y, raid_boss_z, raid_boss_world FROM faction WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateRaidBossLocationSQL)) {
            preparedStatement.setInt(1, factionId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Check if there's at least one result and the faction_id is not null
                if(resultSet.next()){
                    double x = resultSet.getDouble("raid_boss_x");
                    double y =resultSet.getDouble("raid_boss_y");
                    double z =resultSet.getDouble("raid_boss_z");
                    String world = resultSet.getString("raid_boss_world");

                    return new Location(Bukkit.getWorld(world),x,y,z);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    public boolean isPlayerInFaction(UUID playerUUID) {
        String checkPlayerFactionSQL = "SELECT faction_id FROM player_data WHERE player_uuid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(checkPlayerFactionSQL)) {
            preparedStatement.setString(1, playerUUID.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Check if there's at least one result and the faction_id is not null
                return resultSet.next() && resultSet.getObject("faction_id") != null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return false by default if there's an error or the player is not in a faction
        return false;
    }


    public boolean isPlayerInFaction(UUID playerUUID, int factionId) {
        String checkMembershipSQL = "SELECT COUNT(*) AS member_count FROM player_data WHERE player_uuid = ? AND faction_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(checkMembershipSQL)) {
            preparedStatement.setString(1, playerUUID.toString());
            preparedStatement.setInt(2, factionId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int memberCount = resultSet.getInt("member_count");
                    return memberCount > 0; // Player is a member if member_count is greater than 0
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return false if there's an error or the player is not a member of the faction
        return false;
    }


    public void deleteFaction(UUID factionOwnerUUID) {
        // Check if the player is the owner of the faction
        if (!isPlayerFactionOwner(factionOwnerUUID)) {
            // The player is not the owner, so they can't delete the faction
            return;
        }

        // Get the faction ID of the owner's faction
        int factionId = getFactionIdByOwner(factionOwnerUUID);

        // Delete all members from the faction
        deleteMembersFromFaction(factionId);

        // Delete the faction itself
        deleteFactionById(factionId);
    }

    public int getFactionIdByPlayerUUID(UUID playerUUID) {
        String querySQL = "SELECT faction_id FROM player_data WHERE player_uuid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setString(1, playerUUID.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("faction_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Return -1 if no faction ID is found for the player UUID
    }


    public boolean isPlayerFactionOwner(UUID playerUUID) {
        // First, get the player's ID based on their UUID from your player_data table
        String getPlayerIdSQL = "SELECT id FROM player_data WHERE player_uuid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(getPlayerIdSQL)) {
            preparedStatement.setString(1, playerUUID.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int playerId = resultSet.getInt("id");
                    // Now check if the player is the owner of any faction
                    String checkOwnershipSQL = "SELECT id FROM faction WHERE owner_id = ?";

                    try (PreparedStatement ownershipStatement = connection.prepareStatement(checkOwnershipSQL)) {
                        ownershipStatement.setInt(1, playerId);

                        try (ResultSet ownershipResultSet = ownershipStatement.executeQuery()) {
                            return ownershipResultSet.next(); // If the player is the owner of a faction, there will be a result
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return false by default if there's an error or the player is not an owner
        return false;
    }


    private int getFactionIdByOwner(UUID ownerUUID) {
        // First, get the owner's ID based on their UUID from your player_data table
        String getOwnerIdSQL = "SELECT id FROM player_data WHERE player_uuid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(getOwnerIdSQL)) {
            preparedStatement.setString(1, ownerUUID.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int ownerId = resultSet.getInt("id");
                    // Now, get the faction ID associated with the owner's ID
                    String getFactionIdSQL = "SELECT id FROM faction WHERE owner_id = ?";

                    try (PreparedStatement factionIdStatement = connection.prepareStatement(getFactionIdSQL)) {
                        factionIdStatement.setInt(1, ownerId);

                        try (ResultSet factionIdResultSet = factionIdStatement.executeQuery()) {
                            if (factionIdResultSet.next()) {
                                return factionIdResultSet.getInt("id");
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return -1 by default if there's an error or the faction is not found
        return -1;
    }


    private void deleteMembersFromFaction(int factionId) {
        // Set faction_id and title_id to null for all members in the faction
        String updateMembersSQL = "UPDATE player_data SET faction_id = NULL, faction_title = NULL WHERE faction_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateMembersSQL)) {
            preparedStatement.setInt(1, factionId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteFactionById(int factionId) {
        // Delete the faction itself
        String deleteFactionSQL = "DELETE FROM faction WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteFactionSQL)) {
            preparedStatement.setInt(1, factionId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removePlayerFromFaction(UUID playerUUID) {
        // Set faction_id and title_id to NULL for the player in player_data
        String removePlayerSQL = "UPDATE player_data SET faction_id = NULL, faction_title = NULL WHERE player_uuid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(removePlayerSQL)) {
            preparedStatement.setString(1, playerUUID.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void depositMoneyToFaction(UUID playerUUID, String factionName, int amount) {
        // Check if the player is in the faction
        if (!isPlayerInFaction(playerUUID)) {
            // Handle the case where the player is not in the faction (e.g., send an error message)
            // You might want to add appropriate error handling here
            return;
        }

        // Get the faction ID by name
        int factionId = getFactionIdByName(factionName);

        // Check if the faction exists (factionId != -1)
        if (factionId != -1) {
            // Update the faction's money in the faction table
            String updateFactionMoneySQL = "UPDATE faction SET money = money + ? WHERE id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateFactionMoneySQL)) {
                preparedStatement.setInt(1, amount);
                preparedStatement.setInt(2, factionId);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Update the player's money in the money_data table
            // You should have a getPlayerIdByUUID method to retrieve the player's ID
            int playerId = new PlayerAccountDao(this.connection).getPlayerIdByUUID(playerUUID);

            // Check if playerId is valid (-1 indicates player not found)
            if (playerId != -1) {
                String updatePlayerMoneySQL = "UPDATE money_data SET money = money - ? WHERE player_id = ?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(updatePlayerMoneySQL)) {
                    preparedStatement.setInt(1, amount);
                    preparedStatement.setInt(2, playerId);
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                // Handle the case where the player ID is not found (e.g., send an error message)
                // You might want to add appropriate error handling here
            }
        } else {
            // Handle the case where the faction name is not found (e.g., send an error message)
            // You might want to add appropriate error handling here
        }
    }


    public boolean isFactionNameTaken(String factionName) {
        String checkFactionNameSQL = "SELECT COUNT(*) AS name_count FROM faction WHERE name = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(checkFactionNameSQL)) {
            preparedStatement.setString(1, factionName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int nameCount = resultSet.getInt("name_count");
                    return nameCount > 0; // Faction name is taken if name_count is greater than 0
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return false by default if there's an error or the faction name is not taken
        return false;
    }

    public boolean hasFactionTitle(UUID player_uuid, String title) {
        try {
            String sql = "SELECT * FROM player_data WHERE player_uuid = ? AND faction_title = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, player_uuid.toString());
                preparedStatement.setString(2, title);
                ResultSet resultSet = preparedStatement.executeQuery();
                return resultSet.next(); // Returns true if the player has the specified faction title
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if there is an error or the player doesn't have the title
    }

    public List<UUID> getAllPlayersWithFactionTitle(String title) {
        List<UUID> playersWithFactionTitle = new ArrayList<>();
        try {
            String sql = "SELECT player_uuid FROM player_data WHERE faction_title = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, title);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    playersWithFactionTitle.add(UUID.fromString(resultSet.getString("player_uuid")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playersWithFactionTitle;
    }

    public void setFactionTitle(UUID player_uuid, String title) {
        try {
            String sql = "UPDATE player_data SET faction_title = ? WHERE player_uuid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, player_uuid.toString());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void invitePlayerToJoinFaction(UUID inviterUUID, UUID inviteeUUID, int factionId) {
        String invitePlayerSQL = "INSERT INTO faction_invites (inviter_uuid, invitee_uuid, faction_id) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(invitePlayerSQL)) {
            preparedStatement.setString(1, inviterUUID.toString());
            preparedStatement.setString(2, inviteeUUID.toString());
            preparedStatement.setInt(3, factionId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void rescindAllInvites(UUID inviteeUUID) {
        String rescindInviteSQL = "DELETE FROM faction_invites WHERE invitee_uuid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(rescindInviteSQL)) {
            preparedStatement.setString(1, inviteeUUID.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void rescindInvite(int inviteId) {
        // Remove the invite with the specified ID
        String rescindInviteSQL = "DELETE FROM faction_invites WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(rescindInviteSQL)) {
            preparedStatement.setInt(1, inviteId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getPendingInvitations(String playerUUID) {
        List<String> invites = new ArrayList<>();

        // SQL query to retrieve pending invitations for the player including faction name
        String sql = "SELECT inviter_uuid, faction.name AS faction_name FROM faction_invites " +
                "INNER JOIN faction ON faction_invites.faction_id = faction.id " +
                "WHERE invitee_uuid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, playerUUID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String inviterUUID = resultSet.getString("inviter_uuid");
                    String factionName = resultSet.getString("faction_name");

                    // Format the invitation message as needed
                    String invitationMessage = "Invited by: " + inviterUUID + " to faction: " + factionName;
                    invites.add(invitationMessage);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return invites;
    }



    public void acceptInvite(UUID inviteeUUID, int factionId) {
        // Check if the invite exists
        String checkInviteSQL = "SELECT id FROM faction_invites WHERE invitee_uuid = ? AND faction_id = ?";
        int inviteId = -1; // Initialize inviteId with a default value

        try (PreparedStatement preparedStatement = connection.prepareStatement(checkInviteSQL)) {
            preparedStatement.setString(1, inviteeUUID.toString());
            preparedStatement.setInt(2, factionId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // The invite exists; get its ID
                    inviteId = resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // If an invite was found, remove it and add the player to the faction
        if (inviteId != -1) {
            rescindAllInvites(inviteeUUID); // Pass the inviteId to the rescindInvite function
            addToFaction(inviteeUUID, factionId); // Implement addToFaction as needed
        }
    }

    public boolean hasPendingInvitation(UUID inviteeUUID, int factionId) {
        String query = "SELECT COUNT(*) FROM faction_invites WHERE invitee_uuid = ? AND faction_id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, inviteeUUID.toString());
            statement.setInt(2, factionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addToFaction(UUID playerUUID, int factionId) {
        // Set the player's faction_id in player_data to the specified factionId
        String addToFactionSQL = "UPDATE player_data SET faction_id = ? WHERE player_uuid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(addToFactionSQL)) {
            preparedStatement.setInt(1, factionId);
            preparedStatement.setString(2, playerUUID.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addToFaction(UUID playerUUID, String factionName) {
        // Get the faction ID by name
        int factionId = getFactionIdByName(factionName);

        if (factionId != -1) {
            // Update the player's faction ID in the player_data table
            String updatePlayerFactionSQL = "UPDATE player_data SET faction_id = ? WHERE player_uuid = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updatePlayerFactionSQL)) {
                preparedStatement.setInt(1, factionId);
                preparedStatement.setString(2, playerUUID.toString());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }




    public int getFactionIdByName(String factionName) {
        String getFactionIdSQL = "SELECT id FROM faction WHERE name = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(getFactionIdSQL)) {
            preparedStatement.setString(1, factionName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return -1 by default if there's an error or the faction name is not found
        return -1;
    }

    public FactionInfo getFactionInfoByName(String factionName) {
        String query = "SELECT * FROM faction WHERE name = ?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, factionName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int factionId = resultSet.getInt("id");
                    int factionOwnerId = resultSet.getInt("owner_id");

                    // Create a FactionInfo object with the retrieved data
                    FactionInfo factionInfo = new FactionInfo(factionId, factionName, factionOwnerId);
                    // Populate other faction information in the FactionInfo object

                    return factionInfo;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if the faction with the provided name was not found
    }
    public List<UUID> getFactionMembersByFactionId(int factionId) {
        List<UUID> members = new ArrayList<>();
        String getMembersSQL = "SELECT player_uuid FROM player_data WHERE faction_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(getMembersSQL)) {
            preparedStatement.setInt(1, factionId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String memberUUIDString = resultSet.getString("player_uuid");
                    UUID memberUUID = UUID.fromString(memberUUIDString);
                    members.add(memberUUID);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return members;
    }

    public void transferFactionOwnership(int factionId, UUID newOwnerUUID) {
        // Check if the new owner is a member of the faction
        if (!isPlayerInFaction(newOwnerUUID, factionId)) {
            // Handle the case where the new owner is not a member of the faction
            // You can throw an exception or return an error message
            return;
        }

        // Update the owner UUID in the faction table
        String updateOwnerSQL = "UPDATE faction SET owner_id = ? WHERE id = ?";

        PlayerAccountDao playerAccountDao = new PlayerAccountDao(this.connection);

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateOwnerSQL)) {
            preparedStatement.setInt(1, playerAccountDao.getPlayerIdByUUID(newOwnerUUID));
            preparedStatement.setInt(2, factionId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public FactionInfo getFactionInfoByPlayerUUID(UUID playerUUID) {
        String getFactionInfoSQL = "SELECT f.id AS faction_id, f.name AS faction_name, f.owner_id AS faction_owner " +
                "FROM faction f " +
                "INNER JOIN player_data p ON f.id = p.faction_id " +
                "WHERE p.player_uuid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(getFactionInfoSQL)) {
            preparedStatement.setString(1, playerUUID.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int factionId = resultSet.getInt("faction_id");
                    String factionName = resultSet.getString("faction_name");
                    int factionOwnerId = resultSet.getInt("faction_owner");

                    return new FactionInfo(factionId, factionName, factionOwnerId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return null if no faction info is found or there's an error
        return null;
    }

    public String getFactionNameByPlayerUUID(UUID playerUUID) {
        String getFactionNameSQL = "SELECT f.name FROM faction f " +
                "JOIN player_data p ON f.id = p.faction_id " +
                "WHERE p.player_uuid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(getFactionNameSQL)) {
            preparedStatement.setString(1, playerUUID.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("name"); // Return the faction name
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return null if the player's faction name is not found or there's an error
        return null;
    }
}