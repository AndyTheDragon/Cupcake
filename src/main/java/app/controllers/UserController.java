package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class UserController {
        public static void addRoute(Javalin app, ConnectionPool pool) {
                app.post("createuser", ctx -> createUser(ctx, pool));

        }

        private static void createUser(Context ctx, ConnectionPool pool) {
                String username = ctx.formParam("username");
                String password = ctx.formParam("password");
                String confirmPassword = ctx.formParam("confirmpassword");

                if (password.equals(confirmPassword)) {
                        UserMapper.createUser(username, password, confirmPassword);
                } else {
                        ctx.attribute("message", "Dit kodeord stemmer ikke overens. Prøv igen");
                        ctx.render("createuser.html");
                }

        }

        private static void orderCupcake(Context ctx, ConnectionPool pool) {

        }

        private static void customizeCupcake(Context ctx, ConnectionPool pool) {

        }
}
