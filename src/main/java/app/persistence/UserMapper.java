package app.persistence;

import app.exceptions.DatabaseException;
import app.entities.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class UserMapper {

    public static void updateCustomerBalance(int balance, int userId, ConnectionPool pool) throws DatabaseException
    {
        String sql = "UPDATE users SET balance = ? WHERE user_id = ?";
        try (Connection connection = pool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, balance);
            ps.setInt(2, userId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1)
            {
                throw new DatabaseException("fejl ved ændring af brugerens balance.");
            }
        } catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    public static void payForOrder(User user, ConnectionPool dbConnection) throws DatabaseException
    {
        String sql = "UPDATE users SET balance=? WHERE user_id=?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, user.getBalance());
            ps.setInt(2, user.getUserId());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1)
            {
                throw new DatabaseException("Fejl ved betaling.");
            }
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public static void createUser(String username, String password, ConnectionPool pool) throws DatabaseException
    {
        String sql = "insert into users (username, password, role) VALUES (?,?,?);";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        try (Connection connection = pool.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            ps.setString(3, "customer");

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Fejl ved oprettelse af bruger");
            }
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public static User login(String username, String password, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "SELECT * FROM users WHERE username=?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                String storedHashedPassword = rs.getString("password");
                if (BCrypt.checkpw(password, storedHashedPassword))
                {
                    int id = rs.getInt("user_id");
                    String role = rs.getString("role");
                    int balance = rs.getInt("balance");
                    return new User(id, username, role, balance);
                }
                else
                {
                    // Catching wrong passwords.
                    throw new DatabaseException("Kodeord matcher ikke. Prøv igen");
                }
            }
            else
            {
                // Catching wrong usernames.
                throw new DatabaseException("Brugernavn matcher ikke. Prøv igen");
            }
        } catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    public static List<User> getAllUsers(ConnectionPool pool) throws DatabaseException
    {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT user_id, username, password, role, balance FROM users ORDER BY role DESC, username";

        try (Connection connection = pool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String role = rs.getString("role");
                int balance = rs.getInt("balance");

                userList.add(new User(userId, username, role, balance));
            }
            return userList;

        } catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    public static User getUser(int userId, ConnectionPool dbConnection) throws DatabaseException
    {
        String sql = "SELECT user_id, username, role, balance FROM users WHERE user_id=?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                return new User(userId, rs.getString("username"), rs.getString("role"), rs.getInt("balance"));
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
        return null;
    }
}
