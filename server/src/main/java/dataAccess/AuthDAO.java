package dataAccess;

import model.AuthData;

// CRUD operations
public interface AuthDAO {
    void createAuth(AuthData auth);

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken);

    void clear();
}
