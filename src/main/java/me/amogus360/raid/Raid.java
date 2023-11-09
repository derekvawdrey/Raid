package me.amogus360.raid;

import mc.obliviate.inventory.InventoryAPI;
import me.amogus360.raid.CommandManager.FactionCommandManager;
import me.amogus360.raid.CommandManager.ItemCommandManager;
import me.amogus360.raid.CommandManager.MoneyCommandManager;
import me.amogus360.raid.CommandManager.RaidCommandManager;
import me.amogus360.raid.Commands.NoCommandManager.SetSpawnCommand;
import me.amogus360.raid.Commands.NoCommandManager.SpawnCommand;
import me.amogus360.raid.Commands.NoCommandManager.WildTeleportCommand;
import me.amogus360.raid.EventHandlers.*;
import me.amogus360.raid.Model.ItemGlow;
import me.amogus360.raid.Tasks.BlockReplacementTask;
import me.amogus360.raid.Tasks.BossBarUpdateTask;
import me.amogus360.raid.Tasks.JobTask;
import me.amogus360.raid.Tasks.RaidStartEndTask;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Raid extends JavaPlugin implements Listener {
    private Connection connection;
    private static String databaseName = "raid.db";
    private DataAccessManager dataAccessManager;
    private TableManager tableManager;

    @Override
    public void onEnable() {

        getLogger().info("Plugin enabled!");

        setupDatabase();
        TableManager tableManager = new TableManager(connection);
        tableManager.createTables();

        dataAccessManager = new DataAccessManager(connection, this);
        initCommands();
        initEvents();
        initTasks();
        registerGlow();
        new InventoryAPI(this).init();
    }

    private void initCommands(){
        getCommand("raid").setExecutor(new RaidCommandManager(this, this.dataAccessManager));
        getCommand("money").setExecutor(new MoneyCommandManager(this, this.dataAccessManager));
        getCommand("faction").setExecutor(new FactionCommandManager(this, this.dataAccessManager));
        getCommand("item").setExecutor(new ItemCommandManager(this, this.dataAccessManager));

        // Non-commandManager classes
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("wildtp").setExecutor(new WildTeleportCommand(this));
    }

    private void initTasks(){
        BlockReplacementTask.startTask(this, this.dataAccessManager);
        BossBarUpdateTask.startTask(this,this.dataAccessManager);
        RaidStartEndTask.startTask(this, this.dataAccessManager);
        JobTask.startTask(this, this.dataAccessManager);
    }

    private void initEvents(){
        // Register the event handler
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinEventHandler(this.dataAccessManager), this);
        pluginManager.registerEvents(new LandClaimBlockEventHandler(this.dataAccessManager), this);
        pluginManager.registerEvents(new RaidBossEventHandler(this.dataAccessManager), this);
        pluginManager.registerEvents(new ItemUseEventHandler(this.dataAccessManager), this);
        pluginManager.registerEvents(new FactionRevivedChatHandler(this.dataAccessManager), this);
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


    public void registerGlow() {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            NamespacedKey key = new NamespacedKey(this, getDescription().getName());

            ItemGlow glow = new ItemGlow(key);
            Enchantment.registerEnchantment(glow);
        }
        catch (IllegalArgumentException e){
        }
        catch(Exception e){
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
