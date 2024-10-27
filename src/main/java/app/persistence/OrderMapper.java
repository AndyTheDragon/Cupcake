package app.persistence;

import app.entities.*;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper
{

    public static int createOrderInDb(String name, LocalDate datePlaced, LocalDate datePaid, String status, User user, ConnectionPool pool) throws DatabaseException
    {
        String sql = "INSERT INTO orders (name, date_placed, date_paid, status, user_id) VALUES (?,?,?,?,?)";
        try (Connection connection = pool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {

            ps.setString(1, name);
            ps.setDate(2, Date.valueOf(datePlaced));
            if ((datePaid == null))
            {
                ps.setNull(3, Types.DATE);
            } else
            {
                ps.setDate(3, Date.valueOf(datePaid));
            }
            ps.setString(4, status);
            if (user != null)
            {
                ps.setInt(5, user.getUserId());
            }
            else
            {
                ps.setNull(5, Types.INTEGER);
            }

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected != 1)
            {
                throw new DatabaseException("fejl........");
            }

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
            {
                // Return the generated orderId
                return rs.getInt(1);  // assuming orderId is in the first column
            }
            else
            {
                throw new DatabaseException("Kunne ikke hente genereret ordre-id.");
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    public static void createOrderlinesInDb(int orderId, List<OrderLine> orderLines, ConnectionPool pool) throws DatabaseException
    {
        String sql = "INSERT INTO order_lines (quantity, top_flavour, bottom_flavour, price, order_id) VALUES (?,?,?,?,?)";
        try (Connection connection = pool.getConnection())
        {
            for (OrderLine order : orderLines)
            {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(1, order.getQuantity());
                ps.setInt(2, order.getCupcake().getCupcakeTop().getCupcakeFlavourId());
                ps.setInt(3, order.getCupcake().getCupcakeBottom().getCupcakeFlavourId());
                ps.setInt(4, order.getPrice());
                ps.setInt(5, orderId);

                int rowsAffected = ps.executeUpdate();

                if(rowsAffected != 1)
                {
                    throw new DatabaseException("Fejl ved oprettelse af ny ordre til orderlines");
                }

            }
        } catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    public static void deleteOrder(int order_id, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (var connection = connectionPool.getConnection())
        {
            try (var prepareStatement = connection.prepareStatement(sql))
            {
                prepareStatement.setInt(1, order_id);

                prepareStatement.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Could not delete order from the database");
        }
    }

    public static List<Order> getOrders(String sortby, ConnectionPool pool) throws DatabaseException
    {
        String sql = "SELECT order_id, name, date_placed, date_paid, date_completed, status, orders.user_id, username, balance, role FROM orders LEFT JOIN users ON orders.user_id = users.user_id ORDER BY ?";
        int order_id;
        String name;
        Date date_placed;
        Date date_paid;
        Date date_completed;
        String status;
        int user_id;
        String username;
        int balance;
        String user_role;


        try (Connection connection = pool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, sortby);
            ResultSet rs = ps.executeQuery();
            List<Order> orders = new ArrayList<>();
            while (rs.next())
            {
                order_id = rs.getInt("order_id");
                name = rs.getString("name");
                date_placed = rs.getDate("date_placed");
                date_paid = rs.getDate("date_paid");
                date_completed = rs.getDate("date_completed");
                status = rs.getString("status");
                user_id = rs.getInt("user_id");
                username = rs.getString("username");
                balance = rs.getInt("balance");
                user_role = rs.getString("role");
                if (user_id == 0) {
                    username = "Gæst";
                    user_role = "guest";
                    balance = 0;
                }
                orders.add(new Order(order_id, name, date_placed, date_paid, date_completed,status,
                            new User(user_id, username, user_role, balance)));

            }
            return orders;
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }


    public static List<Order> getOrdersByUserId(User user, String sortby, ConnectionPool dbConnection) throws DatabaseException
    {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1,user.getUserId());
            ps.setString(2, sortby);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                orders.add(new Order(
                        rs.getInt("order_id"),
                        rs.getString("name"),
                        rs.getDate("date_placed"),
                        rs.getDate("date_paid"),
                        rs.getDate("date_completed"),
                        rs.getString("status"),
                        user
                ));
            }
            return orders;
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

 public  static Order showOrderDetails (ConnectionPool pool)throws DatabaseException{
        String sql = "SELECT user.user_id, user.username, user.password, user.balance, user.role order.name, order.status, order.date_placed, order.date_paid, order.date_completed, order.order_id, order.user_id " +
                "ol.order_line_id, ol.quantity, bottomf.flavour_name AS bot_flavour, topf.flavour_name AS top_flavour, ol.price  FROM order_lines AS ol " +
                "INNER JOIN cupcake_flavours AS bottomf on bottomf.flavour_id = ol.bottom_flavour INNER JOIN cupcake_flavours AS topf on topf.flavour_id = ol.top_flavour  " +
                "INNER JOIN orders on orders.order_id = ol.order_id INNER JOIN users on users.user_id = orders.user_id ";


     int order_line_id;
        String top_flavour;
        String bottom_flavour;
        int order_lines;

        try (Connection connection = pool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();
            List<OrderLine> orderLines = new ArrayList<>();

            User user = null;
            int orderId = 0;
            String name ="";
            String status = "";


            while (rs.next()) {
                int orderLineId = rs.getInt("order_line_id");
                int quantity = rs.getInt("quantity");
                String topFlavourName = rs.getString("top_flavour");
                String bottomFlavourName = rs.getString("bot_flavour");
                int price = rs.getInt("price");
                int bottomPrice = rs.getInt("bottom_price");
                int cupcakeFlavourId = rs.getInt("buttom_flavour");
                int order_id = rs.getInt("order_id");
                name = rs.getString("name");
                status = rs.getString("status");
                Date date_placed = rs.getDate("date_placed");
                Date date_paid = rs.getDate("date_paid");
                Date date_completed = rs.getDate("date_completed");
                orderId = rs.getInt("order_id");
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                int balance = rs.getInt("balance");
                String role = rs.getString("role");


                CupcakeFlavour topFlavour = new CupcakeFlavour(cupcakeFlavourId,bottomPrice,topFlavourName,"",CupcakeType.TOP);
                CupcakeFlavour bottomFlavour = new CupcakeFlavour(cupcakeFlavourId,bottomPrice,bottomFlavourName,"",CupcakeType.BOTTOM);

                Cupcake cupcake = new Cupcake(topFlavour, bottomFlavour);
                orderLines.add(new OrderLine(order_id,quantity,cupcake,price));
                user = new User(userId,username,password,role,balance);
            }
            return new Order(orderId,name,status, user,orderLines);
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }

    }

}
