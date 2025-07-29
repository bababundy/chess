package client;

import requests.LoginRequest;
import requests.RegisterRequest;
import results.LoginResult;
import results.RegisterResult;
import server.ResponseException;

import java.util.Arrays;

public class PreLoginClient extends ClientBase{
    private Repl repl;

    public PreLoginClient(String serverUrl, Repl repl) {
        super(serverUrl);
        this.repl = repl;
    }

    @Override
    public String help() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - playing chess
                help - with possible commands
                """;
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String register(String[] params) throws ResponseException {
        if(params.length != 3) {
            throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
        }
        String username = params[0];
        String password = params[1];
        String email = params[2];
        RegisterResult result;
        try {
            result = server.register(new RegisterRequest(username, password, email));
        } catch (ResponseException e) {
            throw new ResponseException(400, "Username already taken, try something else");
        }

        state = State.SIGNEDIN;
        PostLoginClient newClient = new PostLoginClient(serverUrl, repl, result.authToken());
        repl.setClient(newClient);
        return String.format("Welcome %s.", username);
    }


    private String login(String[] params) throws ResponseException {
        if(params.length != 2) {
            throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
        }
        String username = params[0];
        String password = params[1];

        //check if correct password and get new authtoken
        LoginResult result;
        try {
            result = server.login(new LoginRequest(username, password));
        } catch (ResponseException e) {
            throw new ResponseException(400, "Incorrect Password");
        }

        state = State.SIGNEDIN;
        PostLoginClient newClient = new PostLoginClient(serverUrl, repl, result.authToken());
        repl.setClient(newClient);
        return String.format("Welcome %s.", result.username());
    }
}
