package server;

import service.UserService;
import com.google.gson.Gson;
import dataaccess.ResponseException;
import spark.Request;
import spark.Response;

public class LogoutHandler {
    private UserService userService;
    private Gson gson = new Gson();

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    public Object logout(Request req, Response res) throws ResponseException {
        try {

            String authToken = req.headers("authorization");
            if (authToken == null) {
                res.status(401);
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }
            try {
                userService.logoutUser(authToken);

                res.status(200);
                return "{}";
            } catch (ResponseException e) {
                res.status(401);
                return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
            }

        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}
