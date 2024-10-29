package app.controllers;

import app.entities.Cupcake;
import app.entities.CupcakeFlavour;
import app.entities.CupcakeType;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.CupcakeMapper;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;

public class CupcakeController
{
    public static void addRoutes(Javalin app, ConnectionPool dbConnection)
    {
        app.get("/", ctx ->  showFrontpage(ctx,dbConnection));
        app.get("/newcupcakeflavours", ctx -> ctx.render("newcupcakeflavours.html") );
        app.post("/newcupcakeflavours", ctx -> addCupcakeFlavour(ctx, dbConnection) );
        //app.get("/editcupcakeflavour", ctx -> ctx.render("editcupcakeflavour.html") );
        app.get("/editcupcakeflavour", ctx -> showCupcakeFlavours(ctx,dbConnection));
    }

    private static void deactivateFlavour(Context ctx, ConnectionPool dbConnection)
    {
        String isEnabledString = ctx.formParam("isenabled");
        String flavourIdString = ctx.formParam("flavourId");

        if (isEnabledString == null || isEnabledString.isEmpty())
        {
            ctx.attribute("message", "Indtast en værdi ");
            ctx.render("/");
        }

        boolean isEnabled = Boolean.parseBoolean(isEnabledString);
        int flavourId = Integer.parseInt(flavourIdString);


        try
        {
            CupcakeMapper.deactivateFlavour(isEnabled, flavourId, dbConnection);
        } catch (DatabaseException e)
        {
            ctx.attribute("message", e.getMessage());
            ctx.render("");
        }


    }


    private static void editCupcakeFlavours(Context ctx, ConnectionPool dbConnection)
    {



    }

    private static void addCupcakeFlavour(Context ctx, ConnectionPool pool)
    {


        try
        {
            String flavourName = ctx.formParam("flavoursmag");
            String flavourPriceString = ctx.formParam("flavourpris");
            int flavourPrice1 = Integer.parseInt(flavourPriceString);
            int flavourPrice2 = flavourPrice1 * 100;

            // check boolean-flavour condition
            boolean isTopFlavourAccepted = ctx.formParam("istopflavour") != null;
            boolean isBottomFlavourAccepted = ctx.formParam("isbottomflavour") != null;
            if (isTopFlavourAccepted || isBottomFlavourAccepted)
            {
                CupcakeMapper.addCupcakeFlavour(flavourName, isTopFlavourAccepted, isBottomFlavourAccepted, flavourPrice2, pool);
                ctx.attribute("message", "Din nye flavour er oprettet.");
                ctx.render("newcupcakeflavours.html");
            } else // if false and false
            {
                ctx.attribute("message", "Din nye flavour skal have en top/bund.");
                ctx.render("newcupcakeflavours.html");
            }

        } catch (DatabaseException e)
        {
            ctx.attribute("message", e.getMessage());
            ctx.render("newcupcakeflavours.html");
        }
        catch (NumberFormatException e)
        {
            ctx.attribute("message", "Prisen skal være et tal");
            ctx.render("newcupcakeflavours.html");
        }

    }

    public static void showFrontpage(Context ctx, ConnectionPool dbConnection)
    {
        try
        {
            List<CupcakeFlavour> availableBottomFlavours = CupcakeMapper.getFlavours(CupcakeType.BOTTOM, dbConnection);
            ctx.attribute("bottomflavours", availableBottomFlavours);
            List<CupcakeFlavour> availableTopFlavours = CupcakeMapper.getFlavours(CupcakeType.TOP, dbConnection);
            ctx.attribute("topflavours", availableTopFlavours);
        }
        catch (DatabaseException e)
        {
            ctx.attribute("message", e.getMessage());
        }
        ctx.render("index.html");
    }

    private static void showCupcakeFlavours(Context ctx, ConnectionPool dbConnection)
    {
        User user = ctx.sessionAttribute("currentUser");
        if (user == null)
        {
            ctx.redirect("login.html");
            return;
        }

        String role = user.getRole();

        if ("admin".equals(role))
        {
            try { // henter kun top flavours
                List<CupcakeFlavour> flavours = CupcakeMapper.getFlavours(CupcakeType.TOP, dbConnection);
                ctx.attribute("flavours", flavours);
                ctx.render("editcupcakeflavour.html");
            } catch (DatabaseException e) {
                ctx.attribute("message", e.getMessage());
                ctx.render("error.html");
            }
        } else
        {
            ctx.render("login.html");
        }


    }

}
