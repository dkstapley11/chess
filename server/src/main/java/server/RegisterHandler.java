package server;

import Service.UserService;
import com.google.gson.Gson;
import dataAccess.ResponseException;
import model.AuthData;
import model.UserData;
import spark.Request;
import spark.Response;

public class RegisterHandler {

    private UserService userService;
    private Gson gson = new Gson();

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    public Object register(Request req, Response res) {
        try {
            UserData userData = gson.fromJson(req.body(), UserData.class);

            if (userData.username() == null || userData.password() == null || userData.email() == null) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Error: bad request"));
            }

            try {
                AuthData userResponse = userService.registerUser(userData);
                res.status(200);
                return gson.toJson(userResponse);
            } catch (ResponseException e) { // Catch user already exists error
                res.status(403);
                return gson.toJson(new ErrorResponse("Error: already taken"));
            }

        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}
