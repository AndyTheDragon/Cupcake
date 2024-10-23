package app.persistence;

import app.entities.CupcakeFlavour;
import app.entities.CupcakeType;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    public static CupcakeFlavour getCupcakeFlavour(String flavourName, CupcakeType cupcakeType, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM cupcake_flavours WHERE flavour_name = ? AND ";

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1)
            {
                throw new DatabaseException("Fejl ved oprettelse af ny ordre");
            }
        if (cupcakeType == CupcakeType.TOP) {
            sql += "if_top_flavour = true";
        } else {
            sql += "is_bottom_flavour = true";
        }

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, flavourName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int flavourId = rs.getInt("flavour_id");
                    int price = rs.getInt("price");
                    String flavourDesc = "";
                    return new CupcakeFlavour(flavourId, price, flavourName, flavourDesc, cupcakeType);
                } else {
                    throw new DatabaseException("Flavour not found: " + flavourName);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting cupcake flavour from database", e);
        }
    }
}
