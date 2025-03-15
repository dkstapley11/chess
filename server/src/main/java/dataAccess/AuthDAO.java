package dataAccess;

import model.AuthData;

import java.util.HashSet;

// CRUD operations
public interface AuthDAO {
    void createAuth(AuthData auth) throws ResponseException;

    AuthData getAuth(String authToken) throws ResponseException;

    void deleteAuth(String authToken) throws ResponseException;

    void clear() throws ResponseException;

    HashSet<AuthData> listAuths() throws ResponseException;
}
