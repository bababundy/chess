package websocket;

import com.google.gson.Gson;
import websocket.messages.ServerMessage;

import java.security.spec.ECField;

public interface NotificationHandler {
    void notify(ServerMessage message);


}
