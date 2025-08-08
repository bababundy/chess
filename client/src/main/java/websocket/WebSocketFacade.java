package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import server.ResponseException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private final NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    Gson gson = new Gson();
                    ServerMessage base = gson.fromJson(message, ServerMessage.class);

                    switch (base.getServerMessageType()) {
                        case LOAD_GAME -> {
                            LoadGameMessage loadGame = gson.fromJson(message, LoadGameMessage.class);
                            notificationHandler.notify(loadGame);
                        }
                        case NOTIFICATION -> {
                            NotificationMessage notification = gson.fromJson(message, NotificationMessage.class);
                            notificationHandler.notify(notification);
                        }
                        case ERROR -> {
                            ErrorMessage error = gson.fromJson(message, ErrorMessage.class);
                            notificationHandler.notify(error);
                        }
                        default -> {
                            notificationHandler.notify(base);
                        }
                    }
                }
            });
        } catch (IOException | URISyntaxException | DeploymentException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        //nothing needed here
    }

    public void connect(String authToken, Integer gameID) throws IOException {
        var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws IOException, ResponseException {
        var command = new MakeMoveCommand(authToken, gameID, move);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void resign(String authToken, int gameID) throws IOException, ResponseException {
        var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void leave(String authToken, Integer gameID) throws ResponseException, IOException {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID));
        session.close();
    }

    private void sendCommand(UserGameCommand command) throws ResponseException {
        try {
            String json = new Gson().toJson(command);
            this.session.getBasicRemote().sendText(json);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void close() {
        try {
            if (session != null && session.isOpen()) {
                session.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing WebSocket: " + e.getMessage());
        }
    }
}
