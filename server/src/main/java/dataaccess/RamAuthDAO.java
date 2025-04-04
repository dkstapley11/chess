package dataaccess;

import model.AuthData;

import java.util.HashSet;

public class RamAuthDAO implements AuthDAO {

    private HashSet<AuthData> database;

    public RamAuthDAO() {
        database = new HashSet<>(16);
    }

    @Override
    public void createAuth(AuthData auth) {
        database.add(auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws ResponseException {
        for (AuthData auth : database) {
            if (auth.authToken().equals(authToken)) {
                return auth;
            }
        };
        throw new ResponseException(401, "Auth token does not exist: " + authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws ResponseException {
        for (AuthData auth : database) {
            if (auth.authToken().equals(authToken)) {
                database.remove(auth);
                return;
            }
        }
        throw new ResponseException(500, "User is not logged in");
    }

    @Override
    public void clear() {
        database.clear();
    }

    @Override
    public HashSet<AuthData> listAuths() {
        return new HashSet<>(database);
    }
}
