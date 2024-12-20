package app.persistence;

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

    public static void updateFlavourAvailability(boolean isEnabled, int flavourId, ConnectionPool pool) throws DatabaseException
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



    public static List<CupcakeFlavour> getActiveFlavours(CupcakeType cupcakeType, ConnectionPool dbConnection) throws DatabaseException
    {
        List<CupcakeFlavour> flavours = new ArrayList<>();
        String sql;
        if (cupcakeType == CupcakeType.BOTTOM)
        {
            sql = "SELECT * FROM cupcake_flavours WHERE is_bottom_flavour = TRUE AND is_enabled = TRUE";
        }
        else if (cupcakeType == CupcakeType.TOP) //dvs. (cupcakeType == CupcakeType.TOP)
        {
            sql = "SELECT flavour_id, price, flavour_name FROM cupcake_flavours WHERE is_top_flavour = TRUE AND is_enabled = TRUE";
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

    public static List<CupcakeFlavour> getInactiveFlavours(CupcakeType cupcakeType, ConnectionPool dbConnection) throws DatabaseException
    {
        List<CupcakeFlavour> flavours = new ArrayList<>();
        String sql;
        if (cupcakeType == CupcakeType.BOTTOM)
        {
            sql = "SELECT * FROM cupcake_flavours WHERE is_bottom_flavour = TRUE AND is_enabled = FALSE";
        }
        else if (cupcakeType == CupcakeType.TOP) //dvs. (cupcakeType == CupcakeType.TOP)
        {
            sql = "SELECT flavour_id, price, flavour_name FROM cupcake_flavours WHERE is_top_flavour = TRUE AND is_enabled = FALSE";
        }
        else
        {
            sql = "SELECT * FROM cupcake_flavours WHERE is_bottom_flavour = TRUE AND is_top_flavour = TRUE AND is_enabled = FALSE";
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
            sql = "SELECT flavour_id, price, flavour_name FROM cupcake_flavours WHERE is_top_flavour = TRUE AND is_enabled = TRUE";
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
