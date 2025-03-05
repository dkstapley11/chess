package dataAccess;

import model.AuthData;

import java.util.HashSet;

// CRUD operations
public interface AuthDAO {
    void createAuth(AuthData auth);

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    void clear();

    HashSet<AuthData> listAuths();
}
