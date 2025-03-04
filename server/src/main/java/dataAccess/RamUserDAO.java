package dataAccess;

import model.UserData;
import java.util.HashSet;

public class RamUserDAO implements UserDAO {

    private HashSet<UserData> database;

    public RamUserDAO() {
        database = new HashSet<>(16);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for (UserData user : database) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        throw new DataAccessException("User not found: " + username);
    }

    @Override
    public void insertUser(UserData userData) throws DataAccessException {
        for (UserData user : database) {
            if (user.username().equals(userData.username())) {
                throw new DataAccessException("User already exists: " + userData.username());
            }
        }
        database.add(userData);
    }

    @Override
    public boolean authenticateUser(String username, String password) throws DataAccessException {
        for (UserData user : database) {
            if (user.username().equals(username) && user.password().equals(password)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        database.clear();
    }
}
