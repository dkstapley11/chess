package exception;

import com.google.gson.Gson;

import java.util.Map;

public class ResponseException extends Exception {
    final private int statusCode;

    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", statusCode));
    }

    public static ResponseException fromJson(String json, int status) {
        Map errorMap = new Gson().fromJson(json, Map.class);
        String message = (String) errorMap.get("message");
        if (message == null || message.isEmpty()) {
            message = "Unknown error";
        }
        return new ResponseException(status, message);
    }

    public int statusCode() {
        return statusCode;
    }
}
