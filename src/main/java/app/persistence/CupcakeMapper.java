package app.persistence;

import app.entities.Cupcake;
import app.entities.CupcakeFlavour;
import app.entities.CupcakeType;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CupcakeMapper
{

    public static void addCupcakeFlavour(String flavourName, boolean flavourTop, boolean flavourBottom, int flavourPrice, ConnectionPool pool) throws DatabaseException
    {
        String sql = "INSERT INTO cupcake_flavours (flavour_name, is_top_flavour, is_bottom_flavour, price) VALUES (?,?,?,?)";

        try (Connection connection = pool.getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, flavourName);
            ps.setBoolean(2, flavourTop);
            ps.setBoolean(3, flavourBottom);
            ps.setInt(4, flavourPrice);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1)
            {
                throw new DatabaseException("fejl ved oprettelse af ny flavour");
            }
        }  catch (DatabaseException | SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public static void deactivateFlavour(boolean isEnabled, int flavourId, ConnectionPool pool) throws DatabaseException
    {
        String sql = "UPDATE cupcake_flavours SET is_enabled = ? WHERE flavour_id = ?";

        try (Connection connection = pool.getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setBoolean(1, isEnabled);
            ps.setInt(2, flavourId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1)
            {
                throw new DatabaseException("Fejl ved opdatering af tilgængelighed");
            }
        } catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    public static List<CupcakeFlavour> getAllFlavours(ConnectionPool dbConnection) throws DatabaseException
    {
        List<CupcakeFlavour> flavours = new ArrayList<>();
        int flavourId = 0;
        String flavourName = "";
        boolean isEnabled = true;
        int price = 0;

        String sql = "SELECT flavour_id, flavour_name, is_enabled, price FROM cupcake_flavours";

        try (Connection connection = dbConnection.getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                rs.getInt("flavour_id");
                rs.getString("flavour_name");
                rs.getBoolean("is_enabled");
                rs.getInt("price");
            }

            flavours.add(new CupcakeFlavour(flavourId, price, flavourName, "desc", CupcakeType.TOP));

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved at hente alle flavours fra db", e);
        }
        return flavours;
    }

    public static List<CupcakeFlavour> getFlavours(CupcakeType cupcakeType, ConnectionPool dbConnection) throws DatabaseException
    {
        List<CupcakeFlavour> flavours = new ArrayList<>();
        String sql;
        if (cupcakeType == CupcakeType.BOTTOM)
        {
            sql = "SELECT * FROM cupcake_flavours WHERE is_bottom_flavour = TRUE AND is_enabled = TRUE";
        }
        else if (cupcakeType == CupcakeType.TOP) //dvs. (cupcakeType == CupcakeType.TOP)
        {
            sql = "SELECT * FROM cupcake_flavours WHERE is_top_flavour = TRUE AND is_enabled = TRUE";
        }
        else
        {
            sql = "SELECT * FROM cupcake_flavours WHERE is_bottom_flavour = TRUE AND is_top_flavour = TRUE AND is_enabled = TRUE";
        }

        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                flavours.add(new CupcakeFlavour(rs.getInt("flavour_id"),
                                                rs.getInt("price"),
                                                rs.getString("flavour_name"),
                                                rs.getString("flavour_name"),
                                                cupcakeType));
            }
        } catch (SQLException e)
        {
            throw new DatabaseException("Could not get flavours from database.", e);
        }

        return flavours;
    }


}
