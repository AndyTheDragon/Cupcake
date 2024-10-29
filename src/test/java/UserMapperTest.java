import app.persistence.UserMapper;
import app.exceptions.DatabaseException;
import app.entities.User;
import app.persistence.ConnectionPool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class UserMapperTest
{

    private ConnectionPool testPool;

    @BeforeEach
    public void setUp() throws Exception
    {
        testPool = ConnectionPool.getInstance(
                "postgres",
                "postgres",
                "jdbc:postgresql://localhost:5432/%s",
                "olsker_cupcake"
        );

        databaseTearDown();
        setUpTestSchema();
    }

    private void databaseTearDown() throws Exception
    {
        try (Connection connection = testPool.getConnection(); Statement statement = connection.createStatement())
        {
            statement.execute("DROP SCHEMA IF EXISTS test_schema CASCADE;");
        }
    }

    private void setUpTestSchema() throws Exception
    {
        try (Connection connection = testPool.getConnection(); Statement statement = connection.createStatement())
        {
            statement.execute("SET search_path TO test_schema;");
            statement.execute("CREATE SCHEMA IF NOT EXISTS test_schema;");
            statement.execute("CREATE TABLE IF NOT EXISTS test_schema.users (" +
                    "user_id serial PRIMARY KEY, " +
                    "username VARCHAR(64) NOT NULL UNIQUE, " +
                    "password VARCHAR(64) NOT NULL, " +
                    "balance INTEGER DEFAULT 0, " +
                    "role VARCHAR(12) NOT NULL);");

            // Insert sample data
            statement.execute("INSERT INTO test_schema.users (username, password, balance, role) VALUES " +
                    "('aliceFromWonderland', 'password123', 1500, 'customer'), " +
                    "('bobTheBuilder', 'password123', 2000, 'admin');");
        }
    }

    @Test
    public void testCreateUser() throws DatabaseException
    {
        // Act
        UserMapper.createUser("TestUser-Sir-CreatesAlot", "password123", testPool);

        // Assert
        try (Connection connection = testPool.getConnection(); Statement statement = connection.createStatement())
        {
            statement.execute("SET search_path TO test_schema;");
            var rs = statement.executeQuery("SELECT * FROM users WHERE username = 'TestUser-Sir-CreatesAlot';");
            Assertions.assertTrue(rs.next(), "User 'TestUser-Sir-CreatesAlot' should be added to the database.");
            Assertions.assertEquals("customer", rs.getString("role"), "Role should be 'customer' by default.");
        } catch (Exception e)
        {
            Assertions.fail("Database error during verification: " + e.getMessage());
        }
    }

    @Test
    public void testLoginSuccessful() throws DatabaseException
    {
        // Act
        User user = UserMapper.login("aliceFromWonderland", "password123", testPool);

        // Assert
        Assertions.assertNotNull(user, "User should be found with correct credentials.");
        Assertions.assertEquals("aliceFromWonderland", user.getUsername(), "Username should match.");
        Assertions.assertEquals(1500, user.getBalance(), "Balance should match.");
    }

    @Test
    public void testLoginFailure()
    {
        // Assert
        DatabaseException exception = Assertions.assertThrows(DatabaseException.class, () -> {
            // Act
            UserMapper.login("aliceFromWonderland", "wrongpassword", testPool);
        });
        Assertions.assertEquals("Brugernavn/kodeord matcher ikke. Prøv igen", exception.getMessage(),
                "Error message should indicate a failed login.");
    }

    @Test
    public void testGetAllUsers() throws DatabaseException
    {
        // Act
        List<User> users = UserMapper.getAllUsers(testPool);

        // Assert
        Assertions.assertFalse(users.isEmpty(), "Users list should not be empty.");
        Assertions.assertEquals(2, users.size(), "There should be two users in the database.");
        Assertions.assertEquals("aliceFromWonderland", users.get(0).getUsername(), "Customer user should be first in sorted list.");
        Assertions.assertEquals("bobTheBuilder", users.get(1).getUsername(), "Admin user should be lase in sorted list.");
    }
}
