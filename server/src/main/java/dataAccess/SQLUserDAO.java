package dataAccess;

import model.UserData;

import java.util.HashSet;

public class SQLUserDAO implements UserDAO {
    @Override
    public void insertUser(UserData userData) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public boolean authenticateUser(String username, String password) throws DataAccessException {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public HashSet<UserData> listUsers() {
        return null;
    }
}
