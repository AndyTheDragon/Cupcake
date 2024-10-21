package app.persistence;

import java.sql.Connection;

import static app.Main.connectionPool;

public class UserMapper {

    public static void createUser(String username, String password, String confirmPassword) {
        String sql = "insert into users () VALUES (?,?,?);";
        try (Connection connection = connectionPool.getConnection())

    }
}
