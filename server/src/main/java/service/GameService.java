package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import requests.*;
import results.*;

import java.util.ArrayList;
import java.util.Objects;


public class GameService {
    private static final GameDao GAMEDAO = new GameDao();
    private static final AuthDao AUTHDAO = new AuthDao();

    public static CreateResult create(CreateRequest req) throws DataAccessException {
        //1. verify input
        String authToken = req.authToken();
        String gameName = req.gameName();
        if (authToken == null || gameName == null) {
            throw new DataAccessException("Missing input field");
        }

        //2. validate authToken
        try{
            AuthData user = AUTHDAO.getByToken(authToken);
        } catch (DataAccessException e) {
            throw new DataAccessException("Invalid AuthToken");
        }

        int gameID = GAMEDAO.getNumGames() + 1;
        //3. create new game model object
        GameData newGame = new GameData(gameID, null, null, gameName, new ChessGame());

        //4. insert new game into database UserDao.createGame(g)
        GAMEDAO.createGame(newGame);

        //5. create result and return gameID
        return new CreateResult(gameID, null);
    }

    public static JoinResult join(JoinRequest req) throws DataAccessException {
        //1. verify input
        String authToken = req.authToken();
        String playerColor = req.playerColor();
        Integer gameID = req.gameID();

        if (authToken == null || playerColor == null || gameID == null || gameID <= 0 || gameID > (GAMEDAO.getNumGames())) {
            throw new DataAccessException("Bad request");
        }
        if(!playerColor.equals("BLACK") && !playerColor.equals("WHITE")){
            throw new DataAccessException("Bad request");
        }

        //2 validate authToken
        AuthData user;
        try{
            user = AUTHDAO.getByToken(authToken);
        } catch (DataAccessException e) {
            throw new DataAccessException("invalid AuthToken");
        }

        //3. check if username is already taken in game
        //4. update game in database GameDao.updateGame(u)
        GameData game = GAMEDAO.getGameByID(gameID);
        if(playerColor.equals("WHITE")){
            if(Objects.equals(game.whiteUsername(), null) || Objects.equals(game.whiteUsername(), user.username())){
                GAMEDAO.updateGame(gameID, new GameData(gameID, user.username(), game.blackUsername(), game.gameName(), game.game()));
            } else {
                throw new DataAccessException("already Taken");
            }
        } else if (playerColor.equals("BLACK")) {
            if(Objects.equals(game.blackUsername(), null) || Objects.equals(game.blackUsername(), user.username())){
                GAMEDAO.updateGame(gameID, new GameData(gameID, game.whiteUsername(), user.username(), game.gameName(), game.game()));
            } else {
                throw new DataAccessException("already Taken");
            }
        } else {
            throw new DataAccessException("playerColor not valid");
        }

        //5. create result and return
        return new JoinResult(null);
    }

    public static ListResult list(ListRequest req) throws DataAccessException {
        //1. verify input
        String authToken = req.authToken();
        if (authToken == null) {
            throw new DataAccessException("Missing authToken");
        }
        //2 validate authToken
        AuthData user;
        try{
            user = AUTHDAO.getByToken(authToken);
        } catch (DataAccessException e) {
            throw new DataAccessException("Invalid AuthToken");
        }

        //3. create new arraylist and GameDao.getList()
        ArrayList<GameData> games = GAMEDAO.getList();

        //4. create result and return
        return new ListResult(games, null);
    }

}

