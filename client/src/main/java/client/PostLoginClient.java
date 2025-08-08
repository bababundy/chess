package client;

import model.GameData;
import requests.*;
import results.*;
import server.ResponseException;
import websocket.ChessClient;
import websocket.NotificationDelegator;
import websocket.WebSocketFacade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PostLoginClient extends ClientBase{
    private String authToken;
    private Repl repl;
    HashMap <Integer, Integer> gameMap = new HashMap<>(); //<fakeNum, gameID>


    public PostLoginClient(String serverUrl, Repl repl, String authToken) {
        super(serverUrl);
        this.repl = repl;
        this.authToken = authToken;
        try {
            updateGameMap();
        } catch (ResponseException e) {
            throw new RuntimeException("Server problems");
        }
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
        updateGameMap();
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
        Integer gameID;
        updateGameMap();
        try {
            Integer gameNum = Integer.parseInt(params[0]);
            gameID = getGameIDfromMap(gameNum);
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
        }
        if(gameID == null){
            throw new ResponseException(400, "Game # not available.");
        }
        String playerColor = params[1].toUpperCase();
        if(!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
            throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
        }

        JoinResult result = server.joinGame(new JoinRequest(authToken, playerColor, gameID));
        if(result.message() != null){
            return result.message();
        }

        int dir = (playerColor.equals("WHITE")) ? 1 : -1;
        state = State.INGAME;
        ChessClient chessNotifier = new ChessClient(serverUrl, repl, authToken, dir, gameID);
        WebSocketFacade ws = new WebSocketFacade(serverUrl, new NotificationDelegator(
                chessNotifier,
                repl
        ));
        chessNotifier.setWebSocketFacade(ws);
        try {
            ws.connect(authToken, gameID);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        repl.setClient(chessNotifier);

        return "Joined game " + gameID + " as " + playerColor.toUpperCase();
    }

    private String observe(String[] params) throws ResponseException {
        if(params.length != 1) {
            throw new ResponseException(400, "Expected: <ID>");
        }

        updateGameMap();

        Integer gameNum = Integer.parseInt(params[0]);
        Integer gameID = getGameIDfromMap(gameNum);
        if(gameID == null){
            throw new ResponseException(400, "Game # not available.");
        }

        state = State.INGAME;
        ChessClient chessNotifier = new ChessClient(serverUrl, repl, authToken, 0, gameID);
        WebSocketFacade ws = new WebSocketFacade(serverUrl, new NotificationDelegator(
                chessNotifier,
                repl
        ));
        chessNotifier.setWebSocketFacade(ws);
        try {
            ws.connect(authToken, gameID);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        repl.setClient(chessNotifier);

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

        Integer i = 1;
        for (GameData game : games) {
            sb.append(String.format("Game %d: %s", i++, game.gameName()));

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

    private Integer getGameIDfromMap(Integer gameNum) {
        return gameMap.get(gameNum);
    }

    private void updateGameMap() throws ResponseException {
        ListResult result = server.listGames(new ListRequest(authToken));
        ArrayList<GameData> games = result.games();
        gameMap.clear();
        Integer i = 1;
        for (GameData game : games) {
            gameMap.put(i, game.gameID());
            i++;
        }
    }
}
