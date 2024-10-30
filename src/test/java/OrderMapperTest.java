import app.persistence.CupcakeMapper;
import app.persistence.OrderMapper;
import app.exceptions.DatabaseException;
import app.entities.*;
import app.persistence.ConnectionPool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderMapperTest
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

    @Test
    public void testCreateOrderInDb() throws DatabaseException
    {
        // Arrange
        String name = "Test Order";
        LocalDate datePlaced = LocalDate.now();
        LocalDate datePaid = null;
        String status = "pending";
        User user = new User(1, "testuser", "customer", 1000);

        // Act
        int orderId = OrderMapper.createOrderInDb(name, datePlaced, datePaid, status, user, testPool);

        // Assert
        Assertions.assertTrue(orderId > 0, "Order ID should be greater than 0.");
    }

    @Test
    public void testCreateOrderlinesInDb() throws DatabaseException
    {
        // Arrange
        int orderId = 1;
        List<OrderLine> orderLines = new ArrayList<>();
        CupcakeFlavour topFlavour = new CupcakeFlavour(1, 500, "Chocolate", "Rich chocolate", CupcakeType.TOP);
        CupcakeFlavour bottomFlavour = new CupcakeFlavour(1, 500, "Chocolate", "Rich chocolate", CupcakeType.BOTTOM);
        Cupcake cupcake = new Cupcake(topFlavour, bottomFlavour);
        orderLines.add(new OrderLine(orderId, 2, cupcake, 1000));

        // Act
        OrderMapper.createOrderlinesInDb(orderId, orderLines, testPool);

        // Assert
        try (Connection connection = testPool.getConnection(); Statement statement = connection.createStatement())
        {
            statement.execute("SET search_path TO test_schema;");
            var rs = statement.executeQuery("SELECT * FROM order_lines WHERE order_id = " + orderId);
            Assertions.assertTrue(rs.next(), "Order line should be added to the database.");
            Assertions.assertEquals(2, rs.getInt("quantity"), "Quantity should match.");
            Assertions.assertEquals(1000, rs.getInt("price"), "Price should match.");
        } catch (Exception e)
        {
            Assertions.fail("Database error during verification: " + e.getMessage());
        }
    }

    @Test
    public void testGetOrders() throws DatabaseException
    {
        // Arrange
        String name = "Test Order";
        LocalDate datePlaced = LocalDate.now();
        LocalDate datePaid = null;
        String status = "pending";
        User user = new User(1, "testuser", "customer", 1000);

        int orderId = OrderMapper.createOrderInDb(name, datePlaced, datePaid, status, user, testPool);

        // Act
        List<Order> orders = OrderMapper.getOrders("date_placed", testPool);

        // Assert
        Assertions.assertTrue(orders.stream().anyMatch(order -> order.getName().equals(name)),
                "Expected to find an order with the name 'Test Order'.");
    }

    @Test
    public void testGetCupcakeFlavour() throws DatabaseException
    {
        // Act
        CupcakeFlavour flavour = CupcakeMapper.getCupcakeFlavour("Chocolate", CupcakeType.TOP, testPool);

        // Assert
        Assertions.assertNotNull(flavour, "Cupcake flavour should not be null.");
        Assertions.assertEquals("Chocolate", flavour.getName(), "Flavour name should match.");
        Assertions.assertEquals(500, flavour.getPrice(), "Price should match.");
    }

    @Test
    public void testDeleteOrder() throws DatabaseException
    {
        // Arrange
        int orderId = 1;

        // Act
        OrderMapper.deleteOrder(orderId, testPool);

        // Assert
        try (Connection connection = testPool.getConnection(); Statement statement = connection.createStatement())
        {
            statement.execute("SET search_path TO test_schema;");
            var rs = statement.executeQuery("SELECT * FROM orders WHERE order_id = " + orderId);
            Assertions.assertFalse(rs.next(), "Order should be deleted from the database.");
        } catch (Exception e) {
            Assertions.fail("Database error during verification: " + e.getMessage());
        }
    }
}
