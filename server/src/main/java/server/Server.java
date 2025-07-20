package server;

import com.google.gson.Gson;
import dataaccess.*;
import dataaccess.memory.*;
import dataaccess.mySQL.*;
import service.*;
import requests.*;
import results.*;
import spark.*;

public class Server {
    private UserService userService;
    private GameService gameService;
    private ClearService clearService;

    public int run(int desiredPort){
        Spark.port(desiredPort);
        Spark.staticFiles.location("/web");

        if (DAOFacade.userDAO == null) {
            DAOFacade.userDAO = new MemoryUserDao();
        }
        if (DAOFacade.authDAO == null) {
            DAOFacade.authDAO = new MemoryAuthDao();
        }
        if (DAOFacade.gameDAO == null) {
            DAOFacade.gameDAO = new MemoryGameDao();
        }
        if (true) { //put true if SQL and false if memory
            DAOFacade.userDAO = new MySQLUserDao();
            try {
                DAOFacade.authDAO = new MySQLAuthDao();
            } catch (DataAccessException e) {
                throw new RuntimeException("setup failure");
            }
            DAOFacade.gameDAO = new MySQLGameDao();
        }

        userService = new UserService(DAOFacade.userDAO, DAOFacade.authDAO);
        gameService = new GameService(DAOFacade.gameDAO, DAOFacade.authDAO);
        clearService = new ClearService(DAOFacade.userDAO, DAOFacade.gameDAO, DAOFacade.authDAO);

        //account for all other (500) errors here
        Spark.exception(Exception.class, (exception, req, res) -> {
            res.status(500);
            res.type("application/json");
            String errorMessage = exception.getMessage() != null ? exception.getMessage() : "Internal server error";
            res.body(new Gson().toJson(new ErrorResult("Error: " + errorMessage)));
        });

        //setup endpoints
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clear);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    //below are all the service handlers for the seven endpoints

    private Object register (Request req, Response res) throws DataAccessException {
        RegisterRequest request = new Gson().fromJson(req.body(), RegisterRequest.class);
        RegisterResult result;
        try{
            result = userService.register(request);
            res.status(200);
        } catch (DataAccessException e) {
            if(e.getMessage().contains("Missing")) {
                res.status(400);
                result = new RegisterResult(null, null, "Error: bad request");
            }
            else {
                result = new RegisterResult(null, null, "Error: already taken");
                res.status(403);
            }
        }
        return new Gson().toJson(result);
    }

    private Object login (Request req, Response res) throws DataAccessException {
        LoginRequest request = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResult result;
        try{
            result = userService.login(request);
            res.status(200);
        } catch (DataAccessException e) {
            if(e.getMessage().contains("Missing")) {
                res.status(400);
                result = new LoginResult(null, null, "Error: bad request");
            }
            else {
                result = new LoginResult(null, null, "Error: unauthorized");
                res.status(401);
            }
        }
        return new Gson().toJson(result);
    }

    private Object logout (Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        LogoutRequest request = new LogoutRequest(authToken);
        try{
            LogoutResult result = userService.logout(request);
            res.status(200);
            return new Gson().toJson(result);
        } catch (DataAccessException e) {
            LogoutResult result = new LogoutResult("Error: unauthorized");
            res.status(401);
            return new Gson().toJson(result);
        }
    }

    private Object listGames (Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        ListRequest request = new ListRequest(authToken);
        ListResult result;
        try{
            result = gameService.list(request);
            res.status(200);
        } catch (DataAccessException e) {
            result = new ListResult(null,"Error: unauthorized");
            res.status(401);
        }
        return new Gson().toJson(result);
    }

    private Object createGame (Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        CreateReqHelper helper = new Gson().fromJson(req.body(), CreateReqHelper.class);
        CreateRequest request = new CreateRequest(authToken, helper.gameName());

        CreateResult result;
        try{
            result = gameService.create(request);
            res.status(200);
            return new Gson().toJson(result);
        } catch (DataAccessException e) {
            if(e.getMessage().contains("Missing")) {
                res.status(400);
                result = new CreateResult(null, "Error: bad request");
            }
            else {
                result = new CreateResult(null, "Error: unauthorized");
                res.status(401);
            }
        }
        return new Gson().toJson(result);
    }

    private Object joinGame (Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        JoinReqHelper helper = new Gson().fromJson(req.body(), JoinReqHelper.class);
        JoinRequest request = new JoinRequest(authToken, helper.playerColor(), helper.gameID());
        JoinResult result;
        try{
            result = gameService.join(request);
            res.status(200);
            return new Gson().toJson(result);
        } catch (DataAccessException e) {
            if(e.getMessage().contains("Bad")) {
                res.status(400);
                result = new JoinResult("Error: bad request");
            }
            else if(e.getMessage().contains("AuthToken")) {
                result = new JoinResult("Error: unauthorized");
                res.status(401);
            } else if(e.getMessage().contains("Taken")){
                result = new JoinResult("Error: already taken");
                res.status(403);
            } else {
                res.status(500);
                result = new JoinResult(e.getMessage());
            }
        }
        return new Gson().toJson(result);
    }

    private Object clear (Request req, Response res) throws DataAccessException {
        ClearResult result = clearService.clear();
        res.status(200);
        return new Gson().toJson(result);
    }
}