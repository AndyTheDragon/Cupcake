package app.controllers;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderController
{

    public static void addRoutes(Javalin app, ConnectionPool dbConnection)
    {
        app.get("/ordrehistory", ctx -> showOrderHistory(ctx, dbConnection));
        app.get("/order/delete", ctx -> deleteOrder(ctx,dbConnection));
        app.get("/order/details/{id}", ctx -> showOrderHistory(ctx, dbConnection));
        app.get("/order/finish/{id}", ctx -> showOrderHistory(ctx, dbConnection));
        app.post("/addcupcake", ctx -> addCupcakeToBasket(ctx, dbConnection));
        app.get("/removecupcake", OrderController::removeCupcakeFromBasket);
        app.get("/basket", ctx -> showBasket(null,ctx) );
        app.get("/checkout", ctx -> showBasket(null,ctx) );
        app.post("/checkout", ctx -> checkout(ctx, dbConnection) );
    }


    private static void showBasket(@Nullable String message, Context ctx)
    {
        ctx.attribute("message", message);
        ctx.render("basket.html");
    }

    private static void checkout(Context ctx, ConnectionPool dbConnection)
    {
        List<OrderLine> orderLineList = ctx.sessionAttribute("orderlines");
        int orderSum = (ctx.sessionAttribute("ordersum") != null) ? ctx.sessionAttribute("ordersum") : 0;
        String pickupName = ctx.formParam("pickupname");
        String paymentMethod = ctx.formParam("paymentmethod");
        if (orderLineList == null || orderLineList.isEmpty())
        {
            showBasket("din kurv er tom", ctx);
            return;
        }
        if (pickupName == null || paymentMethod == null)
        {
            showBasket("Navn eller paymentmethod er tom", ctx);
            return;
        }

        User user = ctx.sessionAttribute("currentUser");
        LocalDate datePlaced = LocalDate.now();
        LocalDate datePaid = null;
        String status = "Ordren er placeret";

        if (paymentMethod.equals("user") && user != null)
        {
            if (user.getBalance() > orderSum)
            {
                try
                {
                    user.buy(orderSum);
                    //UserMapper.payForOrder(user,dbConnection);
                    UserMapper.updateCustomerBalance(user.getBalance(), user.getUserId(), dbConnection);
                    datePaid = LocalDate.now();
                    status = "Ordren er betalt";

                }
                catch (DatabaseException e)
                {
                    showBasket(e.getMessage(), ctx);
                    return;
                }
            }
            else
            {
                showBasket("Du har ikke penge nok på kontoen.", ctx);
                return;
            }

        }
        else if (paymentMethod.equals("guest"))
        {
            user = null;
        }
        else
        {
            showBasket("Du er ikke logget ind, og kan derfor kun bestille til afhentning i butikken.", ctx);
            return;
        }

        try
        {
            // opretter ordren i orders & orderlines
            int orderId = OrderMapper.createOrderInDb(pickupName, datePlaced, datePaid, status, user, dbConnection);
            OrderMapper.createOrderlinesInDb(orderId, orderLineList, dbConnection);
            ctx.attribute("message", "Ordre er placeret med ordrenummer: " + orderId);
            ctx.sessionAttribute("orderlines", null);
            ctx.sessionAttribute("ordersum", null);
            ctx.render("confirmation.html");

        } catch (DatabaseException e)
        {
            showBasket(e.getMessage(), ctx);
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

    private static void removeCupcakeFromBasket(Context ctx)
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
