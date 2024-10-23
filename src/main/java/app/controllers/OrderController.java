package app.controllers;

import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import app.entities.OrderLine;
import app.entities.Cupcake;
import app.entities.CupcakeFlavour;
import app.entities.CupcakeType;
import app.persistence.ConnectionPool;

import java.util.ArrayList;
import java.util.List;

public class OrderController
{

    public static void addRoutes(Javalin app, ConnectionPool pool){
        app.post("/addcupcake", ctx -> addCupcakeToOrder(ctx, pool));
    }

    private static void addCupcakeToOrder(Context ctx, ConnectionPool pool) {
        List<OrderLine> orderLineList = ctx.sessionAttribute("orderlines");
        if (orderLineList == null) {
            orderLineList = new ArrayList<>();
        }

        String topFlavourName = ctx.formParam("chooseTop");
        String bottomFlavourName = ctx.formParam("chooseBottom");

        String quantityString = ctx.formParam("chooseAmount");
        if (quantityString == null) {
            ctx.sessionAttribute("error", "Please select a quantity.");
            CupcakeController.showFrontpage(ctx,pool);
            return;
        }
        int quantity = Integer.parseInt(quantityString);

        try {
            CupcakeFlavour topFlavour = OrderMapper.getCupcakeFlavour(topFlavourName, CupcakeType.TOP, pool);
            CupcakeFlavour bottomFlavour = OrderMapper.getCupcakeFlavour(bottomFlavourName, CupcakeType.BOTTOM, pool);
            Cupcake cupcake = new Cupcake(topFlavour, bottomFlavour);

            int orderId = 0;
            orderLineList.add(new OrderLine(
                    orderId,
                    quantity,
                    cupcake,
                    cupcake.getPrice()
            ));

            int ordersum = 0;
            for (OrderLine ol : orderLineList) {
                ordersum += ol.getPrice() * ol.getQuantity();
            }

            ctx.sessionAttribute("ordersum", ordersum);
            ctx.sessionAttribute("orderlines", orderLineList);
            ctx.redirect("/");
        } catch (DatabaseException e){
            ctx.attribute("message","Database error: " + e.getMessage());
            CupcakeController.showFrontpage(ctx,pool);
        }
    }

    private static void addOrder(Context ctx, ConnectionPool connectionPool) throws DatabaseException
    {
    }
    private static void showOrder(Context ctx, ConnectionPool pool)
    {

    }

    private static void removeOrder(Context ctx, ConnectionPool pool)
    {

    }
}
