package client;

import server.ServerFacade;

public class ClientBase {
    protected final ServerFacade server;
    protected final String serverUrl;
    protected static State state = State.SIGNEDOUT;

    public ClientBase(String serverUrl) {
        server = new ServerFacade(8080);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        return "Base class can't help you here";
    }

    public String help() {
        return "Base class is not much help";
    }
}
