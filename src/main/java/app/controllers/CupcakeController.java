package app.controllers;

import app.entities.CupcakeFlavour;
import app.entities.CupcakeType;
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
    }

    private static void showFrontpage(Context ctx, ConnectionPool dbConnection)
    {
        List<CupcakeFlavour> availableBottomFlavours = CupcakeMapper.getFlavours(CupcakeType.BOTTOM, dbConnection);
        ctx.attribute("bottomflavours", availableBottomFlavours);
        List<CupcakeFlavour> availableTopFlavours = CupcakeMapper.getFlavours(CupcakeType.TOP, dbConnection);
        ctx.attribute("topflavours", availableTopFlavours);
        ctx.render("index.html");
    }
}