package app.persistence;

import app.entities.Order;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import app.persistence.ConnectionPool;

public class OrderMapper {




        public static Order getOrder(){
            String sql = "SELECT order_id, name, status FROM orders ORDER BY date_placed ASC";
            String cupcake_flavours;
            String order_lines;
            String imageURL;
            int authorId;
            int motivationId;

            try (Connection connection = connectionPool.getConnection())
            {
                try (PreparedStatement ps = connection.prepareStatement(sql))
                {
                    ResultSet rs = ps.executeQuery();
                    if (rs.next())
                    {
                        motivationId = rs.getInt("id");
                        title = rs.getString("title");
                        text = rs.getString("text");
                        imageURL = rs.getString("image_url");
                        authorId = rs.getInt("author_id");
                        return new Motivation(motivationId, title, text, imageURL, authorId);
                    }
                }
            } catch (SQLException e)
            {
                throw new DatabaseException(e.getMessage());
            }
            return null;

        }


}
