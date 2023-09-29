package me.amogus360.raid;

import me.amogus360.raid.EventHandlers.PlayerJoinEventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Raid extends JavaPlugin implements Listener {
    private Connection connection;
    private static String databaseName = "raid.db";

    @Override
    public void onEnable() {
        getLogger().info("Plugin enabled!");

        setupDatabase();
        TableManager tableManager = new TableManager(connection);
        tableManager.createTables();

        initCommands(tableManager);
        initEvents(tableManager);
    }

    private void initCommands(TableManager tableManager){
        getCommand("raid").setExecutor(new RaidCommandManager(this, connection));
    }

    private void initEvents(TableManager tableManager){
        // Register the event handler
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinEventHandler(tableManager), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled!");
        closeDatabase();
    }

    private void setupDatabase() {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Define the database URL
            String dbUrl = "jdbc:sqlite:" + databaseName;

            // Check if the database file doesn't exist and create it
            File dbFile = new File(getDataFolder(), databaseName);
            if (!dbFile.exists()) {
                dbFile.getParentFile().mkdirs();
                dbFile.createNewFile();
            }

            // Connect to the SQLite database
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getPath());
            getLogger().info("Connected to the database!");

            // Create tables and perform other database setup here
            // For example: createTable();
        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void closeDatabase() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                getLogger().info("Database connection closed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
