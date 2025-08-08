package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DAOFacade;
import dataaccess.DataAccessException;
import model.GameData;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(Connection newConn) {
        connections.put(newConn.authToken, newConn);
    }

    public void remove(Connection conn) {
        connections.remove(conn.authToken);
    }

    void sendErrorMessage(RemoteEndpoint remote, ErrorMessage errorMessage) throws IOException {
        remote.sendString(new Gson().toJson(errorMessage));
    }

    public void sendLoadGameToOne(Session session, ChessGame game) throws IOException {
        session.getRemote().sendString(new Gson().toJson(new LoadGameMessage(game)));
    }

    public void sendLoadGame(int gameID, ChessGame game) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.gameID == (gameID)) {
                    c.send(new Gson().toJson(new LoadGameMessage(game)));
                }
            } else {
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            connections.remove(c);
        }
    }

    public void broadcast(int gameID, String excludeAuth, NotificationMessage notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.authToken.equals(excludeAuth) && c.gameID == gameID) {
                    c.send(new Gson().toJson(notification));
                }
            } else {
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            connections.remove(c);
        }
    }
}