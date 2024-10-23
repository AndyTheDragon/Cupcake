package app.persistence;

import app.entities.Order;
import app.exceptions.DatabaseException;

import java.sql.*;

import app.persistence.ConnectionPool;

public class OrderMapper {




        public static Order getOrder(ConnectionPool pool){
            String sql = "SELECT order_id, name, date_placed, date_paid, date_completed, status, user_id FROM orders ORDER BY date_placed ASC\n";
            int order_id;
            String name;
            Date date_placed;
            Date date_paid;
            Date date_completed;
            String status;
            int user_id;


            try (Connection connection = pool.getConnection())
            {
                try (PreparedStatement ps = connection.prepareStatement(sql))
                {
                    ResultSet rs = ps.executeQuery();
                    if (rs.next())
                    {
                        order_id = rs.getInt("Order_id");
                        name = rs.getString("Name");
                        date_placed = rs.getDate("Date placed");
                        date_paid = rs.getDate("Date paid");
                        date_completed = rs.getDate("Date completed");
                        status = rs.getString("Status");
                        user_id = rs.getInt("User_id");
                        return new Order(order_id, name, date_placed, date_paid, date_completed,status, user_id);
                    }
                }
            } catch (SQLException e)
            {

                throw new DatabaseException(e.getMessage());


            }

            return null;

        }


}
