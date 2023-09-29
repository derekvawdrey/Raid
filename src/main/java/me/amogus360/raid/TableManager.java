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

    public Connection getConnection(){
        return this.connection;
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


}
