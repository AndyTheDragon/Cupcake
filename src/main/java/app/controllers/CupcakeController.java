package app.controllers;

import app.entities.CupcakeFlavour;
import app.entities.CupcakeType;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.CupcakeMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class CupcakeController
{
    public static void addRoutes(Javalin app, ConnectionPool dbConnection)
    {
        app.get("/", ctx ->  showFrontpage(ctx,dbConnection));
        app.get("/newcupcakeflavours", ctx -> ctx.render("newcupcakeflavours.html") );
        app.post("/newcupcakeflavours", ctx -> addCupcakeFlavour(ctx, dbConnection) );
        app.get("/editcupcakeflavour", ctx -> ctx.render("editcupcakeflavour.html") );
        app.post("/editcupcakeflavour", ctx -> editCupcakeFlavours(ctx,dbConnection));
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

        try
        {
            List<CupcakeFlavour> flavours = CupcakeMapper.getAllFlavours(dbConnection);
            ctx.attribute("flavours", flavours);
        } catch (DatabaseException e)
        {
            ctx.attribute("message", e.getMessage());
        }

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
/*
        try
        {
            String cupcakeType = ctx.formParam("cupcaketype");

            if (cupcakeType == null || cupcakeType.isEmpty())
            {
                ctx.attribute("message", "Du skal angive en type");
                ctx.render("newcupcakeflavours.html");
            }

        } catch (DatabaseException e)
        {
            ctx.attribute("message", e.getMessage());
        }*/

    }


}
