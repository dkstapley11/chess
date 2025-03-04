package server;

import Service.UserService;
import com.google.gson.Gson;
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

            AuthData userResponse = userService.loginUser(loginRequest.username(), loginRequest.password());

            if (userResponse == null) {
                res.status(401);
                return gson.toJson(new ErrorResponse("Error: invalid credentials"));
            }

            res.status(200);
            return gson.toJson(userResponse);

        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}
