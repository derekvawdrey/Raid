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
        createFactionTable();
        createMoneyDataTable();
        createTitleTable();
        createFactionInvitesTable();
        createLandClaimsTable();
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
                            "player_name TEXT NOT NULL," +
                            "faction_id INTEGER," + // Reference to the faction
                            "title_id INTEGER," +   // Reference to the title
                            "FOREIGN KEY (faction_id) REFERENCES faction (id)," +
                            "FOREIGN KEY (title_id) REFERENCES title (id)" +
                            ");";

                    statement.executeUpdate(createTableSQL);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTitleTable() {
        try {
            if (!tableExists("title")) {
                try (Statement statement = connection.createStatement()) {
                    String createTableSQL = "CREATE TABLE title (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT NOT NULL" +
                            ");";

                    statement.executeUpdate(createTableSQL);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createFactionTable() {
        try {
            if (!tableExists("faction")) {
                try (Statement statement = connection.createStatement()) {
                    String createTableSQL = "CREATE TABLE faction (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT NOT NULL," +
                            "owner_id INTEGER," +
                            "money INT DEFAULT 0," + // Add a money column with a default value of 0
                            "FOREIGN KEY (owner_id) REFERENCES player_data (id)" +
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

    private void createFactionInvitesTable() {
        try {
            if (!tableExists("faction_invites")) {
                try (Statement statement = connection.createStatement()) {
                    String createTableSQL = "CREATE TABLE faction_invites (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "inviter_uuid TEXT NOT NULL," +
                            "invitee_uuid TEXT NOT NULL," +
                            "faction_id INTEGER NOT NULL," +
                            "FOREIGN KEY (inviter_uuid) REFERENCES player_data (player_uuid)," +
                            "FOREIGN KEY (invitee_uuid) REFERENCES player_data (player_uuid)," +
                            "FOREIGN KEY (faction_id) REFERENCES faction (id)" +
                            ");";

                    statement.executeUpdate(createTableSQL);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void createLandClaimsTable() {
        try {
            if (!tableExists("land_claims")) {
                try (Statement statement = connection.createStatement()) {
                    String createTableSQL = "CREATE TABLE land_claims (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "faction_id INTEGER NOT NULL," +
                            "chunk_x INT NOT NULL," +
                            "chunk_y INT NOT NULL," +
                            "chunk_z INT NOT NULL," +
                            "chunk_world TEXT NOT NULL, " +
                            "FOREIGN KEY (faction_id) REFERENCES faction (id)" +
                            ");";

                    statement.executeUpdate(createTableSQL);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}
