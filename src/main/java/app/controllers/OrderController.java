package app.controllers;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper; // Sørg for at du har importeret din OrderMapper
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context; // Den korrekte Context import fra Javalin
import org.jetbrains.annotations.NotNull;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderController
{

    public static void addRoutes(Javalin app, ConnectionPool pool)
    {
        app.get("/ordrehistory", ctx -> showOrderHistory(ctx, pool));
        app.post("/addcupcake", ctx -> addCupcakeToOrder(ctx, pool));
        app.get("/order/delete", ctx -> deleteOrder(ctx,pool));
        app.post("/addcupcake", ctx -> addCupcakeToBasket(ctx, pool));
        app.get("/basket", ctx -> ctx.render("basket.html") );
        app.get("/checkout", ctx -> ctx.render("checkout.html") );
        app.post("/checkout", ctx -> checkout(ctx, pool) );
        app.get("/confirmation", ctx -> ctx.render("confirmation.html") );
        app.post("/confirmation", ctx -> confirmation(ctx, pool));
    }

    private static void confirmation(Context ctx, ConnectionPool pool)
    {
        ctx.attribute("message", "Din ordre er gennemført.");
        ctx.redirect("/");
    }

    private static void checkout(Context ctx, ConnectionPool pool) throws DatabaseException
    {
        // hvis brugeren er logget ind - betal med store credit

        // til orders
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        LocalDate datePlaced = LocalDate.now();
        String status = "Ordren er placeret";
        // til order_lines
        List<OrderLine> orderLineList = ctx.sessionAttribute("orderlines");
        if (orderLineList == null || orderLineList.isEmpty())
        {
            ctx.attribute("message", "din kurv er tom");
            ctx.render("/basket");
            return;
        }


        try
        {
            User user = UserMapper.login(username, password, pool);
            ctx.sessionAttribute("currentUser", user);
            ctx.render("checkout.html");

            // opretter ordren i orders & orderlines
            OrderMapper.newOrdersToOrdersTable(username, datePlaced, status, user, pool); // egentlig er det user_id
            OrderMapper.newOrderToOrderLines(orderLineList, pool);

        } catch (DatabaseException e)
        {
            ctx.attribute("message", e.getMessage());
            ctx.render("basket.html");
        }


        // hvis brugeren er gæst - afhent i butikken

    }

    private static void addCupcakeToBasket(Context ctx, ConnectionPool pool) {
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
                    cupcake.getPrice()*quantity
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
        // Render Thymeleaf-skabelonen
        ctx.attribute("orders", orders);
        ctx.render("/ordrehistory.html");
    }

    private static void deleteOrder(Context ctx, ConnectionPool pool)
    {
        try
        {
            int orderId = Integer.parseInt(ctx.queryParam("order_id"));
            OrderMapper.deleteOrder(orderId,pool);
            ctx.attribute("message","Order deleted");
            showOrderHistory(ctx,pool);
        }
        catch (NumberFormatException e)
        {
            ctx.attribute("message","Invalid order id.");
            showOrderHistory(ctx, pool);
        }
        catch (DatabaseException e)
        {
            ctx.attribute("message","Database error: " + e.getMessage());
            showOrderHistory(ctx, pool);
        }
    }

}
