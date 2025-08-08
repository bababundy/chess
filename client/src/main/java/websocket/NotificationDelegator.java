package websocket;

import websocket.messages.*;

public class NotificationDelegator implements NotificationHandler {
    private final NotificationHandler gameHandler;  // ChessClient
    private final NotificationHandler errorHandler; // Repl

    public NotificationDelegator(NotificationHandler gameHandler, NotificationHandler errorHandler) {
        this.gameHandler = gameHandler;
        this.errorHandler = errorHandler;
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case ERROR -> errorHandler.notify(message);
            case NOTIFICATION, LOAD_GAME -> gameHandler.notify(message);
            default -> gameHandler.notify(message); // safe default
        }
    }
}
