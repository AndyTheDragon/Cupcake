package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class UserController {
        public static void addRoute(Javalin app, ConnectionPool pool) {
                app.get("/basket", ctx -> ctx.render("basket.html") );
                app.get("/checkout", ctx -> ctx.render("checkout.html") );
                app.post("/checkout", ctx -> pay(ctx, pool) );

        }

        private static void pay(Context ctx, ConnectionPool connectionPool) {

        }

        private static void orderCupcake(Context ctx, ConnectionPool pool) {

        }

        private static void customizeCupcake(Context ctx, ConnectionPool pool) {

        }
}
