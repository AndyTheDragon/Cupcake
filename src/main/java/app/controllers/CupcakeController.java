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
    }

    private static void addCupcakeFlavour(Context ctx, ConnectionPool pool) throws DatabaseException
    {
        String flavourName = ctx.formParam("flavoursmag");
        String flavourPriceString = ctx.formParam("flavourpris");
        int flavourPrice1 = Integer.parseInt(flavourPriceString);
        int flavourPrice2 = flavourPrice1 * 100;

        // check boolean-flavour condition
        boolean isTopFlavourAccepted = ctx.formParam("istopflavour") != null;
        boolean isBottomFlavourAccepted = ctx.formParam("isbottomflavour") != null;

        try
        {
            if(isTopFlavourAccepted && isBottomFlavourAccepted) // if true and true
            {
                CupcakeMapper.addCupcakeFlavour(flavourName, true, true, flavourPrice2, pool);
                ctx.attribute("message", "Din nye flavour er oprettet.");
                ctx.render("newcupcakeflavours.html");
            } else if (isTopFlavourAccepted || isBottomFlavourAccepted) // if true/false
            {
                CupcakeMapper.addCupcakeFlavour(flavourName, isTopFlavourAccepted, isBottomFlavourAccepted, flavourPrice2, pool);
                ctx.attribute("message", "Din nye flavour er oprettet.");
                ctx.render("newcupcakeflavours.html");
            } else // if false and false
            {
                CupcakeMapper.addCupcakeFlavour(flavourName, isTopFlavourAccepted, isBottomFlavourAccepted, flavourPrice2, pool);
                ctx.attribute("message", "Din nye flavour er oprettet.");
                ctx.render("newcupcakeflavours.html");
            }

        } catch (DatabaseException e)
        {
            throw new DatabaseException(e.getMessage());
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
}
