package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Objects;

public class Connection {
    public String authToken;
    public int gameID;
    public Session session;

    public Connection(String authToken, int gameID, Session session) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.session = session;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Connection that = (Connection) o;
        return gameID == that.gameID && Objects.equals(authToken, that.authToken) && Objects.equals(session, that.session);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authToken, gameID, session);
    }
}