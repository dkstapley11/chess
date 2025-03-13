package dataAccess;

import model.UserData;

import java.util.HashSet;

public interface UserDAO {
    void insertUser(UserData userData) throws ResponseException;
    UserData getUser(String username) throws ResponseException;
    boolean authenticateUser(String username, String password) throws ResponseException;
    void clear() throws ResponseException;
    HashSet<UserData> listUsers() throws ResponseException;
}
