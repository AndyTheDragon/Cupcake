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

    public static List<CupcakeFlavour> getFlavours(CupcakeType cupcakeType, ConnectionPool dbConnection) throws DatabaseException
    {
        List<CupcakeFlavour> flavours = new ArrayList<>();
        String sql;
        if (cupcakeType == CupcakeType.BOTTOM)
        {
            sql = "SELECT * FROM cupcake_flavours WHERE is_bottom_flavour = TRUE";
        }
        else //dvs. (cupcakeType == CupcakeType.TOP)
        {
            sql = "SELECT * FROM cupcake_flavours WHERE is_top_flavour = TRUE";
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
