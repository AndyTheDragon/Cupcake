package app.controllers;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper; // Sørg for at du har importeret din OrderMapper
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context; // Den korrekte Context import fra Javalin
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderController
{

    public static void addRoutes(Javalin app, ConnectionPool dbConnection)
    {
        app.get("/ordrehistory", ctx -> showOrderHistory(ctx, dbConnection));
        app.get("/order/delete", ctx -> deleteOrder(ctx,dbConnection));
        app.post("/addcupcake", ctx -> addCupcakeToBasket(ctx, dbConnection));
        app.get("/removecupcake", ctx -> removeCupcakeFromBasket(ctx,dbConnection));
        app.get("/basket", ctx -> ctx.render("basket.html") );
        app.get("/checkout", ctx -> ctx.render("checkout.html") );
        app.post("/checkout", ctx -> checkout(ctx, dbConnection) );
        app.get("/confirmation", ctx -> confirmation(ctx, dbConnection) );
        //app.post("/confirmation", ctx -> confirmation(ctx, dbConnection));
    }

    private static void confirmation(Context ctx, ConnectionPool pool)
    {
        ctx.attribute("message", "Din ordre er gennemført.");
        ctx.render("confirmation.html");
    }

    private static void checkout(Context ctx, ConnectionPool pool)
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
            int orderId = OrderMapper.newOrdersToOrdersTable(username, datePlaced, status, user, pool);
            OrderMapper.newOrderToOrderLines(orderId, orderLineList, pool);
            ctx.render("confirmation.html");

        } catch (DatabaseException e)
        {
            ctx.attribute("message", e.getMessage());
            ctx.render("/basket");
        }


        // hvis brugeren er gæst - afhent i butikken
        if (ctx.sessionAttribute("currentUser") == null)
        {
            try
            {
                int orderId = OrderMapper.newOrdersToOrdersTable(username, datePlaced, status, null, pool);
                OrderMapper.newOrderToOrderLines(orderId, orderLineList, pool);
                ctx.render("confirmation.html");
                ctx.attribute("message", "Din ordre er gennemført og du kan afhente i butikken");

            } catch (DatabaseException e)
            {
                ctx.attribute("message", e.getMessage());
                ctx.render("basket.html");
            }
        }


    }

    private static void addCupcakeToBasket(Context ctx, ConnectionPool pool)
    {
        List<OrderLine> orderLineList = ctx.sessionAttribute("orderlines");
        if (orderLineList == null)
        {
            orderLineList = new ArrayList<>();
        }

        String topFlavourName = ctx.formParam("chooseTop");
        String bottomFlavourName = ctx.formParam("chooseBottom");

        String quantityString = ctx.formParam("chooseAmount");
        if (quantityString == null)
        {
            ctx.sessionAttribute("error", "Please select a quantity.");
            CupcakeController.showFrontpage(ctx,pool);
            return;
        }
        int quantity = Integer.parseInt(quantityString);

        try
        {
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
                ordersum += ol.getPrice();
            }

            ctx.sessionAttribute("ordersum", ordersum);
            ctx.sessionAttribute("orderlines", orderLineList);
            ctx.redirect("/");
        }
        catch (DatabaseException e)
        {
            ctx.attribute("message","Database error: " + e.getMessage());
            CupcakeController.showFrontpage(ctx,pool);
        }
    }

    private static void showOrderHistory(Context ctx, ConnectionPool pool)
    {
        List<Order> orders = new ArrayList<>();
        String sortby = ctx.formParam("sort");
        try
        {
            if (!(sortby==null || sortby.equals("name") || sortby.equals("status") || sortby.equals("date_created") || sortby.equals("date_paid") || sortby.equals("date_completed")))
            {
                sortby = "order_id";
            }
            orders = OrderMapper.getOrders(sortby, pool);
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

    private static void removeCupcakeFromBasket(Context ctx, ConnectionPool dbConnection)
    {
        String lineId = ctx.queryParam("line_id");
        List<OrderLine> orderLineList = ctx.sessionAttribute("orderlines");
        if (lineId != null && orderLineList != null) {
            int orderLineId = Integer.parseInt(lineId);
            orderLineList.remove(orderLineId);
        }
        ctx.render("/basket.html");
    }

}
