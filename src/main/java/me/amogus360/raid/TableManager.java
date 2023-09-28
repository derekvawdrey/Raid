package me.amogus360.raid;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class TableManager {

    private final Connection connection;

    public TableManager(Connection connection) {
        this.connection = connection;
    }

    public void createTables() {
        createPlayerDataTable();
        createMoneyDataTable();
    }

    private boolean tableExists(String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet tables = metaData.getTables(null, null, tableName, null)) {
            return tables.next();
        }
    }

    private void createPlayerDataTable() {
        try {
            if (!tableExists("player_data")) {
                try (Statement statement = connection.createStatement()) {
                    String createTableSQL = "CREATE TABLE player_data (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "player_uuid TEXT NOT NULL," +
                            "player_name TEXT NOT NULL" +
                            ");";

                    statement.executeUpdate(createTableSQL);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createMoneyDataTable() {
        try {
            if (!tableExists("money_data")) {
                try (Statement statement = connection.createStatement()) {
                    String createTableSQL = "CREATE TABLE money_data (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "player_id INTEGER," +
                            "money INT DEFAULT 0," +
                            "FOREIGN KEY (player_id) REFERENCES player_data (id)" +
                            ");";

                    statement.executeUpdate(createTableSQL);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add methods for other table-related operations here

    public void insertPlayer(UUID playerUUID, String playerName) {
        // First, check if a player with the same UUID already exists
        if (playerExists(playerUUID)) {
            // Handle the case where the player already exists (e.g., update their data)
            // You can add your logic here if needed.
            System.out.println("Player with UUID " + playerUUID + " already exists.");
            return; // Exit the method without inserting a new record
        }

        // If the player doesn't exist, insert a new record
        String insertSQL = "INSERT INTO player_data (player_uuid, player_name) VALUES (?, ?);";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, playerUUID.toString());
            preparedStatement.setString(2, playerName);
            preparedStatement.executeUpdate();

            // Get the auto-generated ID of the inserted player
            int playerId = -1;
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                playerId = generatedKeys.getInt(1);
            }

            // Insert the player's ID into the money_data table
            if (playerId != -1) {
                insertMoneyData(playerId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean playerExists(UUID playerUUID) {
        String querySQL = "SELECT COUNT(*) FROM player_data WHERE player_uuid = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setString(1, playerUUID.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to assuming the player doesn't exist in case of errors
    }


    private void insertMoneyData(int playerId) {
        String insertSQL = "INSERT INTO money_data (player_id) VALUES (?);";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setInt(1, playerId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMoney(UUID playerUUID, int amount) {
        String updateSQL = "UPDATE money_data SET money = money + ? WHERE player_id = (SELECT id FROM player_data WHERE player_uuid = ?);";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, playerUUID.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPlayerMoney(UUID playerUUID) {
        String querySQL = "SELECT money FROM money_data WHERE player_id = (SELECT id FROM player_data WHERE player_uuid = ?);";
        try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setString(1, playerUUID.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("money");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Default to 0 in case of errors or if the player is not found
    }

}
