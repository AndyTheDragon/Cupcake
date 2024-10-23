package app.controllers;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper; // Sørg for at du har importeret din OrderMapper
import io.javalin.Javalin;
import io.javalin.http.Context; // Den korrekte Context import fra Javalin

import java.util.ArrayList;
import java.util.List;

public class OrderController
{

    public static void addRoutes(Javalin app, ConnectionPool pool)
    {
        app.get("/ordrehistory", ctx -> showOrderHistory(ctx, pool));
    }

    private static void showOrderHistory(Context ctx, ConnectionPool pool)
    {
        List<Order> orders = new ArrayList<>();
        try
        {

            orders = OrderMapper.getOrders(pool);
        }
        catch (DatabaseException e)
        {

            ctx.attribute("message","Noget gik galt. " + e.getMessage());
        }


        ctx.attribute("orders", orders);

        // Render Thymeleaf-skabelonen
        ctx.render("/ordrehistory.html");
    }
}
