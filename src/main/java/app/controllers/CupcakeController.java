package app.controllers;

import app.entities.CupcakeFlavour;
import app.entities.CupcakeType;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.CupcakeMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import javax.xml.crypto.Data;
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
        int flavourPrice = Integer.parseInt(flavourPriceString);

        try
        {
            CupcakeMapper.addCupcakeFlavour(flavourName, flavourPrice, pool);
            ctx.attribute("message", "Din nye flavour er oprettet.");
            ctx.render("newcupcakeflavours.html");
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
