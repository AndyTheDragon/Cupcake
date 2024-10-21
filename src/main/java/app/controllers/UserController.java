package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;
import app.persistence.UserMapper;

public class UserController {
        public static void addRoute(Javalin app, ConnectionPool pool) {
                //app.post("/createuser", ctx -> ctx.render("index.html") );
                app.get("/createuser", ctx -> ctx.render("createuser.html") );
                app.post("/createuser", ctx -> createUser(ctx, pool));

        }

        private static void createUser(Context ctx, ConnectionPool pool) {
                String username = ctx.formParam("username");
                String password = ctx.formParam("password");
                String email = ctx.formParam("email");
                String confirmPassword = ctx.formParam("confirmpassword");

                if (password.equals(confirmPassword)) {
                        UserMapper.createUser(username, password, pool);
                } else {
                        ctx.attribute("message", "Dit kodeord stemmer ikke overens. Prøv igen");
                        ctx.render("createuser.html");
                }
                ctx.redirect("/");

        }

        private static void orderCupcake(Context ctx, ConnectionPool pool) {

        }

        private static void customizeCupcake(Context ctx, ConnectionPool pool) {

        }
}
