package service;

import chess.ChessGame;
import dataaccess.*;
import dataaccess.daointerfaces.AuthDAO;
import dataaccess.daointerfaces.GameDAO;
import model.*;
import requests.*;
import results.*;

import java.util.ArrayList;
import java.util.Objects;


public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateResult create(CreateRequest req) throws DataAccessException {
        //1. verify input
        String authToken = req.authToken();
        String gameName = req.gameName();
        if (authToken == null || gameName == null) {
            throw new DataAccessException("Missing input field");
        }

        //2. validate authToken
        try{
            AuthData user = authDAO.getByToken(authToken);
        } catch (DataAccessException e) {
            if(e.getMessage().contains("500")){
                throw e;
            } else {
                throw new DataAccessException("Invalid AuthToken");
            }
        }

        int gameID  = 1;
        if(gameDAO.getNumGames() != null){
            gameID += gameDAO.getNumGames();
        }
        //3. create new game model object
        GameData newGame = new GameData(gameID, null, null, gameName, new ChessGame());

        //4. insert new game into database UserDao.createGame(g)
        gameDAO.createGame(newGame);

        //5. create result and return gameID
        return new CreateResult(gameID, null);
    }

    public JoinResult join(JoinRequest req) throws DataAccessException {
        //1. verify input
        String authToken = req.authToken();
        String playerColor = req.playerColor();
        Integer gameID = req.gameID();

        if (authToken == null || playerColor == null || gameID == null || gameID <= 0) {
            throw new DataAccessException("Bad request");
        }
        if(!playerColor.equals("BLACK") && !playerColor.equals("WHITE")){
            throw new DataAccessException("Bad request");
        }

        //2 validate authToken
        AuthData user;
        try{
            user = authDAO.getByToken(authToken);
        } catch (DataAccessException e) {
            if(e.getMessage().contains("500")){
                throw e;
            } else {
                throw new DataAccessException("Invalid AuthToken");
            }
        }

        //3. check if username is already taken in game
        //4. update game in database GameDao.updateGame(u)
        GameData game = gameDAO.getGameByID(gameID);

        if(playerColor.equals("WHITE")){
            if(game.whiteUsername() == null || game.whiteUsername().equals(user.username())){
                gameDAO.updateGame(gameID, new GameData(gameID, user.username(), game.blackUsername(), game.gameName(), game.game()));
            } else {
                throw new DataAccessException("Error: already taken");
            }
        } else {
            if(game.blackUsername() == null || game.blackUsername().equals(user.username())){
                gameDAO.updateGame(gameID, new GameData(gameID, game.whiteUsername(), user.username(), game.gameName(), game.game()));
            } else {
                throw new DataAccessException("Error: already taken");
            }
        }


        //5. create result and return
        return new JoinResult(null);
    }

    public ListResult list(ListRequest req) throws DataAccessException {
        //1. verify input
        String authToken = req.authToken();
        if (authToken == null) {
            throw new DataAccessException("Missing authToken");
        }
        //2 validate authToken
        AuthData user;
        try{
            user = authDAO.getByToken(authToken);
        } catch (DataAccessException e) {
            if(e.getMessage().contains("500")){
                throw e;
            } else {
                throw new DataAccessException("Invalid AuthToken");
            }
        }

        //3. create new arraylist and GameDao.getList()
        ArrayList<GameData> games = gameDAO.getList();

        //4. create result and return
        return new ListResult(games, null);
    }

    public void resign(String authToken, int gameID) throws DataAccessException {
        GameData game = gameDAO.getGameByID(gameID);
        String username = authDAO.getByToken(authToken).username();
        if (!username.equals(game.whiteUsername()) && !username.equals(game.blackUsername())) {
            throw new DataAccessException("Observers can't resign");
        }
        if (game.game().isGameOver()) {
            throw new DataAccessException("Game already over");
        }
        game.game().setGameOver(true);
        gameDAO.updateGame(gameID, game);
    }
}

