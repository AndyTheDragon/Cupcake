package app.persistence;

import app.exceptions.DatabaseException;
import app.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;


public class UserMapper {

    public static void createUser(String username, String password, ConnectionPool pool) {
        String sql = "insert into public.\"users\" (username, password, role) VALUES (?,?,?);";

        try (Connection connection = pool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql))
            {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, "customer");

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected != 1) {
                    throw new DatabaseException("Fejl ved oprettelse af bruger");
                }
            }
        } catch (DatabaseException e) {
            throw new RuntimeException();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
