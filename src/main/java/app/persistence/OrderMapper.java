package app.persistence;

import app.entities.Order;
import app.entities.CupcakeFlavour;
import app.entities.CupcakeType;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper
{

    public static List<Order> getOrders(ConnectionPool dbConnection) throws DatabaseException
    {
        String sql = "SELECT order_id, name, date_placed, date_paid, date_completed, status, user_id FROM orders ORDER BY date_placed";
        int order_id;
        String name;
        Date date_placed;
        Date date_paid;
        Date date_completed;
        String status;
        int user_id;

        try (Connection connection = dbConnection.getConnection();
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

}
