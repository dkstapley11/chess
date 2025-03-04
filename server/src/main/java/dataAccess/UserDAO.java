package dataAccess;

import model.UserData;

public interface UserDAO {
    void insertUser(UserData userData) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    boolean authenticateUser(String username, String password) throws DataAccessException;
    void clear();
}
