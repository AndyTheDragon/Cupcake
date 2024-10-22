package app.persistence;

import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Locale;

public class OrderMapper
{
    public static void newOrder(String name, Date datePlaced, Date datePaid, Date dateCompleted,
                                String status, int userId, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "insert into orders (name, date_placed, date_paid, date_completed, status, user_id)" +
                " values (?,?,?,?,?,?)";

        try (Connection connection = connectionPool.getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            ps.setDate(2, datePlaced);
            ps.setDate(3, datePaid);
            ps.setDate(4, dateCompleted);
            ps.setString(5, status);
            ps.setInt(6, userId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1)
            {
                throw new DatabaseException("Fejl ved oprettelse af ny ordre");
            }

        } catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }
}
