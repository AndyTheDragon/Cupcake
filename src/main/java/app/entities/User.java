package app.entities;

public class User
{
    private final int userId;
    private final String username;
    private final String role;
    private int balance;

    public User(int userId, String username, String role, int balance)
    {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.balance = balance;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public int getBalance() {
        return balance;
    }

    public void deposit(int amount)
    {
        this.balance += amount;
    }

    public void buy(int amount)
    {
        this.balance -= amount;
    }

    @Override
    public String toString()
    {
        return this.username;
    }
}
