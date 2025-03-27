package ui;

import com.google.gson.Gson;
import model.*;
import exception.ResponseException;

import java.io.*;
import java.net.*;
import java.util.Map;


public class ServerFacade {
    private final String serverUrl;
    String authtoken;

    public ServerFacade(String url) {
        serverUrl = url;
    }


    public AuthData register(UserData user) throws ResponseException {
        AuthData auth = this.makeRequest("POST", "/user", user, AuthData.class);
        authtoken = auth.authToken();
        return auth;
    }

    public void login(String username, String password) throws ResponseException {
        var request = Map.of("username", username, "password", password);
        AuthData auth = this.makeRequest("POST", "/session", request, AuthData.class);
        authtoken = auth.authToken();
    }

    public void logout() throws ResponseException {
        var request = Map.of("authtoken", authtoken);
        AuthData auth = this.makeRequest("DELETE","/session", request, AuthData.class);
        authtoken = auth.authToken();
    }

    public void joinGame(String playerColor, int gameID) throws ResponseException {
        JoinRequest request;
        if (playerColor != null) {
            request = new JoinRequest(playerColor, gameID);
        } else {
            request = new JoinRequest(null, gameID);
        }
        this.makeRequest("PUT", "/game", request, null);
    }

    public GameData createGame(String gameName) throws ResponseException {
        var request = Map.of("gameName", gameName);
        return this.makeRequest("POST", "/game", request, GameData.class);
    }

    public GameListResponse listGames() throws ResponseException {
        return this.makeRequest("GET", "/game", null, GameListResponse.class);
    }


    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            http.setRequestProperty("authorization", authtoken);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        int status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    String errorBody = new String(respErr.readAllBytes());
                    throw ResponseException.fromJson(errorBody, status);
                }
            }
            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        if (responseClass == null) {
            return null;
        }
        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader reader = new InputStreamReader(respBody);
            return new Gson().fromJson(reader, responseClass);
        }
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
