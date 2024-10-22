package app.persistence;

import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderMapper
{
    public static void newOrder(int quantity, String top_flavour, String bottom_flavour, int price, int orderId, ConnectionPool pool) throws DatabaseException {
        String sql = "insert into order_lines (quantity, top_flavour, bottom_flavour, price, order_id) " +
                "values (?,?,?,?,?)";

        try (Connection connection = pool.getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, quantity);
            ps.setString(2, top_flavour);
            ps.setString(3, bottom_flavour);
            ps.setInt(4, price);
            ps.setInt(5, orderId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1)
            {
                throw new DatabaseException("Fejl ved oprettelse af ny ordre");
            }

        } catch(SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }
}
