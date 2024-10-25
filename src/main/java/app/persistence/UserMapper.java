package app.persistence;

import app.exceptions.DatabaseException;
import app.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class UserMapper {

    public static void createUser(String username, String password, ConnectionPool pool) throws DatabaseException
    {
        String sql = "insert into users (username, password, role) VALUES (?,?,?);";

        try (Connection connection = pool.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, "customer");

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Fejl ved oprettelse af bruger");
            }
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public static User login(String username, String password, ConnectionPool connectionPool) throws DatabaseException {

        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int id = rs.getInt("user_id");
                    String role = rs.getString("role");
                    int balance = rs.getInt("balance");
                    return new User(id, username, password, role, balance);
                } else {
                    throw new DatabaseException("Brugernavn/kodeord matcher ikke. Prøv igen");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public static List<User> getAllUsers(ConnectionPool pool) throws DatabaseException {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT user_id, username, password, role, balance FROM users ORDER BY role DESC, username ASC";

        try (Connection connection = pool.getConnection()){
            try(PreparedStatement ps = connection.prepareStatement(sql)){
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String role = rs.getString("role");
                    int balance = rs.getInt("balance");

                    userList.add(new User(userId, username, password, role, balance));
                }
                return userList;
            }
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}
