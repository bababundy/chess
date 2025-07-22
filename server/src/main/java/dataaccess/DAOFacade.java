package dataaccess;

import dataaccess.daointerfaces.AuthDAO;
import dataaccess.daointerfaces.GameDAO;
import dataaccess.daointerfaces.UserDAO;

public class DAOFacade {
    public static UserDAO userDAO;
    public static GameDAO gameDAO;
    public static AuthDAO authDAO;
}
