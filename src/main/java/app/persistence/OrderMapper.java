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


}
