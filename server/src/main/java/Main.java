import dataaccess.*;
import dataaccess.mySQL.*;
import server.Server;
import service.*;

public class Main {
    public static void main(String[] args) {
        try {
            var port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }

            DAOFacade.userDAO = new MemoryUserDao();
            DAOFacade.authDAO = new MemoryAuthDao(); // or new SQLAuthDAO();
            DAOFacade.gameDAO = new MemoryGameDao();
            if (args.length >= 2 && args[1].equals("sql")) {
                DAOFacade.userDAO = new MySQLUserDao();
                DAOFacade.authDAO = new MySQLAuthDao();
                DAOFacade.gameDAO = new MySQLGameDao();
            }

            port = new Server().run(port);
            System.out.printf("Server started on port %d", port);
            return;
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }

        System.out.println("♕ 240 Chess Server: ");
    }
}

