package app.persistence;

import app.entities.CupcakeFlavour;
import app.entities.CupcakeType;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper {

    public static CupcakeFlavour getCupcakeFlavour(String flavourName, CupcakeType cupcakeType, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM cupcake_flavours WHERE flavour_name = ? AND ";

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
