package app.entities;

public class User
{
    private int userId;
    private String username;
    private String password;
    private String role;
    private int balance;

    public User(int userId, String username, String password, String role, int balance)
    {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.balance = balance;
    }

    public User(int userId, String username, String password, String role)
    {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public int getBalance() {
        return balance;
    }

    public void deposit()
    {

    }

    public void buy()
    {

    }
}
