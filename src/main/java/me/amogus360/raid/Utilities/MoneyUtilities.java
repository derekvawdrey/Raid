package me.amogus360.raid.Utilities;

import me.amogus360.raid.DAO.PlayerAccountDao;
import me.amogus360.raid.DataAccessManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class MoneyUtilities {

    public static boolean buyItem(DataAccessManager dataAccessManager, UUID playerUUID, int itemCost) throws SQLException {
        // Check if the player has enough money to buy the item
        try {
            int playerBalance = dataAccessManager.getPlayerAccountDao().getBalance(playerUUID);

            if (playerBalance >= itemCost) {
                // Deduct the item cost from the player's balance
                dataAccessManager.getPlayerAccountDao().updateBalance(playerUUID, -itemCost);
                return true; // Purchase successful
            } else {
                return false; // Insufficient funds
            }
        } catch (SQLException e) {
            // Handle the SQLException here, e.g., log the error or throw a custom exception
            e.printStackTrace(); // This is just an example; you can choose how to handle the exception
        }

        return false;
    }

    public static int getBalance(DataAccessManager dataAccessManager, UUID playerUUID) throws SQLException {
        return dataAccessManager.getPlayerAccountDao().getBalance(playerUUID);
    }
}
