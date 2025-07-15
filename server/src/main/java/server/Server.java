package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requests.RegisterRequest;
import service.UserService;
import service.GameService;
import service.ClearService;
import requests.*;
import results.*;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
//        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
//        Spark.delete("/session", this::logout);
//        Spark.get("/game", this::listGames);
//        Spark.post("/game", this::createGame);
//        Spark.put("/game", this::joinGame);
//        Spark.delete("/db", this::clear);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

//    private Object register (Request req, Response res) throws DataAccessException {
//        RegisterRequest request = new Gson().fromJson(req.body(), RegisterRequest.class);
//        var result = UserService.register(request);
//        res.status(200);
//        return new Gson().toJson(result);
//    }
//
    private Object login (Request req, Response res) throws DataAccessException {
        LoginRequest request = new Gson().fromJson(req.body(), LoginRequest.class);
        try{
            var result = UserService.login(request);
            res.status(200);
            return new Gson().toJson(result);
        } catch (DataAccessException e) {
            LoginResult result = new LoginResult(null, null, "Error: unauthorized");
            res.status(401);
            return new Gson().toJson(result);
        }
    }
//
//    private Object logout (Request req, Response res) throws DataAccessException {
//        String authToken = req.headers("authToken");
//        LogoutRequest request = new LogoutRequest(authToken);
//        var result = UserService.logout(request);
//        res.status(200);
//        return new Gson().toJson(result);
//    }
//
//    private Object listGames (Request req, Response res) throws DataAccessException {
//        String authToken = req.headers("authToken");
//        ListRequest request = new ListRequest(authToken);
//        var result = GameService.list(request);
//        res.status(200);
//        return new Gson().toJson(result);
//    }
//
//    private Object createGame (Request req, Response res) throws DataAccessException {
//        String authToken = req.headers("Authorization");
//        var body = new Gson().fromJson(req.body(), BodyOnlyJoinData.class);
//        JoinRequest request = new JoinRequest(authToken, body.playerColor(), body.gameID());
//        JoinResult result = GameService.joinGame(request);
//        res.status(200);
//        return new Gson().toJson(result);
//    }
//
//    private Object joinGame (Request req, Response res) throws DataAccessException {
//        String authToken = req.headers("authToken");
//        JoinRequest request = new JoinRequest(authToken);
//        var result = GameService.join(request);
//        res.status(200);
//        return new Gson().toJson(result);
//    }
//
//    private Object clear (Request req, Response res) throws DataAccessException {
//        ClearResult result = ClearService.clear();
//        res.status(200);
//        return new Gson().toJson(result);
//    }
}