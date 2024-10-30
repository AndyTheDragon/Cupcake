package app.controllers;

import app.entities.CupcakeFlavour;
import app.entities.CupcakeType;
import app.entities.User;
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
        app.get("/editcupcakeflavour", ctx -> showCupcakeFlavours(ctx,dbConnection));
        app.post("/deactivateflavour", ctx -> deactivateFlavour(ctx, dbConnection) );
        app.post("/activateflavour", ctx -> activateFlavour(ctx, dbConnection) );

    }

    private static void deactivateFlavour(Context ctx, ConnectionPool dbConnection)
    {
        String flavourIdString = ctx.formParam("deactivateflavourid");
        int flavourId = Integer.parseInt(flavourIdString);
        try
        {
            CupcakeMapper.updateFlavourAvailability(false, flavourId, dbConnection);
            ctx.attribute("message", "Flavour er ikke længere muligt at bestille.");
            ctx.render("editcupcakeflavour.html");
        } catch (DatabaseException e)
        {
            ctx.attribute("message", e.getMessage());
            ctx.render("editcupcakeflavour.html");
        }


    }

    private static void activateFlavour(Context ctx, ConnectionPool dbConnection)
    {
        String deactivateFlavourIdString = ctx.formParam("activateflavourid");
        int deactivateFlavourId = Integer.parseInt(deactivateFlavourIdString);

        try
        {
            CupcakeMapper.updateFlavourAvailability(true, deactivateFlavourId, dbConnection);
            ctx.attribute("message", "Flavour er nu muligt og bestille.");
            ctx.render("editcupcakeflavour.html");
        } catch (DatabaseException e)
        {
            ctx.attribute("message", e.getMessage());
            ctx.render("editcupcakeflavour.html");
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
        User user = ctx.sessionAttribute("currentUser");
        if (user == null)
        {
            ctx.redirect("login.html");
            return;
        }

        String role = user.getRole();

        if ("admin".equals(role))
        {
            try {
                List<CupcakeFlavour> inactiveTopFlavours = CupcakeMapper.getInactiveFlavours(CupcakeType.TOP, dbConnection);
                List<CupcakeFlavour> inactiveBottomFlavours = CupcakeMapper.getInactiveFlavours(CupcakeType.BOTTOM, dbConnection);
                List<CupcakeFlavour> inactiveBothFlavours = CupcakeMapper.getInactiveFlavours(CupcakeType.BOTH, dbConnection);

                List<CupcakeFlavour> activeTopFlavours = CupcakeMapper.getActiveFlavours(CupcakeType.TOP, dbConnection);
                List<CupcakeFlavour> activeBottomFlavors = CupcakeMapper.getActiveFlavours(CupcakeType.BOTTOM, dbConnection);
                List<CupcakeFlavour> activeBothFlavours = CupcakeMapper.getActiveFlavours(CupcakeType.BOTH, dbConnection);

                //
                ctx.attribute("activetopflavours", activeTopFlavours);
                ctx.attribute("activebottomflavours", activeBottomFlavors);
                ctx.attribute("activeBothFlavours", activeBothFlavours);
                //
                ctx.attribute("inactivetopflavors", inactiveTopFlavours);
                ctx.attribute("inactivebottomflavours", inactiveBottomFlavours);
                ctx.attribute("inactivebothflavours", inactiveBothFlavours);

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
