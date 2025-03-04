package Service;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import dataAccess.DataAccessException;
import model.UserData;
import model.AuthData;

import javax.xml.crypto.Data;
import java.util.UUID;

public class UserService {
    UserDAO uDAO;
    AuthDAO aDAO;

    public UserService(UserDAO uDAO, AuthDAO aDAO) {
        this.uDAO = uDAO;
        this.aDAO = aDAO;
    }

    public AuthData registerUser(UserData userData) throws DataAccessException {
        if (userData.username() == null || userData.password() == null || userData.email() == null || userData.username().isEmpty() || userData.password().isEmpty() || userData.email().isEmpty()) {
            throw new DataAccessException("Error: bad request");
        }

        try {
            uDAO.getUser(userData.username());
            throw new DataAccessException("Error: already taken");
        } catch (DataAccessException e) {
        }

        UserData newUser = new UserData(userData.username(), userData.password(), userData.email());
        uDAO.insertUser(newUser);

        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(userData.username(), authToken);
        aDAO.createAuth(authData);

        return authData;
    }

    public AuthData loginUser(UserData userData) throws DataAccessException {
        if (!uDAO.authenticateUser(userData.username(), userData.password())) {
            throw new DataAccessException("Error: Unauthorized");
        }

        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(userData.username(), authToken);
        aDAO.createAuth(authData);
        return authData;
    }

    public void logoutUser(String authToken) throws DataAccessException {
        aDAO.deleteAuth(authToken);
    }

    public void clearUsers() {
        uDAO.clear();
    }
}
