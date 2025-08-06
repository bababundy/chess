package websocket;

import client.ClientBase;
import client.PostLoginClient;
import client.Repl;
import client.State;
import server.ResponseException;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class ChessClient extends ClientBase implements NotificationHandler {

    private String outerColor = SET_BG_COLOR_LIGHT_GREY;
    private String letterColor = SET_TEXT_COLOR_DARK_GREY;
    private String lightColor = SET_BG_COLOR_WHITE;
    private String darkColor = SET_BG_COLOR_BLACK;
    private String offBoardColor = RESET_BG_COLOR;

    private String authToken;
    private int dir; // 1 for white or observe and -1 for black
    private Repl repl;
    private WebSocketFacade ws;

    private String[][] board;

    public ChessClient(String serverUrl, Repl repl, String authToken, int direction) {
        super(serverUrl);
        this.repl = repl;
        this.authToken = authToken;
        this.dir = direction;
        try {
            this.ws = new WebSocketFacade(serverUrl, this);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void notify(ServerMessage message) {
        switch(message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(message.getMessage());
            case ERROR -> displayError(message.getMessage());
            case LOAD_GAME -> loadGame(message.getGame());
        }
    }

    private void displayNotification(String message) {

    }

    private void displayError(String message) {
    }

    private void loadGame(Object game) {
    }

    @Override
    public String help() {
        return """
                Options:
                Make a move: "m", "move", "make" <source> <destination> <optional promotion>(e.g. f5 e4 q)
                Redraw Chess Board: "r", "redraw"
                Change color scheme: "c", "colors" <color number>
                Resign from game: "res", "resign"
                Leave game: "leave"
                """;
    }

    @Override
    public String eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "draww" -> drawWhiteBoard();
            case "drawb" -> drawBlackBoard();
            case "hl" -> highlight();
            case "m" -> move(params);
            case "move" -> move(params);
            case "make" -> move(params);
            case "r" -> drawBoard();
            case "redraw" -> drawBoard();
            case "c" -> changeColors(params);
            case "colors" -> changeColors(params);
            case "res" -> resign();
            case "resign" -> resign();
            case "leave" -> leave();
            default -> help();
        };

    }

    private String highlight() {
        //get the valid moves

        //print the board but if the valid move location then different color
        return "";
    }

    private String resign() {
        //set game to null or delete game?

        return "";
    }

    private String changeColors(String[] params) {
        //read inputs

        //ensure valid

        //perform change

        //report status

        return "You changed the colors to " + darkColor + " and " + lightColor;
    }

    private String move(String[] params) {
        //validate one move was entered
        if(params.length >= 1) {
            //validate that the move is a valid move

            //send to websocket

        }

//        ws = new WebSocketFacade(serverUrl, notificationHandler);
//        ws.move(message);
        //draw fresh board
        return "";
    }

    private String drawWhiteBoard() {
        dir *= 1;
        drawBoard();
        return "";
    }

    private String drawBlackBoard() {
        dir *= -1;
        drawBoard();
        return "";
    }

    private String leave() {
        //handle any closing game stuff
        ws.leave();
        ws = null;

        state = State.SIGNEDIN;
        PostLoginClient newClient = new PostLoginClient(serverUrl, repl, authToken);
        repl.setClient(newClient);
        return "you left the game";
    }

    private String drawBoard() {
        int init = (dir == 1) ? 7 : 0; // 7 is top row, 0 is bottom
        int end = (dir == 1) ? -1 : 8;
        int step = (dir == 1) ? -1 : 1;
        int checkerRow = 1;

        printColHeaders(dir);
        for (int row = init; row != end; row += step, checkerRow++) {
            System.out.print(outerColor + letterColor + " " + (8 - row) + " ");  // left label

            String squareColor = (checkerRow % 2 == 0) ? lightColor : darkColor;
            for (int i = 0; i < 8; i++) {
                int col = (dir == -1) ? i : 7 - i;
                squareColor = (Objects.equals(squareColor, darkColor)) ? lightColor : darkColor;
                String piece = board[row][col];
                String pieceColor = "♙♖♘♗♕♔".contains(piece) ? SET_TEXT_COLOR_BLUE :
                        "♟♜♞♝♛♚".contains(piece) ? SET_TEXT_COLOR_RED : letterColor;
                if (piece == null || piece.equals(" ")) {
                    System.out.print(squareColor + pieceColor + EMPTY);
                } else {
                    System.out.print(squareColor + pieceColor + " " + piece + " ");
                }
            }

            System.out.println(outerColor + letterColor + " " + (8 - row) + " " + offBoardColor);
        }
        printColHeaders(dir);
        System.out.print(RESET_TEXT_COLOR);
        return "move?";
    }

    private void printColHeaders(int dir) {
        String[] cols = { "a", "b", "c", "d", "e", "f", "g", "h" };

        int start = (dir == -1) ? 0 : 7;
        int end = (dir == -1) ? 8 : -1;
        int step = (dir == -1) ? 1 : -1;

        System.out.print(outerColor + "   ");
        System.out.print(letterColor);
        for (int col = start; col != end; col += step) {
            System.out.print(UNICODE_SPACE + cols[col] + " ");
        }
        System.out.println(outerColor + "   " + offBoardColor + RESET_TEXT_COLOR);
    }
}