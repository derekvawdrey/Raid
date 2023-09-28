package me.amogus360.raid.DAO;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import me.amogus360.raid.Model.PlayerAccount;
public class PlayerAccountDao {

    private Connection connection;

    public PlayerAccountDao(Connection connection) {
        this.connection = connection;
    }

    public void createAccount(PlayerAccount account) throws SQLException {
        // SQL to insert account into database
    }

    public PlayerAccount getAccount(String playerName) throws SQLException {
        // SQL to retrieve account from database
        return null;
    }

    public void updateBalance(PlayerAccount account) throws SQLException {
        // SQL to update balance
    }

    // Other CRUD methods
}