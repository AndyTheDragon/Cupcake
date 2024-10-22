package app.controllers;

import app.entities.User;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class UserController
{
        public static void addRoute(Javalin app, ConnectionPool pool)
        {

        }

        private static void orderCupcake(Context ctx, ConnectionPool pool)
        {

        }

        private static void customizeCupcake(Context ctx, ConnectionPool pool)
        {

        }
        public static void login(Context ctx, ConnectionPool connectionPool) {
                String name = ctx.formParam("username");
                String password = ctx.formParam("password");
                try {
                        User user = UserMapper.login(name, password, connectionPool);
                        ctx.sessionAttribute("currentUser", user);
                        ctx.render("tasks.html");
                } catch (DataBaseException e) {
                        ctx.attribute("message", e.getMessage());
                        ctx.render("index.html");
                }
        }
}
