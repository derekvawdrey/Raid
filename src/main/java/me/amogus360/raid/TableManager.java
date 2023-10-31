package me.amogus360.raid;
import me.amogus360.raid.Model.FactionInfo;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
        createFactionInvitesTable();
        createLandClaimsTable();
        createBlockInfoTable();
        createRaidsTable();
        createRaidParticipantsTable();
    }

    private boolean tableExists(String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet tables = metaData.getTables(null, null, tableName, null)) {
            return tables.next();
        }
    }

    private void createPlayerDataTable() {
        try {
            //TODO: Remove table for titles and simply assign title string
            if (!tableExists("player_data")) {
                try (Statement statement = connection.createStatement()) {
                    String createTableSQL = "CREATE TABLE player_data (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "player_uuid TEXT NOT NULL," +
                            "player_name TEXT NOT NULL," +
                            "faction_id INTEGER," + // Reference to the faction
                            "faction_title TEXT," +   // Reference to the title
                            "FOREIGN KEY (faction_id) REFERENCES faction (id)" +
                            ");";

                    statement.executeUpdate(createTableSQL);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createBlockInfoTable() {
        try {
            if (!tableExists("block_info")) {
                try (Statement statement = connection.createStatement()) {
                    String createTableSQL = "CREATE TABLE block_info (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "block_info_json TEXT NOT NULL," +
                            "time_destroyed DATETIME," +
                            "time_to_replace DATETIME" +
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
                            "spawn_x DOUBLE NOT NULL, " +
                            "spawn_y DOUBLE NOT NULL, " +
                            "spawn_z DOUBLE NOT NULL, " +
                            "spawn_world TEXT NOT NULL, " +
                            "raid_boss_x DOUBLE NOT NULL, " +
                            "raid_boss_y DOUBLE NOT NULL, " +
                            "raid_boss_z DOUBLE NOT NULL, " +
                            "raid_boss_world TEXT NOT NULL, " +
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
                            "money INT DEFAULT 500," +
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

    private void createRaidsTable() {
        try {
            if (!tableExists("raids")) {
                try (Statement statement = connection.createStatement()) {
                    String createTableSQL = "CREATE TABLE raids (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "attacking_faction_id INTEGER NOT NULL," +
                            "defending_faction_id INTEGER NOT NULL," +
                            "in_progress INTEGER DEFAULT 0," +
                            "boss_killed INTEGER DEFAULT 0," +
                            "start_time DATETIME NOT NULL," +
                            "end_time DATETIME NOT NULL," +
                            "FOREIGN KEY (attacking_faction_id) REFERENCES faction (id)," +
                            "FOREIGN KEY (defending_faction_id) REFERENCES faction (id)" +
                            ");";

                    statement.executeUpdate(createTableSQL);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createRaidParticipantsTable() {
        try {
            if (!tableExists("raid_participants")) {
                try (Statement statement = connection.createStatement()) {
                    String createTableSQL = "CREATE TABLE raid_participants (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "raid_id INTEGER NOT NULL," +
                            "faction_id INTEGER NOT NULL," +
                            "FOREIGN KEY (raid_id) REFERENCES raids (id)," +
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
