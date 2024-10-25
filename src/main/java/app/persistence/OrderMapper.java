package app.persistence;

import app.entities.*;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper
{

    public static int newOrdersToOrdersTable(String name, LocalDate datePlaced, String status, User user, ConnectionPool pool) throws DatabaseException
    {
        String sql = "INSERT INTO orders (name, date_placed, status, user_id) VALUES (?,?,?,?)";
        try (Connection connection = pool.getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setDate(2, Date.valueOf(datePlaced));
            ps.setString(3, status);
            ps.setInt(4, user.getUserId());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected != 1)
            {
                throw new DatabaseException("fejl........");
            }

            try (ResultSet rs = ps.getGeneratedKeys())
            {
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
        } catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    public static void newOrderToOrderLines(int orderId, List<OrderLine> orderLines, ConnectionPool pool) throws DatabaseException
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

    public static List<Order> getOrders(ConnectionPool pool) throws DatabaseException
    {
        String sql = "SELECT order_id, name, date_placed, date_paid, date_completed, status, user_id FROM orders ORDER BY date_placed";
        int order_id;
        String name;
        Date date_placed;
        Date date_paid;
        Date date_completed;
        String status;
        int user_id;

        try (Connection connection = pool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
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
                orders.add(new Order(order_id, name, date_placed, date_paid, date_completed,status, user_id));

            }
            return orders;
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    public static CupcakeFlavour getCupcakeFlavour(String flavourName, CupcakeType cupcakeType, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "SELECT * FROM cupcake_flavours WHERE flavour_name = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {

            ps.setString(1, flavourName);

            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    int flavourId = rs.getInt("flavour_id");
                    int price = rs.getInt("price");
                    String flavourDesc = "";
                    return new CupcakeFlavour(flavourId, price, flavourName, flavourDesc, cupcakeType);
                } else
                {
                    throw new DatabaseException("Flavour not found: " + flavourName);
                }
            }
        } catch (SQLException e)
        {
            throw new DatabaseException("Error getting cupcake flavour from database", e);
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

 public  static Order showOrderDetails (ConnectionPool pool)throws DatabaseException{
        String sql = "SELECT user.user_id, user.username, user.password, user.balance, user.role order.name, order.status, order.date_placed, order.date_paid, order.date_completed, order.order_id, order.user_id " +
                "ol.order_line_id, ol.quantity, bottomf.flavour_name AS bot_flavour, topf.flavour_name AS top_flavour, ol.price  FROM order_lines AS ol " +
                "INNER JOIN cupcake_flavours AS bottomf on bottomf.flavour_id = ol.bottom_flavour INNER JOIN cupcake_flavours AS topf on topf.flavour_id = ol.top_flavour  " +
                "INNER JOIN orders on orders.order_id = ol.order_id INNER JOIN users on users.user_id = orders.user_id ";


     int order_line_id;
        String top_flavour;
        String bottom_flavour;
        int order_lines;

        try (Connection connection = pool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();
            List<OrderLine> orderLines = new ArrayList<>();

            User user = null;
            int orderId = 0;
            String name ="";
            String status = "";


            while (rs.next()) {
                int orderLineId = rs.getInt("order_line_id");
                int quantity = rs.getInt("quantity");
                String topFlavourName = rs.getString("top_flavour");
                String bottomFlavourName = rs.getString("bot_flavour");
                int price = rs.getInt("price");
                int bottomPrice = rs.getInt("bottom_price");
                int cupcakeFlavourId = rs.getInt("buttom_flavour");
                int order_id = rs.getInt("order_id");
                name = rs.getString("name");
                status = rs.getString("status");
                Date date_placed = rs.getDate("date_placed");
                Date date_paid = rs.getDate("date_paid");
                Date date_completed = rs.getDate("date_completed");
                orderId = rs.getInt("order_id");
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                int balance = rs.getInt("balance");
                String role = rs.getString("role");


                CupcakeFlavour topFlavour = new CupcakeFlavour(cupcakeFlavourId,bottomPrice,topFlavourName,"",CupcakeType.TOP);
                CupcakeFlavour bottomFlavour = new CupcakeFlavour(cupcakeFlavourId,bottomPrice,bottomFlavourName,"",CupcakeType.BOTTOM);

                Cupcake cupcake = new Cupcake(topFlavour, bottomFlavour);
                orderLines.add(new OrderLine(order_id,quantity,cupcake,price));
                user = new User(userId,username,password,role,balance);
            }
            return new Order(orderId,name,status, user,orderLines);
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }

    }

}
