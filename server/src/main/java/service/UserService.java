package service;

import dataaccess.AuthDAO;
import dataaccess.ResponseException;
import dataaccess.UserDAO;
import model.UserData;
import model.AuthData;

import java.util.UUID;

public class UserService {
    UserDAO uDAO;
    AuthDAO aDAO;

    public UserService(UserDAO uDAO, AuthDAO aDAO) {
        this.uDAO = uDAO;
        this.aDAO = aDAO;
    }

    public AuthData registerUser(UserData userData) throws ResponseException {
        if (userData.username() == null || userData.password() == null || userData.email() == null || userData.username().isEmpty() || userData.password().isEmpty() || userData.email().isEmpty()) {
            throw new ResponseException(400, "Error: bad request");
        }

//        uDAO.getUser(userData.username());

        UserData newUser = new UserData(userData.username(), userData.password(), userData.email());
        uDAO.insertUser(newUser);

        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(userData.username(), authToken);
        aDAO.createAuth(authData);

        return authData;
    }

    public AuthData loginUser(String username, String password) throws ResponseException {
        if (!uDAO.authenticateUser(username, password)) {
            throw new ResponseException(401, "Error: Unauthorized");
        }

        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(username, authToken);
        aDAO.createAuth(authData);
        return authData;
    }

    public void logoutUser(String authToken) throws ResponseException {
        aDAO.deleteAuth(authToken);
    }

    public AuthData getAuth(String authToken) throws ResponseException {
        return aDAO.getAuth(authToken);
    }

    public void clearUsers() throws ResponseException {
        uDAO.clear();
        aDAO.clear();
    }
}
