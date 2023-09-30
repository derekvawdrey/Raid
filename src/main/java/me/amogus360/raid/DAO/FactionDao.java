package me.amogus360.raid.DAO;
import me.amogus360.raid.Model.FactionInfo;
import me.amogus360.raid.Model.LandClaim;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FactionDao {

    private final Connection connection;

    public FactionDao(Connection connection) {
        this.connection = connection;
    }

    public int createFaction(UUID ownerUUID, String factionName) {
        String insertFactionSQL = "INSERT INTO faction (name, owner_id) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertFactionSQL, Statement.RETURN_GENERATED_KEYS)) {
            // Set the faction name
            preparedStatement.setString(1, factionName);

            // Set the owner's UUID
            PlayerAccountDao playerAccountDao = new PlayerAccountDao(this.connection);
            preparedStatement.setInt(2, playerAccountDao.getPlayerIdByUUID(ownerUUID));

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
        String updateMembersSQL = "UPDATE player_data SET faction_id = NULL, title_id = NULL WHERE faction_id = ?";

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

    public boolean hasTitle(UUID playerUUID, String title) {
        String checkTitleSQL = "SELECT COUNT(*) AS title_count FROM player_titles WHERE player_uuid = ? AND title_name = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(checkTitleSQL)) {
            preparedStatement.setString(1, playerUUID.toString());
            preparedStatement.setString(2, title);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int titleCount = resultSet.getInt("title_count");
                    return titleCount > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    public String getPlayerTitle(UUID playerUUID) {
        // Get the title_id associated with the player's UUID
        int titleId = getTitleIdByPlayerUUID(playerUUID);

        // If the titleId is -1, the player doesn't have a title
        if (titleId == -1) {
            return "No Title"; // You can return a default title or handle it as you prefer
        }

        // Get the title name from the title table based on titleId
        String getTitleSQL = "SELECT name FROM title WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(getTitleSQL)) {
            preparedStatement.setInt(1, titleId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return a default title if there's an error or the title is not found
        return "No Title";
    }

    public boolean hasPlayerTitle(UUID playerUUID, int titleId) {
        String checkTitleSQL = "SELECT COUNT(*) AS title_count FROM player_data WHERE player_uuid = ? AND title_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(checkTitleSQL)) {
            preparedStatement.setString(1, playerUUID.toString());
            preparedStatement.setInt(2, titleId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int titleCount = resultSet.getInt("title_count");
                    return titleCount > 0; // Player has the title if title_count is greater than 0
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return false if there's an error or the player doesn't have the specified title
        return false;
    }


    public int getTitleIdByName(String titleName) {
        String getTitleIdSQL = "SELECT id FROM title WHERE name = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(getTitleIdSQL)) {
            preparedStatement.setString(1, titleName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return -1 by default if there's an error or the title name is not found
        return -1;
    }


    private int getTitleIdByPlayerUUID(UUID playerUUID) {
        // Get the title_id associated with the player's UUID from the player_data table
        String getTitleIdSQL = "SELECT title_id FROM player_data WHERE player_uuid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(getTitleIdSQL)) {
            preparedStatement.setString(1, playerUUID.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("title_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return -1 by default if there's an error or the player doesn't have a title
        return -1;
    }

    public void removePlayerFromFaction(UUID playerUUID) {
        // Set faction_id and title_id to NULL for the player in player_data
        String removePlayerSQL = "UPDATE player_data SET faction_id = NULL, title_id = NULL WHERE player_uuid = ?";

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


    public void setTitleForPlayer(UUID playerUUID, String titleName) {
        // Get the title_id based on the title name
        int titleId = getTitleIdByName(titleName);

        if (titleId == -1) {
            // Title not found; you can handle this as needed, e.g., return an error message
            return;
        }

        // Update the player's title_id in player_data
        String updateTitleSQL = "UPDATE player_data SET title_id = ? WHERE player_uuid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateTitleSQL)) {
            preparedStatement.setInt(1, titleId);
            preparedStatement.setString(2, playerUUID.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeTitleFromPlayer(UUID playerUUID) {
        // Set title_id to NULL for the player in player_data
        String removeTitleSQL = "UPDATE player_data SET title_id = NULL WHERE player_uuid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(removeTitleSQL)) {
            preparedStatement.setString(1, playerUUID.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getPlayersWithTitlesInFaction(UUID factionOwnerUUID) {
        List<String> playersWithTitles = new ArrayList<>();

        // Get the faction ID associated with the owner
        int factionId = getFactionIdByOwner(factionOwnerUUID);

        if (factionId == -1) {
            // Faction not found or the player is not the owner
            return playersWithTitles;
        }

        // Retrieve players in the faction with titles
        String getPlayersWithTitlesSQL = "SELECT pd.player_name, t.name " +
                "FROM player_data pd " +
                "JOIN title t ON pd.title_id = t.id " +
                "WHERE pd.faction_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(getPlayersWithTitlesSQL)) {
            preparedStatement.setInt(1, factionId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String playerName = resultSet.getString("player_name");
                    String titleName = resultSet.getString("name");
                    playersWithTitles.add(playerName + " - " + titleName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playersWithTitles;
    }



    public UUID getOwnerUuidByFactionId(int factionId) {
        String getOwnerUUIDSQL = "SELECT owner_id FROM faction WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(getOwnerUUIDSQL)) {
            preparedStatement.setInt(1, factionId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String ownerUUIDString = resultSet.getString("owner_id");
                    return UUID.fromString(ownerUUIDString);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return null if the faction ID is not found or there's an error
        return null;
    }

    public UUID getOwnerUuidByFactionName(String factionName) {
        String getOwnerUUIDSQL = "SELECT owner_id FROM faction WHERE name = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(getOwnerUUIDSQL)) {
            preparedStatement.setString(1, factionName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String ownerUUIDString = resultSet.getString("owner_id");
                    return UUID.fromString(ownerUUIDString);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return null if the faction name is not found or there's an error
        return null;
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

    public void rescindInvite(UUID inviterUUID, UUID inviteeUUID, int factionId) {
        String rescindInviteSQL = "DELETE FROM faction_invites WHERE inviter_uuid = ? AND invitee_uuid = ? AND faction_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(rescindInviteSQL)) {
            preparedStatement.setString(1, inviterUUID.toString());
            preparedStatement.setString(2, inviteeUUID.toString());
            preparedStatement.setInt(3, factionId);
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
            rescindInvite(inviteId); // Pass the inviteId to the rescindInvite function
            addToFaction(inviteeUUID, factionId); // Implement addToFaction as needed
        }
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


    public void acceptInviteByFactionName(UUID inviteeUUID, String factionName) {
        // Get the faction ID based on the faction name
        int factionId = getFactionIdByName(factionName);

        if (factionId == -1) {
            // Faction not found; you can handle this as needed, e.g., return an error message
            return;
        }

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
            rescindInvite(inviteId); // Pass the inviteId to the rescindInvite function
            addToFaction(inviteeUUID, factionId); // Implement addToFaction as needed
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

    public void claimLand(int factionId, Location location) {
        int x = (int) location.getX();
        int z = (int) location.getZ();

        String claimLandSQL = "INSERT INTO land_claims (faction_id, x, z) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(claimLandSQL)) {
            preparedStatement.setInt(1, factionId);
            preparedStatement.setInt(2, x);
            preparedStatement.setInt(3, z);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void removeLandClaim(int claimId) {
        String removeClaimSQL = "DELETE FROM land_claims WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(removeClaimSQL)) {
            preparedStatement.setInt(1, claimId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isWithinRadiusOfSameFactionClaim(int factionId, Location location, int radius) {
        // Calculate the boundaries of the claim
        int x = (int) location.getX();
        int z = (int) location.getZ();
        int minX = x - radius;
        int maxX = x + radius;
        int minZ = z - radius;
        int maxZ = z + radius;

        String querySQL = "SELECT COUNT(*) AS claim_count " +
                "FROM land_claims " +
                "WHERE faction_id = ? " +  // Restrict to claims of the same faction
                "AND x BETWEEN ? AND ? " +
                "AND z BETWEEN ? AND ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setInt(1, factionId);
            preparedStatement.setInt(2, minX);
            preparedStatement.setInt(3, maxX);
            preparedStatement.setInt(4, minZ);
            preparedStatement.setInt(5, maxZ);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int claimCount = resultSet.getInt("claim_count");
                    return claimCount > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean hasOverlapWithOtherFactions(int factionId, Location location, int radius) {
        // Calculate the boundaries of the claim
        int x = (int) location.getX();
        int z = (int) location.getZ();
        int minX = x - radius;
        int maxX = x + radius;
        int minZ = z - radius;
        int maxZ = z + radius;

        String querySQL = "SELECT COUNT(*) AS overlap_count " +
                "FROM land_claims " +
                "WHERE faction_id != ? " +  // Exclude the current faction
                "AND x BETWEEN ? AND ? " +
                "AND z BETWEEN ? AND ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setInt(1, factionId);
            preparedStatement.setInt(2, minX);
            preparedStatement.setInt(3, maxX);
            preparedStatement.setInt(4, minZ);
            preparedStatement.setInt(5, maxZ);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int overlapCount = resultSet.getInt("overlap_count");
                    return overlapCount > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean nearbyClaimedArea(Location location, int radius) {
        // Calculate the boundaries of the claim
        int x = (int) location.getX();
        int z = (int) location.getZ();
        int minX = x - radius;
        int maxX = x + radius;
        int minZ = z - radius;
        int maxZ = z + radius;

        String querySQL = "SELECT COUNT(*) AS overlap_count " +
                "FROM land_claims " +
                "WHERE x BETWEEN ? AND ? " +
                "AND z BETWEEN ? AND ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setInt(1, minX);
            preparedStatement.setInt(2, maxX);
            preparedStatement.setInt(3, minZ);
            preparedStatement.setInt(4, maxZ);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int overlapCount = resultSet.getInt("overlap_count");
                    return overlapCount > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<LandClaim> getLandClaimsWithFactionInRadius(Location centerLocation, int radius) {
        List<LandClaim> landClaims = new ArrayList<>();
        String getClaimsSQL = "SELECT lc.id AS claim_id, lc.x, lc.z, f.id AS faction_id, f.name AS faction_name " +
                "FROM land_claims lc " +
                "INNER JOIN faction f ON lc.faction_id = f.id " +
                "WHERE lc.x BETWEEN ? AND ? AND lc.z BETWEEN ? AND ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(getClaimsSQL)) {
            preparedStatement.setInt(1, centerLocation.getBlockX() - radius);
            preparedStatement.setInt(2, centerLocation.getBlockX() + radius);
            preparedStatement.setInt(3, centerLocation.getBlockZ() - radius);
            preparedStatement.setInt(4, centerLocation.getBlockZ() + radius);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int claimId = resultSet.getInt("claim_id");
                    int x = resultSet.getInt("x");
                    int z = resultSet.getInt("z");
                    int factionId = resultSet.getInt("faction_id");
                    String factionName = resultSet.getString("faction_name");

                    LandClaim landClaimInfo = new LandClaim(claimId,factionId,factionName,x,z);
                    landClaims.add(landClaimInfo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return landClaims;
    }


    public List<LandClaim> getLandClaimsByFactionId(int factionId) {
        List<LandClaim> landClaims = new ArrayList<>();
        String getClaimsSQL = "SELECT lc.id AS claim_id, lc.x, lc.z, f.name AS faction_name " +
                "FROM land_claims lc " +
                "INNER JOIN faction f ON lc.faction_id = f.id " +
                "WHERE lc.faction_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(getClaimsSQL)) {
            preparedStatement.setInt(1, factionId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int claimId = resultSet.getInt("claim_id");
                    int x = resultSet.getInt("x");
                    int z = resultSet.getInt("z");
                    String factionName = resultSet.getString("faction_name");

                    // Create a LandClaim object and add it to the landClaims list
                    LandClaim landClaim = new LandClaim(claimId, factionId, factionName, x, z);
                    landClaims.add(landClaim);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return landClaims;
    }

    public List<LandClaim> checkNearbyClaims(Location centerLocation, int radius) {
        List<LandClaim> nearbyClaims = new ArrayList<>();

        String retrieveClaimsSQL = "SELECT lc.id AS claim_id, lc.faction_id, lc.x, lc.z, f.name AS faction_name " +
                "FROM land_claims lc " +
                "INNER JOIN faction f ON lc.faction_id = f.id " +
                "WHERE " +
                "  ABS(x - ?) <= ? AND " +
                "  ABS(z - ?) <= ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(retrieveClaimsSQL)) {
            preparedStatement.setDouble(1, centerLocation.getX());
            preparedStatement.setDouble(2, radius);
            preparedStatement.setDouble(3, centerLocation.getZ());
            preparedStatement.setDouble(4, radius);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int claimId = resultSet.getInt("claim_id");
                    int factionId = resultSet.getInt("faction_id");
                    int x = resultSet.getInt("x");
                    int z = resultSet.getInt("z");
                    String factionName = resultSet.getString("faction_name");

                    // Create a LandClaim object and add it to the nearbyClaims list
                    LandClaim landClaim = new LandClaim(claimId, factionId, factionName, x, z);
                    nearbyClaims.add(landClaim);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nearbyClaims;
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