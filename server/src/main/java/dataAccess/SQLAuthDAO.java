package dataAccess;

import model.AuthData;

import java.util.HashSet;

public class SQLAuthDAO implements AuthDAO {
    @Override
    public void createAuth(AuthData auth) {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clear() {

    }

    @Override
    public HashSet<AuthData> listAuths() {
        return null;
    }
}
