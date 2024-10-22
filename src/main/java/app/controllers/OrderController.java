package app.controllers;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class OrderController
{
    public static void addRoutes(Javalin app, ConnectionPool pool)
    {
        app.get("/basket", ctx -> ctx.render("basket.html") );
        app.get("/checkout", ctx -> ctx.render("checkout.html") );
    }

    private static void addOrder(Context ctx, ConnectionPool connectionPool) throws DatabaseException
    {




    }
}
