package app.controllers;

import app.entities.Cupcake;
import app.entities.CupcakeFlavour;
import app.entities.CupcakeType;
import app.entities.OrderLine;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;

public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool pool){
        app.post("/addcupcake", ctx -> addCupcakeToOrder(ctx, pool));
    }

    private static void addCupcakeToOrder(Context ctx, ConnectionPool pool){
        List<OrderLine> orderLineList = ctx.sessionAttribute("orderlines");

        if (orderLineList == null) {
            orderLineList = new ArrayList<>();
        }

        String topFlavourName = ctx.formParam("chooseTop");
        String bottomFlavourName = ctx.formParam("chooseBottom");

        String quantityString = ctx.formParam("chooseAmount");
        if (quantityString == null) {
            ctx.sessionAttribute("error", "Please select a quantity.");
            ctx.redirect("/");
            return;
        }
        int quantity = Integer.parseInt(quantityString);

        try {
            CupcakeFlavour topFlavour = OrderMapper.getCupcakeFlavour(topFlavourName, CupcakeType.TOP);
            CupcakeFlavour bottomFlavour = OrderMapper.getCupcakeFlavour(bottomFlavourName, CupcakeType.TOP);
            Cupcake cupcake = new Cupcake(topFlavour, bottomFlavour);

            int orderId = 0;
            orderLineList.add(new OrderLine(
                    orderId,
                    quantity,
                    cupcake,
                    cupcake.getPrice()
            ));

            ctx.sessionAttribute("orderlines", orderLineList);
            ctx.redirect("/");
        } catch (DatabaseException e){
            ctx.sessionAttribute("error", "Database error: " + e.getMessage());
            ctx.redirect("/");
        }
    }

    private static void addOrder(Context ctx, ConnectionPool pool){

    }

    private static void showOrder(Context ctx, ConnectionPool pool){

    }

    private static void removeOrder(Context ctx, ConnectionPool pool){

    }
}
