package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DAOFacade;
import dataaccess.DataAccessException;
import dataaccess.daointerfaces.AuthDAO;
import dataaccess.daointerfaces.GameDAO;
import dataaccess.daointerfaces.UserDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    AuthDAO authDAO;
    GameDAO gameDAO;
    UserDAO userDAO;

    public WebSocketHandler () {
        authDAO = DAOFacade.authDAO;
        gameDAO = DAOFacade.gameDAO;
        userDAO = DAOFacade.userDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            String username = null;
            try {
                username = (authDAO.getByToken(command.getAuthToken())).username();
            }
            catch (Exception ex) {
                connections.sendErrorMessage(session.getRemote(), new ErrorMessage("Error: unauthorized"));
                return;
            }
            saveSession(command.getAuthToken(), command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, command);
                case MAKE_MOVE -> makeMove(session, username, new Gson().fromJson(message, MakeMoveCommand.class));
                case LEAVE -> leaveGame(session, command);
                case RESIGN -> resign(session, username, command);
            }
        } catch (DataAccessException ex) {
            connections.sendErrorMessage(session.getRemote(), new ErrorMessage("Error: unauthorized"));
        } catch (Exception ex) {
            ex.printStackTrace();
            connections.sendErrorMessage(session.getRemote(), new ErrorMessage("Error: " + ex.getMessage()));
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        connections.connections.values().removeIf(c -> c.session.equals(session));
    }

    private void saveSession(String authToken, Integer gameID, Session session) {
        connections.add(new Connection(authToken, gameID, session));
    }

    private void endSession(String authToken) {
        connections.remove(authToken);
    }

    private void connect(Session session, String username, UserGameCommand command) throws IOException, DataAccessException {
        GameData game = gameDAO.getGameByID(command.getGameID());
        if (game == null) {
            throw new DataAccessException("Game not found");
        }
        connections.add(new Connection(command.getAuthToken(), command.getGameID(), session));
        String color = null;
        if (username.equals(game.whiteUsername())) {
            color = "WHITE";
        } else if (username.equals(game.blackUsername())) {
            color = "BLACK";
        } else {
            color = "OBSERVER";
        }
        var message = username + " has joined the game as " + color;
        connections.broadcast(command.getGameID(), command.getAuthToken(), new NotificationMessage(message));
        GameData gameData = gameDAO.getGameByID(command.getGameID());
        connections.sendLoadGameToOne(session, gameData.game());
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) throws IOException, DataAccessException {
        try {
            GameData gameData = gameDAO.getGameByID(command.getGameID());
            ChessGame game = gameData.game();
            if (game.isGameOver()) {
                connections.sendErrorMessage(session.getRemote(), new ErrorMessage("Error: game already over"));
                return;
            }
            ChessGame.TeamColor playerColor = getPlayerColor(username, gameData);
            if (playerColor == null) {
                connections.sendErrorMessage(session.getRemote(), new ErrorMessage("Error: observers can't move"));
                return;
            }
            if (game.getTeamTurn() != playerColor) {
                connections.sendErrorMessage(session.getRemote(), new ErrorMessage("Error: not your turn"));
                return;
            }
            if (command.getMove().getStartPosition() != null) {
                var piece = game.getBoard().getPiece(command.getMove().getStartPosition());
                if (piece == null || piece.getTeamColor() != playerColor) {
                    connections.sendErrorMessage(session.getRemote(), new ErrorMessage("Error: not your piece"));
                    return;
                }
            }
            game.makeMove(command.getMove());
            if (game.isInCheckmate(game.getTeamTurn())) {
                game.setGameOver(true);
            }
            gameDAO.updateGame(command.getGameID(), gameData);
            connections.sendLoadGame(command.getGameID(), game);
            String message = username + " moved to " + command.getMove().toString();
            connections.broadcast(command.getGameID(), command.getAuthToken(), new NotificationMessage(message));
        } catch (Exception ex) {
            connections.sendErrorMessage(session.getRemote(), new ErrorMessage("Error: General make move error" + ex.getMessage()));
        }
    }

    private ChessGame.TeamColor getPlayerColor(String username, GameData game) {
        if (username.equals(game.whiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        }
        if (username.equals(game.blackUsername())) {
            return ChessGame.TeamColor.BLACK;
        }
        return null; // observer
    }

    private void leaveGame(Session session, UserGameCommand command) throws IOException, DataAccessException {
        String username = authDAO.getByToken(command.getAuthToken()).username();
        GameData gameData = gameDAO.getGameByID(command.getGameID());

        boolean isPlayer = false;
        String white = gameData.whiteUsername();
        String black = gameData.blackUsername();
        if (username.equals(white)) {
            white = null;
            isPlayer = true;
        } else if (username.equals(black)) {
            black = null;
            isPlayer = true;
        }
        if (isPlayer) {
            GameData updatedGameData = new GameData(
                    gameData.gameID(), white, black, gameData.gameName(), gameData.game()
            );
            gameDAO.updateGame(command.getGameID(), updatedGameData);
        }
        String message = username + " has left the game";
        connections.broadcast(command.getGameID(), command.getAuthToken(), new NotificationMessage(message));
        endSession(command.getAuthToken());
    }

    private void resign(Session session, String username, UserGameCommand command) throws IOException, DataAccessException {
        GameData gameData = gameDAO.getGameByID(command.getGameID());
        ChessGame game = gameData.game();
        if (game.isGameOver()) {
            connections.sendErrorMessage(session.getRemote(), new ErrorMessage("Error: game already over"));
            return;
        }
        ChessGame.TeamColor playerColor = getPlayerColor(username, gameData);
        if (playerColor == null) {
            connections.sendErrorMessage(session.getRemote(), new ErrorMessage("Error: observers can't resign"));
            return;
        }
        game.setGameOver(true);
        GameData updated = new GameData(gameData.gameID(), gameData.gameName(),gameData.whiteUsername(), gameData.blackUsername(), game);
        gameDAO.updateGame(command.getGameID(), updated);
        String msg = username + " has resigned from the game";
        connections.broadcastToAll(command.getGameID(), new NotificationMessage(msg));
        endSession(command.getAuthToken());
    }

}