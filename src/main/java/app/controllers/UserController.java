package app.controllers;

import app.exceptions.DatabaseException;
import app.entities.User;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;
import app.persistence.UserMapper;
import java.util.List;

public class UserController
        {
        public static void addRoutes(Javalin app, ConnectionPool pool)
        {
                app.get("/createuser", ctx -> ctx.render("createuser.html") );
                app.post("/createuser", ctx -> createUser(ctx, pool));
                app.get("/login", ctx -> ctx.render("login.html"));
                app.post("/login", ctx -> login(ctx, pool));
                app.get("/logout", ctx -> logout(ctx, pool));
                app.get("/customer", ctx -> redirectUserByRole(ctx, pool));
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
                if(password.length() >= 8 && hasNumber && hasSpecialChar && password.equals(confirmPassword))
                {
                        return true;
                } else
                {
                        ctx.attribute("message", "Kodeordet følger ikke op til krav. Check venligst: \n" +
                                "{" +
                                "Minimumslængde på 8 tegn," +
                                "Inkluder et tal i dit kodeord," +
                                "Inkluder et specielt tegn} ");
                        return false;

                }
        }

        private static void orderCupcake(Context ctx, ConnectionPool pool) {

        }

        private static void customizeCupcake(Context ctx, ConnectionPool pool) {

        }
        public static void login(Context ctx, ConnectionPool connectionPool) {
                String name = ctx.formParam("username");
                String password = ctx.formParam("password");
                try {
                        User user = UserMapper.login(name, password, connectionPool);
                        ctx.sessionAttribute("currentUser", user);
                        ctx.render("index.html");
                } catch (DatabaseException e) {
                        ctx.attribute("message", e.getMessage());
                        ctx.render("index.html");
                } catch (Exception e){
                        ctx.attribute("message", "An unexpected error occurred. Please try again.");
                        ctx.render("index.html");
                }
        }
        public static void logout(Context ctx, ConnectionPool pool){
                //Invalidate session
                ctx.req().getSession().invalidate();
                ctx.redirect("/");
        }

        private static void redirectUserByRole(Context ctx, ConnectionPool pool) {
                User currentUser = ctx.sessionAttribute("currentUser");

                if (currentUser == null) {
                        ctx.redirect("/login");
                        return;
                }

                String role = currentUser.getRole();

                if ("admin".equals(role)) {
                        showAdminPage(ctx, pool);
                } else {
                        showCustomerPage(ctx, currentUser);
                }
        }

        private static void showAdminPage(Context ctx, ConnectionPool pool) {
                try {
                        List<User> allUsers = UserMapper.getAllUsers(pool);
                        ctx.attribute("users", allUsers);
                        ctx.render("admin_users.html");
                } catch (DatabaseException e) {
                        ctx.attribute("message", e.getMessage());
                        ctx.render("error.html");
                }
        }

        private static void showCustomerPage(Context ctx, User currentUser) {
                ctx.attribute("user", currentUser);
                ctx.render("customer_details.html");
        }
}
