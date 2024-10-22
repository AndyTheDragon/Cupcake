package app.controllers;

import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;
import app.persistence.UserMapper;

public class UserController
        {
        public static void addRoutes(Javalin app, ConnectionPool pool)
        {
                app.get("/createuser", ctx -> ctx.render("createuser.html") );
                app.post("/createuser", ctx -> createUser(ctx, pool));

        }

        private static void createUser(Context ctx, ConnectionPool pool) throws DatabaseException
        {
                String username = ctx.formParam("username");
                String password = ctx.formParam("password");
                String confirmPassword = ctx.formParam("confirmpassword");
            if (password == null || confirmPassword == null)
            {
                    ctx.attribute("message", "Venligst udfyld dit kodeord i begge felter.");
            } else
            {
                    passwordCheck(ctx, password, confirmPassword);
            }

            UserMapper.createUser(username, password, pool);
            ctx.redirect("/");


        }

        private static void passwordCheck(Context ctx, String password, String confirmPassword)
        {
                CharSequence specialCharacters = "!#¤%&/()=?";
                CharSequence numbers = "1234567890";

                if(password.length() >= 8 && password.contains(specialCharacters) && password.contains(numbers) && password.equals(confirmPassword))
                {
                        System.out.println();
                } else
                {
                        ctx.attribute("message", "Kodeordet følger ikke op til krav. Check venligst: \n" +
                                "{" +
                                "Minimumslængde på 8 tegn," +
                                "Inkluder et tal i dit kodeord," +
                                "Inkluder et specielt tegn} ");

                }
        }

        private static void orderCupcake(Context ctx, ConnectionPool pool) {

        }

        private static void customizeCupcake(Context ctx, ConnectionPool pool) {

        }
}
