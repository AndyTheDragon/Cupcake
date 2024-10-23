package app.persistence;

import app.entities.Order;
import app.entities.CupcakeFlavour;
import app.entities.CupcakeType;
import app.entities.OrderLine;
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

    public static void newOrdersToOrdersTable(String name, LocalDate datePlaced, LocalDate datePaid, LocalDate dateCompleted, String status, ConnectionPool pool) throws DatabaseException
    {
        String sql = "INSERT INTO orders (name, date_placed, date_paid, date_completed, status) VALUES (?,?,?,?,?)";
        try (Connection connection = pool.getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            ps.setDate(2, Date.valueOf(datePlaced));
            ps.setDate(3, Date.valueOf(datePaid));
            ps.setDate(4, Date.valueOf(dateCompleted));
            ps.setString(5, status);

            int rowsAffected = ps.executeUpdate();
            if(rowsAffected != 1)
            {
                throw new DatabaseException("Fejl ved oprettelse af ny ordre");
            }

        } catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    public static void newOrderToOrderLines(int orderId, List<OrderLine> orderLines, ConnectionPool pool) throws DatabaseException
    {
        String sql = "INSERT INTO order_lines (quantity, top_flavour, bottom_flavour, price) VALUES (?,?,?,?,?)";
        try (Connection connection = pool.getConnection())
        {
            for (OrderLine order : orderLines)
            {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(1, orderId);
                ps.setInt(2, order.getQuantity());
                ps.setString(3, order.getTopFlavour());
                ps.setString(4, order.getBottomFlavour());
                ps.setInt(5, order.getPrice());
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
                order_id = rs.getInt("Order_id");
                name = rs.getString("Name");
                date_placed = rs.getDate("Date placed");
                date_paid = rs.getDate("Date paid");
                date_completed = rs.getDate("Date completed");
                status = rs.getString("Status");
                user_id = rs.getInt("User_id");
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


}
