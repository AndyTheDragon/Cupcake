package app.controllers;

import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class OrderController
{
    public static void addRoutes(Javalin app, ConnectionPool pool)
    {
        app.get("/basket", ctx -> ctx.render("basket.html") );
        app.get("/checkout", ctx -> ctx.render("checkout.html") );
    }

    private static void addOrder(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        String q = ctx.formParam("chooseAmount");
        int quantity = Integer.parseInt(q);
        String topFlavour = ctx.formParam("topflavour");
        String bottomFlavour = ctx.formParam("bottomFlavour");
        String p = ctx.formParam("price");
        int price = Integer.parseInt(p);
        String id = ctx.formParam("orderid");
        int orderId = Integer.parseInt(id);

        try
        {
            OrderMapper.newOrder(quantity, topFlavour, bottomFlavour, price, orderId, connectionPool);
            ctx.attribute("message", "Din ordre er tilføjet til kurven.");
        } catch (DatabaseException e)
        {
            throw new DatabaseException(e.getMessage());
        }


    }
}
