package dataaccess;

import dataaccess.daoInterfaces.AuthDAO;
import dataaccess.daoInterfaces.GameDAO;
import dataaccess.daoInterfaces.UserDAO;

public class DAOFacade {
    public static UserDAO userDAO;
    public static GameDAO gameDAO;
    public static AuthDAO authDAO;
}
