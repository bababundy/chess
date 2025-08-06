package server.websocket;

import chess.ChessGame;
import model.GameData;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

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

    public void sendNotification(String msg) {

    }

    void sendErrorMessage(RemoteEndpoint remote, ErrorMessage errorMessage) throws IOException {
        remote.sendString(errorMessage.toString());
    }

    public void sendLoadGame(int gameID, ChessGame game) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.gameID == (gameID)) {
                    c.send(game.toString());
                }
            } else {
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            connections.remove(c);
        }
    }

    public void broadcast(String excludeAuth, ServerMessage message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.authToken.equals(excludeAuth)) {
                    c.send(message.toString());
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