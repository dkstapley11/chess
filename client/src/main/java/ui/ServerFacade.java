package ui;

import com.google.gson.Gson;
import model.*;
import Exception.ResponseException;

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
        var request = new Gson().toJson(user);
        return this.makeRequest("POST", "/user", request, AuthData.class);
    }

    public void login(String username, String password) throws ResponseException {
        var creds = Map.of("username", username, "password", password);
        var request = new Gson().toJson(creds);
        AuthData auth = this.makeRequest("POST", request, "/user", AuthData.class);
        authtoken = auth.authToken();
    }

    public void logout() throws ResponseException {
        var token = Map.of("authtoken", authtoken);
        var request = new Gson().toJson(token);
        AuthData auth = this.makeRequest("DELETE", request, "/session", AuthData.class);
        authtoken = auth.authToken();
    }

    public void joinGame(String playerColor, int gameID) throws ResponseException {
        JoinRequest request;
        if (playerColor != null) {
            request = new JoinRequest(playerColor, gameID);
        } else {
            request = new JoinRequest(null, gameID);
        }
        var r = new Gson().toJson(request);
        this.makeRequest("POST", "/game", r, null);
    }

    public GameData createGame(String gameName) throws ResponseException {
        var name = Map.of("gameName", gameName);
        var request = new Gson().toJson(name);
        return this.makeRequest("POST", request, "/game", GameData.class);
    }

    public GameListResponse listGames() throws ResponseException {
        var path = "/game";
        return this.makeRequest("GET", path, null, GameListResponse.class);
    }


    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

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
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }

    }
    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
