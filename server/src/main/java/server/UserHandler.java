package server;

import Service.UserService;
import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class UserHandler {
    private UserService userService;
    private Gson gson = new Gson();

    public UserHandler(UserService userService) {
        this.userService = userService;
    }


    public void registerEndpoints() {
        Spark.post("/user", this::registerUser);
    }

    public Object registerUser(Request req, Response res) {
        try {
        UserData userData = gson.fromJson(req.body(), UserData.class);

        if (userData.username() == null || userData.password() == null || userData.email() == null) {
            res.status(400);
            return gson.toJson(new ErrorResponse("Error: bad request"));
        }

        AuthData userResponse = userService.registerUser(userData);

        if (userResponse != null) {
            res.status(200);
            return gson.toJson(userResponse);
        } else {
            res.status(403);
            return gson.toJson(new ErrorResponse("Error: already taken"));
        }
    } catch (Exception e) {
        res.status(500);
        return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
    }
    }

    private static class ErrorResponse {
        String message;

        ErrorResponse(String message) {
            this.message = message;
        }
    }
}
