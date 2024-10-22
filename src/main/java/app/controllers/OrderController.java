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
        int quantity = Integer.parseInt(ctx.formParam("chooseAmount"));

        CupcakeFlavour topFlavour = new CupcakeFlavour(
                OrderMapper.getFlavourId(topFlavourName),
                OrderMapper.getFlavourPrice(topFlavourName),
                topFlavourName,
                "",
                CupcakeType.TOP);
        CupcakeFlavour bottomFlavour = new CupcakeFlavour(
                OrderMapper.getFlavourId(bottomFlavourName),
                OrderMapper.getFlavourPrice(bottomFlavourName),
                bottomFlavourName,
                "",
                CupcakeType.BOTTOM);

        int orderLineId;
        if(OrderMapper.getOrderId(bottomFlavourName) == null){
            orderLineId = 1;
        }else{
            orderLineId = OrderMapper.getLastOrderId(bottomFlavourName)+1;
        }

        orderLineList.add(new OrderLine(
                orderLineId,
                quantity,
                new Cupcake(topFlavour,bottomFlavour),
                (OrderMapper.getPrice(topFlavourName) + OrderMapper.getPrice(bottomFlavourName)) * quantity));

        ctx.sessionAttribute("orderlines", orderLineList);
    }

    private static void addOrder(Context ctx, ConnectionPool pool){

    }

    private static void showOrder(Context ctx, ConnectionPool pool){

    }

    private static void removeOrder(Context ctx, ConnectionPool pool){

    }
}
