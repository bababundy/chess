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

    private void endSession(String authToken, Integer gameID, Session session) {
        connections.remove(new Connection(authToken, gameID, session));
    }

    private void connect(Session session, String username, UserGameCommand command) throws IOException, DataAccessException {
        GameData game = gameDAO.getGameByID(command.getGameID());
        if (game == null) {
            throw new DataAccessException("Game not found");
        }
        connections.add(new Connection(command.getAuthToken(), command.getGameID(), session));
        var message = String.format("%s has joined the game", username);
        connections.broadcast(command.getGameID(), command.getAuthToken(), new NotificationMessage(message));
        GameData gameData = gameDAO.getGameByID(command.getGameID());
        connections.sendLoadGameToOne(session, gameData.game());
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) throws IOException {

        //validate that the move is good?


        var message = String.format("%s moved to %s", username, command.getMove().toString());
        connections.broadcast(command.getAuthToken(), new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message));
//        connections.sendLoadGame();
    }

    private void leaveGame(Session session, UserGameCommand command) {


        endSession(command.getAuthToken(), command.getGameID(), session);
    }

    private void resign(Session session, String username, UserGameCommand command) {

        endSession(command.getAuthToken(), command.getGameID(), session);
    }

//    private void enter(String visitorName, Session session) throws IOException {
//        connections.add(visitorName, session);
//        var message = String.format("%s is in the shop", visitorName);
//        ServerMessage notification = new Notification(Notification.Type.ARRIVAL, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    private void exit(String visitorName) throws IOException {
//        connections.remove(visitorName);
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast("", notification);
//        } catch (Exception ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
}