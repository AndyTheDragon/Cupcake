import app.entities.CupcakeFlavour;
import app.entities.CupcakeType;
import app.persistence.CupcakeMapper;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class CupcakeMapperTest
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

    private void setUpTestSchema() throws Exception
    {
        try (Connection connection = testPool.getConnection(); Statement statement = connection.createStatement())
        {
            statement.execute("SET search_path TO test_schema;");
            statement.execute("BEGIN;");
            statement.execute("DROP SCHEMA IF EXISTS test_schema CASCADE;");
            statement.execute("CREATE SCHEMA test_schema;");

            // Creating tables
            statement.execute("CREATE TABLE test_schema.users (user_id serial PRIMARY KEY, username VARCHAR(64) NOT NULL UNIQUE, password VARCHAR(64) NOT NULL, balance INTEGER DEFAULT 0, role VARCHAR(12) NOT NULL);");
            statement.execute("CREATE TABLE test_schema.orders (order_id serial PRIMARY KEY, name VARCHAR(64) NOT NULL, date_placed DATE DEFAULT NOW(), date_paid DATE, date_completed DATE, status VARCHAR(64) NOT NULL, user_id INTEGER REFERENCES test_schema.users(user_id));");
            statement.execute("CREATE TABLE test_schema.cupcake_flavours (flavour_id serial PRIMARY KEY, flavour_name VARCHAR(64) NOT NULL, is_top_flavour BOOLEAN NOT NULL, is_bottom_flavour BOOLEAN NOT NULL, price INTEGER NOT NULL, is_enabled BOOLEAN NOT NULL DEFAULT true);");
            statement.execute("CREATE TABLE test_schema.order_lines (order_line_id serial PRIMARY KEY, quantity INTEGER NOT NULL, top_flavour INTEGER REFERENCES test_schema.cupcake_flavours(flavour_id), bottom_flavour INTEGER REFERENCES test_schema.cupcake_flavours(flavour_id), price INTEGER NOT NULL, order_id INTEGER REFERENCES test_schema.orders(order_id) ON DELETE CASCADE);");

            // Insert test data
            statement.execute("INSERT INTO test_schema.cupcake_flavours (flavour_name, is_top_flavour, is_bottom_flavour, price) VALUES " +
                    "('Chocolate', true, true, 500), " +
                    "('Vanilla', false, true, 500), " +
                    "('Strawberry', true, false, 600), " +
                    "('Blueberry', true, false, 650), " +
                    "('Nutmeg', false, true, 550), " +
                    "('Almond', false, true, 700), " +
                    "('Raspberry', true, false, 750), " +
                    "('Lemon', true, true, 800), " +
                    "('Orange', true, false, 850), " +
                    "('Pistachio', false, true, 650), " +
                    "('Hazelnut', false, true, 700);");
            statement.execute("INSERT INTO test_schema.users (username, password, balance, role) VALUES ('alice', 'password123', 1500, 'customer');");
            statement.execute("INSERT INTO test_schema.orders (name, date_placed, status, user_id) VALUES ('Alice Order 1', '2023-08-15', 'completed', 1);");
            statement.execute("INSERT INTO test_schema.order_lines (quantity, top_flavour, bottom_flavour, price, order_id) VALUES (2, 1, 2, 1000, 1);");

            statement.execute("COMMIT;");
        }
    }

    public void databaseTearDown() throws Exception {
        try (Connection connection = testPool.getConnection(); Statement statement = connection.createStatement())
        {
            statement.execute("DROP SCHEMA IF EXISTS test_schema CASCADE;");
        }
    }

    @Test
    public void testAddCupcakeFlavour() throws DatabaseException
    {
        // Arrange
        String flavourName = "Mint";
        boolean flavourTop = true;
        boolean flavourBottom = false;
        int flavourPrice = 700;

        // Act
        CupcakeMapper.addCupcakeFlavour(flavourName, flavourTop, flavourBottom, flavourPrice, testPool);

        // Assert
        try (Connection connection = testPool.getConnection(); Statement statement = connection.createStatement())
        {
            statement.execute("SET search_path TO test_schema;");
            var rs = statement.executeQuery("SELECT * FROM cupcake_flavours WHERE flavour_name = 'Mint';");

            Assertions.assertTrue(rs.next(), "Mint flavour should be added to the database.");

            Assertions.assertEquals(flavourName, rs.getString("flavour_name"), "The flavour name should match.");
            Assertions.assertEquals(flavourTop, rs.getBoolean("is_top_flavour"), "The top flavour flag should match.");
            Assertions.assertEquals(flavourBottom, rs.getBoolean("is_bottom_flavour"), "The bottom flavour flag should match.");
            Assertions.assertEquals(flavourPrice, rs.getInt("price"), "The price should match the inserted value.");

            Assertions.assertFalse(rs.next(), "There should be only one 'Mint' flavour entry in the database.");
        } catch (Exception e) {
            Assertions.fail("Database error during verification: " + e.getMessage());
        }
    }


    @Test
    public void testGetFlavoursBottom() throws DatabaseException
    {
        // Act
        List<CupcakeFlavour> bottomFlavours = CupcakeMapper.getFlavours(CupcakeType.BOTTOM, testPool);

        // Assert
        Assertions.assertEquals(7, bottomFlavours.size(), "Expected 7 bottom flavours.");
        Assertions.assertTrue(bottomFlavours.stream().allMatch(f -> f.getType() == CupcakeType.BOTTOM), "All should be bottom flavours.");
    }

    @Test
    public void testGetFlavoursTop() throws DatabaseException
    {
        // Act
        List<CupcakeFlavour> topFlavours = CupcakeMapper.getFlavours(CupcakeType.TOP, testPool);

        // Assert
        Assertions.assertEquals(6, topFlavours.size(), "Expected 6 bottom flavours.");
        Assertions.assertTrue(topFlavours.stream().allMatch(f -> f.getType() == CupcakeType.TOP), "All should be top flavours.");
    }
}
