package app.persistence;

import app.exceptions.DatabaseException;
import app.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;


public class UserMapper {

    public static void createUser(String username, String password, ConnectionPool pool) throws DatabaseException {
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

}
                public static User login(String name, String password, ConnectionPool connectionPool) throws DataBaseException {

                    String sql = "Select * from \"user\" where name=? and password=?";
                    try (Connection connection = connectionPool.getConnection()) {
                        try (PreparedStatement ps = connection.prepareStatement(sql)) {
                            ps.setString(1, name);
                            ps.setString(2, password);
                            ResultSet rs = ps.executeQuery();
                            if (rs.next()) {
                                int id = rs.getInt("id");
                                return new User(id, name, password);
                            } else {
                                throw new DataBaseException("fejl i login. Prøv igen");
                            }
                        }

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
