package server;

import Service.UserService;
import com.google.gson.Gson;
import model.AuthData;
import model.LoginRequest;
import spark.Request;
import spark.Response;

public class LogoutHandler {
    private UserService userService;
    private Gson gson = new Gson();

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    public Object logout(Request req, Response res) {
        try {

            String authToken = req.headers("authorization");
            if (authToken == null) {
                res.status(401);
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }
            userService.logoutUser(authToken);

            res.status(200);
            return "{}";

        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}
