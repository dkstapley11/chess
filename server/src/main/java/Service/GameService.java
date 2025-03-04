package Service;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import dataAccess.DataAccessException;
import model.UserData;
import model.AuthData;

public class GameService {
    AuthDAO aDAO;
    GameDAO gDAO;

    public GameService(AuthDAO aDAO, GameDAO gDAO) {
        this.aDAO = aDAO;
        this.gDAO = gDAO;
    }
}
