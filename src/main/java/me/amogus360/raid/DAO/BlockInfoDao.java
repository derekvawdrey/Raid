package me.amogus360.raid.DAO;

import me.amogus360.raid.Model.Block.BlockInfo;
import me.amogus360.raid.Utilities.BlockUtilities;
import org.bukkit.block.Block;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BlockInfoDao {

    private final Connection connection;

    public BlockInfoDao(Connection connection) {
        this.connection = connection;
    }
    public List<BlockInfo> getBlocksToReplace() {
        List<BlockInfo> blocksToReplace = new ArrayList<>();

        // Construct the SQL query to retrieve blocks where time_to_replace has elapsed
        String selectSQL = "SELECT block_info_json, id FROM block_info WHERE time_to_replace <= CURRENT_TIMESTAMP";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {

            while (resultSet.next()) {
                String blockInfoJson = resultSet.getString("block_info_json");
                BlockInfo blockInfo = BlockInfo.fromJson(blockInfoJson);
                blocksToReplace.add(blockInfo);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return blocksToReplace;
    }

    public List<BlockInfo> getAndDeleteBlocksToReplace(Timestamp restorationTimestamp) {
        System.out.println("Retrieving blocks");
        List<BlockInfo> blocksToReplace = new ArrayList<>();
        List<Integer> blockIdsToDelete = new ArrayList<>();

        // Construct the SQL query to retrieve blocks where time_to_replace has elapsed
        String selectSQL = "SELECT block_info_json, id FROM block_info WHERE time_to_replace <= ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            // Set the Timestamp parameter for the query
            preparedStatement.setTimestamp(1, restorationTimestamp);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String blockInfoJson = resultSet.getString("block_info_json");
                    BlockInfo blockInfo = BlockInfo.fromJson(blockInfoJson);
                    blocksToReplace.add(blockInfo);

                    // Store the ID for deletion
                    int blockId = resultSet.getInt("id");
                    blockIdsToDelete.add(blockId);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Delete the fetched blocks based on their IDs
        deleteBlocksByIds(blockIdsToDelete);
        System.out.println("Finished blocks");
        return blocksToReplace;
    }


    // Add a method to delete blocks by their IDs
    private void deleteBlocksByIds(List<Integer> blockIds) {
        if (blockIds.isEmpty()) {
            return;
        }

        String ids = blockIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        String sql = "DELETE FROM block_info WHERE id IN (" + ids + ");";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void insertBlocks(List<Block> blockList, Timestamp restorationDate) {
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO block_info (block_info_json, time_destroyed, time_to_replace) VALUES (?, CURRENT_TIMESTAMP, ?);"
            );

            for (Block block : blockList) {
                BlockInfo blockInfo = BlockUtilities.convertBlockToBlockInfo(block.getWorld(), block.getX(), block.getY(), block.getZ(), block);
                preparedStatement.setString(1, blockInfo.toJson());
                preparedStatement.setTimestamp(2, restorationDate);
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void insertBlockInfo(BlockInfo blockInfo, Timestamp restorationDate) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO block_info (block_info_json, time_destroyed, time_to_replace) VALUES (?, CURRENT_TIMESTAMP, ?);")) {
            preparedStatement.setString(1, blockInfo.toJson());
            preparedStatement.setTimestamp(2, restorationDate);
            preparedStatement.executeUpdate();
        }
    }

    public void removeBlockInfo(int id) {
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM block_info WHERE id = ?;")) {
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
