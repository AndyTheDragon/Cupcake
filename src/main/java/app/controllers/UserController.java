package app.controllers;

import app.exceptions.DatabaseException;
import app.entities.User;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;
import app.persistence.UserMapper;

public class UserController
        {
        public static void addRoutes(Javalin app, ConnectionPool dbConnection)
        {
                app.get("/createuser", ctx -> ctx.render("createuser.html") );
                app.post("/createuser", ctx -> createUser(ctx, dbConnection));
                app.get("/login", ctx -> ctx.render("login.html"));
                app.post("/login", ctx -> doLogin(ctx, dbConnection));
                app.get("/logout", ctx -> doLogout(ctx));
        }

        private static void createUser(Context ctx, ConnectionPool dbConnection)
        {

                String username = ctx.formParam("username");
                String password = ctx.formParam("password");
                String confirmPassword = ctx.formParam("confirmpassword");
            if (password == null || confirmPassword == null)
            {
                    ctx.attribute("message", "Venligst udfyld dit kodeord i begge felter.");
                    ctx.render("createuser.html");
            } else if (passwordCheck(ctx, password, confirmPassword))
            {
                    try
                    {
                            UserMapper.createUser(username, password, dbConnection);
                            ctx.attribute("message", "du er nu oprettet");
                            CupcakeController.showFrontpage(ctx,dbConnection);
                    }
                    catch (DatabaseException e)
                    {
                            ctx.attribute("message", e.getMessage());
                            ctx.render("createuser.html");
                    }
            } else
            {
                    ctx.render("createuser.html");
            }
        }


    private static boolean passwordCheck(Context ctx, String password, String confirmPassword)
    {
        String specialCharacters = "!#¤%&/()=?$§£€-_[]{}";
        String numbers = "1234567890";
        boolean hasSpecialChar = password.chars().anyMatch(ch -> specialCharacters.indexOf(ch) >= 0);
        boolean hasNumber = password.chars().anyMatch(ch -> numbers.indexOf(ch) >= 0);
        //Checks if the passwords match at all. Proceeds with code if true.
        if (!password.equals(confirmPassword))
        {
            ctx.attribute("message", "Kodeord matcher ikke. Prøv igen");
            return false;
        }
        // Check password length and character requirements
        if (password.length() >= 8 && hasNumber && hasSpecialChar)
        {
            return true; // Password meets all criteria
        }
        else
        {
            ctx.attribute("message", "Kodeordet følger ikke op til krav. Check venligst: \n" +
                    "{" +
                    "Minimumslængde på 8 tegn," +
                    "Inkluder et tal i dit kodeord," +
                    "Inkluder et specielt tegn} ");
        }
        return false;
    }

        public static void doLogin(Context ctx, ConnectionPool dbConnection)
        {
                String name = ctx.formParam("username");
                String password = ctx.formParam("password");
                try
                {
                        User user = UserMapper.login(name, password, dbConnection);
                        ctx.sessionAttribute("currentUser", user);
                }
                catch (DatabaseException e)
                {
                        ctx.attribute("message", e.getMessage());
                }
                CupcakeController.showFrontpage(ctx,dbConnection);

        }
        public static void doLogout(Context ctx)
        {
                //Invalidate session
                ctx.req().getSession().invalidate();
                ctx.redirect("/");
        }
}
