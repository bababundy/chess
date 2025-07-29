package client;

import model.GameData;
import requests.*;
import results.*;
import server.ResponseException;

import java.util.ArrayList;
import java.util.Arrays;

public class PostLoginClient extends ClientBase{
    private String authToken;
    private Repl repl;

    public PostLoginClient(String serverUrl, Repl repl, String authToken) {
        super(serverUrl);
        this.repl = repl;
        this.authToken = authToken;
    }

    @Override
    public String help() {
        return """
                    create <NAME> - a game
                    list - games
                    join <ID> [WHITE|BLACK] - a game
                    observe <ID> - a game
                    logout - when you are done
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
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String create(String[] params) throws ResponseException {
        if(params.length != 1) {
            throw new ResponseException(400, "Expected: <NAME>");
        }
        String gameName = params[0];

        CreateResult result = server.createGame(new CreateRequest(authToken, gameName));

        return "Created game " + result.gameID();
    }

    private String list() throws ResponseException {
        ListResult result = server.listGames(new ListRequest(authToken));
        return formatGameList(result.games());
    }

    private String join(String[] params) throws ResponseException {
        if(params.length != 2) {
            throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
        }
        Integer gameID = Integer.parseInt(params[0]);
        String playerColor = params[1].toUpperCase();

        JoinResult result = server.joinGame(new JoinRequest(authToken, playerColor, gameID));

        int dir = (playerColor == "WHITE") ? 1 : -1;
        state = State.INGAME;
        repl.setClient(new InGameClient(serverUrl, repl, authToken, dir));
        return "Joined game " + gameID + " as " + playerColor.toUpperCase();
    }

    private String observe(String[] params) throws ResponseException {
        if(params.length != 1) {
            throw new ResponseException(400, "Expected: <ID>");
        }
        int gameID = Integer.parseInt(params[0]);

        state = State.INGAME;
        repl.setClient(new InGameClient(serverUrl, repl, authToken, 1));
        return "Observing game " + gameID + " as WHITE";
    }

    private String logout() throws ResponseException {
        LogoutResult result = server.logout(new LogoutRequest(authToken));
        state = State.SIGNEDOUT;
        PreLoginClient newClient = new PreLoginClient(serverUrl, repl);
        repl.setClient(newClient);
        return "Come back soon!";
    }

    private String formatGameList(ArrayList<GameData> games) {
        if (games == null || games.isEmpty()) {
            return "No games available.\n";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Available Games:\n");
        for (GameData game : games) {
            sb.append(String.format("ID: %d, Name: %s", game.gameID(), game.gameName()));

            if (game.whiteUsername() != null) {
                sb.append(", White: ").append(game.whiteUsername());
            }
            if (game.blackUsername() != null) {
                sb.append(", Black: ").append(game.blackUsername());
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
