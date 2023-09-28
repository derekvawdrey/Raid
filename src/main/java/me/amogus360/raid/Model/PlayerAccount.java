package me.amogus360.raid.Model;

public class PlayerAccount {

    private int id;
    private String playerName;
    private double balance;

    public PlayerAccount(String playerName, double balance) {
        this.playerName = playerName;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    // Other methods like deposit(), withdraw(), etc
}