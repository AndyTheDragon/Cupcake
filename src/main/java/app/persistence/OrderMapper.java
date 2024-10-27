package app.persistence;

import app.entities.*;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper
{

    public static int createOrderInDb(String name, LocalDate datePlaced, LocalDate datePaid, String status, User user, ConnectionPool pool) throws DatabaseException
    {
        String sql = "INSERT INTO orders (name, date_placed, date_paid, status, user_id) VALUES (?,?,?,?,?)";
        try (Connection connection = pool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {

            ps.setString(1, name);
            ps.setDate(2, Date.valueOf(datePlaced));
            if ((datePaid == null))
            {
                ps.setNull(3, Types.DATE);
            } else
            {
                ps.setDate(3, Date.valueOf(datePaid));
            }
            ps.setString(4, status);
            if (user != null)
            {
                ps.setInt(5, user.getUserId());
            }
            else
            {
                ps.setNull(5, Types.INTEGER);
            }

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected != 1)
            {
                throw new DatabaseException("fejl........");
            }

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
            {
                // Return the generated orderId
                return rs.getInt(1);  // assuming orderId is in the first column
            }
            else
            {
                throw new DatabaseException("Kunne ikke hente genereret ordre-id.");
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    public static void createOrderlinesInDb(int orderId, List<OrderLine> orderLines, ConnectionPool pool) throws DatabaseException
    {
        String sql = "INSERT INTO order_lines (quantity, top_flavour, bottom_flavour, price, order_id) VALUES (?,?,?,?,?)";
        try (Connection connection = pool.getConnection())
        {
            for (OrderLine order : orderLines)
            {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(1, order.getQuantity());
                ps.setInt(2, order.getCupcake().getCupcakeTop().getCupcakeFlavourId());
                ps.setInt(3, order.getCupcake().getCupcakeBottom().getCupcakeFlavourId());
                ps.setInt(4, order.getPrice());
                ps.setInt(5, orderId);

                int rowsAffected = ps.executeUpdate();

                if(rowsAffected != 1)
                {
                    throw new DatabaseException("Fejl ved oprettelse af ny ordre til orderlines");
                }

            }
        } catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    public static void deleteOrder(int order_id, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (var connection = connectionPool.getConnection())
        {
            try (var prepareStatement = connection.prepareStatement(sql))
            {
                prepareStatement.setInt(1, order_id);

                prepareStatement.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Could not delete order from the database");
        }
    }

    public static List<Order> getOrders(String sortby, ConnectionPool pool) throws DatabaseException
    {
        String sql = "SELECT order_id, name, date_placed, date_paid, date_completed, status, orders.user_id, username, balance, role FROM orders LEFT JOIN users ON orders.user_id = users.user_id ORDER BY ?";
        int order_id;
        String name;
        Date date_placed;
        Date date_paid;
        Date date_completed;
        String status;
        int user_id;
        String username;
        int balance;
        String user_role;


        try (Connection connection = pool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, sortby);
            ResultSet rs = ps.executeQuery();
            List<Order> orders = new ArrayList<>();
            while (rs.next())
            {
                order_id = rs.getInt("order_id");
                name = rs.getString("name");
                date_placed = rs.getDate("date_placed");
                date_paid = rs.getDate("date_paid");
                date_completed = rs.getDate("date_completed");
                status = rs.getString("status");
                user_id = rs.getInt("user_id");
                username = rs.getString("username");
                balance = rs.getInt("balance");
                user_role = rs.getString("role");
                if (user_id == 0) {
                    username = "Gæst";
                    user_role = "guest";
                    balance = 0;
                }
                orders.add(new Order(order_id, name, date_placed, date_paid, date_completed,status,
                            new User(user_id, username, user_role, balance)));

            }
            return orders;
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }


    public static List<Order> getOrdersByUserId(User user, String sortby, ConnectionPool dbConnection) throws DatabaseException
    {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1,user.getUserId());
            ps.setString(2, sortby);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                orders.add(new Order(
                        rs.getInt("order_id"),
                        rs.getString("name"),
                        rs.getDate("date_placed"),
                        rs.getDate("date_paid"),
                        rs.getDate("date_completed"),
                        rs.getString("status"),
                        user
                ));
            }
            return orders;
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }
}
