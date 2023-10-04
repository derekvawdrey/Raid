package me.amogus360.raid.DAO;
import me.amogus360.raid.Model.LandClaim.LandClaim;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import me.amogus360.raid.Model.LandClaim.LandClaimLocation;
import me.amogus360.raid.Utilities.LandClaimChunkUtilities;
import org.bukkit.Location;

public class LandClaimDao {

    private final Connection connection;

    public LandClaimDao(Connection connection) {
        this.connection = connection;
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

    public List<LandClaim> getLandClaimsWithFactionInRadius(Location centerLocation, int radius) {
        List<LandClaim> landClaims = new ArrayList<>();
        Location convertedChunkCoordinate = LandClaimChunkUtilities.convertToChunkCoordinate(centerLocation);
        String getClaimsSQL = "SELECT lc.id AS claim_id, lc.chunk_x, lc.chunk_z, lc.chunk_y, lc.chunk_world, f.id AS faction_id, f.name AS faction_name " +
                "FROM land_claims lc " +
                "INNER JOIN faction f ON lc.faction_id = f.id " +
                "WHERE lc.chunk_x BETWEEN ? AND ? AND lc.chunk_z BETWEEN ? AND ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(getClaimsSQL)) {
            preparedStatement.setInt(1, convertedChunkCoordinate.getBlockX() - radius);
            preparedStatement.setInt(2, convertedChunkCoordinate.getBlockX() + radius);
            preparedStatement.setInt(3, convertedChunkCoordinate.getBlockZ() - radius);
            preparedStatement.setInt(4, convertedChunkCoordinate.getBlockZ() + radius);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int claimId = resultSet.getInt("claim_id");
                    int chunk_x = resultSet.getInt("chunk_x");
                    int chunk_z = resultSet.getInt("chunk_z");
                    int chunk_y = resultSet.getInt("chunk_y");
                    int factionId = resultSet.getInt("faction_id");
                    String factionName = resultSet.getString("faction_name");
                    String chunkWorld = resultSet.getString("chunk_world");
                    LandClaimLocation chunkLocation = new LandClaimLocation(chunk_x,chunk_y,chunk_z, chunkWorld);
                    LandClaim landClaimInfo = new LandClaim(claimId, factionId, factionName, chunkLocation);
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
        String getClaimsSQL = "SELECT lc.id AS claim_id, lc.chunk_x, lc.chunk_z, lc.chunk_y, lc.chunk_world, f.name AS faction_name " +
                "FROM land_claims lc " +
                "INNER JOIN faction f ON lc.faction_id = f.id " +
                "WHERE lc.faction_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(getClaimsSQL)) {
            preparedStatement.setInt(1, factionId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int claimId = resultSet.getInt("claim_id");
                    int chunk_x = resultSet.getInt("chunk_x");
                    int chunk_z = resultSet.getInt("chunk_z");
                    int chunk_y = resultSet.getInt("chunk_y");
                    String factionName = resultSet.getString("faction_name");
                    String chunkWorld = resultSet.getString("chunk_world");

                    // Create a LandClaim object and add it to the landClaims list
                    LandClaimLocation chunkLocation = new LandClaimLocation(chunk_x,chunk_y,chunk_z, chunkWorld);
                    LandClaim landClaimInfo = new LandClaim(claimId, factionId, factionName, chunkLocation);
                    landClaims.add(landClaimInfo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return landClaims;
    }

    public List<LandClaim> getNearbyClaimChunks(Location centerLocation, int radius) {
        List<LandClaim> nearbyClaims = new ArrayList<>();
        Location convertedChunkCoordinate = LandClaimChunkUtilities.convertToChunkCoordinate(centerLocation);
        String retrieveClaimsSQL = "SELECT lc.id AS claim_id, lc.faction_id, lc.chunk_x, lc.chunk_z, lc.chunk_y, lc.chunk_world, f.name AS faction_name " +
                "FROM land_claims lc " +
                "INNER JOIN faction f ON lc.faction_id = f.id " +
                "WHERE " +
                "  chunk_x BETWEEN ? AND ? AND " +
                "  chunk_z BETWEEN ? AND ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(retrieveClaimsSQL)) {
            preparedStatement.setInt(1, convertedChunkCoordinate.getBlockX() - radius);
            preparedStatement.setInt(2, convertedChunkCoordinate.getBlockX() + radius);
            preparedStatement.setInt(3, convertedChunkCoordinate.getBlockZ() - radius);
            preparedStatement.setInt(4, convertedChunkCoordinate.getBlockZ() + radius);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int claimId = resultSet.getInt("claim_id");
                    int factionId = resultSet.getInt("faction_id");
                    int chunk_x= resultSet.getInt("chunk_x");
                    int chunk_y= resultSet.getInt("chunk_y");
                    int chunk_z= resultSet.getInt("chunk_z");
                    String worldName = resultSet.getString("chunk_world");
                    String factionName = resultSet.getString("faction_name");
                    LandClaimLocation chunkLocation = new LandClaimLocation(chunk_x,chunk_y,chunk_z, worldName);
                    // Create a LandClaim object and add it to the nearbyClaims list
                    LandClaim landClaim = new LandClaim(claimId, factionId, factionName, chunkLocation);
                    nearbyClaims.add(landClaim);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nearbyClaims;
    }

    public LandClaim getClaimChunk(Location centerLocation) {
        Location convertedChunkCoordinate = LandClaimChunkUtilities.convertToChunkCoordinate(centerLocation);
        String retrieveClaimsSQL = "SELECT lc.id AS claim_id, lc.faction_id, lc.chunk_x, lc.chunk_z, lc.chunk_y, lc.chunk_world, f.name AS faction_name " +
                "FROM land_claims lc " +
                "INNER JOIN faction f ON lc.faction_id = f.id " +
                "WHERE " +
                "  chunk_x = ? AND " +
                "  chunk_z = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(retrieveClaimsSQL)) {
            preparedStatement.setInt(1, convertedChunkCoordinate.getBlockX() );
            preparedStatement.setInt(2, convertedChunkCoordinate.getBlockZ() );
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int claimId = resultSet.getInt("claim_id");
                    int factionId = resultSet.getInt("faction_id");
                    int chunk_x= resultSet.getInt("chunk_x");
                    int chunk_y= resultSet.getInt("chunk_y");
                    int chunk_z= resultSet.getInt("chunk_z");
                    String worldName = resultSet.getString("chunk_world");
                    String factionName = resultSet.getString("faction_name");
                    LandClaimLocation chunkLocation = new LandClaimLocation(chunk_x,chunk_y,chunk_z, worldName);
                    // Create a LandClaim object and add it to the nearbyClaims list
                    LandClaim landClaim = new LandClaim(claimId, factionId, factionName, chunkLocation);
                    return landClaim;
                }else{
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isClaimed(Location location) {
        Location convertedChunkCoordinate = LandClaimChunkUtilities.convertToChunkCoordinate(location);
        int chunk_x = (int) convertedChunkCoordinate.getX();
        int chunk_y = (int) convertedChunkCoordinate.getY();
        int chunk_z = (int) convertedChunkCoordinate.getZ();
        String querySQL = "SELECT COUNT(*) AS claim_count FROM land_claims WHERE chunk_x = ? AND chunk_z = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setInt(1, chunk_x);
            preparedStatement.setInt(2, chunk_z);

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



    public LandClaim claimLand(int factionId, Location location) {
        Location convertedChunkCoordinate = LandClaimChunkUtilities.convertToChunkCoordinate(location);
        int chunk_x = (int) convertedChunkCoordinate.getX();
        int chunk_y = (int) convertedChunkCoordinate.getY();
        int chunk_z = (int) convertedChunkCoordinate.getZ();


        String claimLandSQL = "INSERT INTO land_claims (faction_id, chunk_x, chunk_y, chunk_z, chunk_world) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(claimLandSQL)) {
            preparedStatement.setInt(1, factionId);
            preparedStatement.setInt(2, chunk_x);
            preparedStatement.setInt(3, chunk_y);
            preparedStatement.setInt(4, chunk_z);
            preparedStatement.setString(5, convertedChunkCoordinate.getWorld().getName());
            preparedStatement.executeUpdate();
            LandClaimLocation landClaimLocation = new LandClaimLocation(chunk_x, chunk_y, chunk_z, convertedChunkCoordinate.getWorld().getName());
            return new LandClaim(0, factionId, "E", landClaimLocation);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasOverlapWithOtherFactions(int factionId, Location location, int radius) {
        // Calculate the boundaries of the claim
        Location player_location_converted =  LandClaimChunkUtilities.convertToChunkCoordinate(location);
        int player_chunk_x = player_location_converted.getBlockX();
        int player_chunk_z = player_location_converted.getBlockZ();
        int minchunk_x = player_chunk_x- radius;
        int maxchunk_x = player_chunk_x+ radius;
        int minchunk_z = player_chunk_z- radius;
        int maxchunk_z = player_chunk_z+ radius;

        String querySQL = "SELECT COUNT(*) AS overlap_count " +
                "FROM land_claims " +
                "WHERE faction_id != ? " +  // Exclude the current faction
                "AND chunk_x BETWEEN ? AND ? " +
                "AND chunk_z BETWEEN ? AND ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setInt(1, factionId);
            preparedStatement.setInt(2, minchunk_x);
            preparedStatement.setInt(3, maxchunk_x);
            preparedStatement.setInt(4, minchunk_z);
            preparedStatement.setInt(5, maxchunk_z);

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

    public boolean isWithinRadiusOfSameFactionClaim(int factionId, Location location, int radius) {
        // Calculate the boundaries of the claim
        Location player_location_converted =  LandClaimChunkUtilities.convertToChunkCoordinate(location);
        int player_chunk_x = player_location_converted.getBlockX();
        int player_chunk_z = player_location_converted.getBlockZ();
        int minchunk_x = player_chunk_x - radius;
        int maxchunk_x = player_chunk_x + radius;
        int minchunk_z = player_chunk_z - radius;
        int maxchunk_z = player_chunk_z + radius;

        String querySQL = "SELECT COUNT(*) AS claim_count " +
                "FROM land_claims " +
                "WHERE faction_id = ? " +  // Restrict to claims of the same faction
                "AND chunk_x BETWEEN ? AND ? " +
                "AND chunk_z BETWEEN ? AND ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setInt(1, factionId);
            preparedStatement.setInt(2, minchunk_x);
            preparedStatement.setInt(3, maxchunk_x);
            preparedStatement.setInt(4, minchunk_z);
            preparedStatement.setInt(5, maxchunk_z);

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

    public boolean nearbyClaimedArea(Location location, int radius) {
        // Calculate the boundaries of the claim

        Location chunkCoordinate = LandClaimChunkUtilities.convertToChunkCoordinate(location);
        int chunk_x = (int) chunkCoordinate.getX();
        int chunk_z = (int) chunkCoordinate.getZ();
        int minchunk_X = chunk_x - radius;
        int maxchunk_X = chunk_x + radius;
        int minchunk_Z = chunk_z - radius;
        int maxchunk_Z = chunk_z + radius;

        String querySQL = "SELECT COUNT(*) AS overlap_count " +
                "FROM land_claims " +
                "WHERE chunk_x BETWEEN ? AND ? " +
                "AND chunk_z BETWEEN ? AND ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setInt(1, minchunk_X);
            preparedStatement.setInt(2, maxchunk_X);
            preparedStatement.setInt(3, minchunk_Z);
            preparedStatement.setInt(4, maxchunk_Z);

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

}
