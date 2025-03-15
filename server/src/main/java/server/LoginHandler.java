package server;

import Service.UserService;
import com.google.gson.Gson;
import dataAccess.ResponseException;
import model.AuthData;
import model.LoginRequest;
import spark.Request;
import spark.Response;

public class LoginHandler {
    private UserService userService;
    private Gson gson = new Gson();

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    public Object login(Request req, Response res) {
        try {
            LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);

            if (loginRequest.username() == null || loginRequest.password() == null) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Error: bad request"));
            }

            try {
                AuthData userResponse = userService.loginUser(loginRequest.username(), loginRequest.password());
                res.status(200);
                return gson.toJson(userResponse);
            } catch (ResponseException e) { // Catch authentication failure
                res.status(401);
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }

        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}
