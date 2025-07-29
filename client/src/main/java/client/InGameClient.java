package client;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class InGameClient extends ClientBase{

    private String outerColor = SET_BG_COLOR_LIGHT_GREY;
    private String letterColor = SET_TEXT_COLOR_DARK_GREY;
    private String lightColor = SET_BG_COLOR_WHITE;
    private String darkColor = SET_BG_COLOR_BLACK;
    private String offBoardColor = RESET_BG_COLOR;

    private String authToken;
    private int checkerColor = 0;
    private int dir; // 1 for white or observe and -1 for black
    private Repl repl;

    public InGameClient(String serverUrl, Repl repl, String authToken, int direction) {
        super(serverUrl);
        this.repl = repl;
        this.authToken = authToken;
        this.dir = direction;
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
            case "d1" -> drawBoard();
            case "d2" -> drawOtherBoard();
//            case "hl" -> highlight();
//            case "m" -> move(params);
//            case "move" -> move(params);
//            case "make" -> move(params);
//            case "r" -> drawBoard(params);
//            case "redraw" -> drawBoard(params);
//            case "c" -> changeColors(params);
//            case "colors" -> changeColors(params);
//            case "res" -> resign();
//            case "resign" -> resign();
            case "leave" -> leave();
            default -> help();
        };

    }

    private String drawOtherBoard() {
        dir *= -1;
        drawBoard();
        return "";
    }

    private String leave() {
        //handle any closing game stuff

        state = State.SIGNEDIN;
        PostLoginClient newClient = new PostLoginClient(serverUrl, repl, authToken);
        repl.setClient(newClient);
        return "you left the game";
    }


    private String drawBoard() {
        int init = (dir == 1) ? 7 : 0; // 7 is top row, 0 is bottom
        int end = (dir == 1) ? -1 : 8;
        int step = (dir == 1) ? -1 : 1;

        String[][] board = {
                {"♜", "♞", "♝", "♛", "♚", "♝", "♞", "♜"},
                {"♟", "♟", "♟", "♟", "♟", "♟", "♟", "♟"},
                {" ", " ", " ", " ", " ", " ", " ", " "},
                {" ", " ", " ", " ", " ", " ", " ", " "},
                {" ", " ", " ", " ", " ", " ", " ", " "},
                {" ", " ", " ", " ", " ", " ", " ", " "},
                {"♙", "♙", "♙", "♙", "♙", "♙", "♙", "♙"},
                {"♖", "♘", "♗", "♕", "♔", "♗", "♘", "♖"},
        };

        printColHeaders(dir);
        checkerColor = 1;
        for (int row = init; row != end; row += step) {
            System.out.print(outerColor + letterColor + " " + (8 - row) + " ");  // left label

            checkerColor = row % 2;
            for (int col = 0; col < 8; col++) {
                String squareColor = checkerColor();
                String piece = board[row][col];
                String pieceColor = "♙♖♘♗♕♔".contains(piece) ? SET_TEXT_COLOR_BLUE :
                        "♟♜♞♝♛♚".contains(piece) ? SET_TEXT_COLOR_RED : letterColor;
                if(piece == null || piece.equals(" ")) {
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

    private String checkerColor () {
        if(checkerColor == 0){
            checkerColor = 1;
            return lightColor;
        } else {
            checkerColor = 0;
            return darkColor;
        }
    }

    private void printColHeaders(int dir) {
        String[] cols = { "a", "b", "c", "d", "e", "f", "g", "h" };

        System.out.print(outerColor + "   ");
        System.out.print(letterColor);
        if (dir == -1) {
            for (int col = 0; col < 8; col++) {
                System.out.print(UNICODE_SPACE + cols[col] + " ");
            }
        } else {
            for (int col = 7; col >= 0; col--) {
                System.out.print(UNICODE_SPACE + cols[col] + " ");
            }
        }
        System.out.println(outerColor + "   " + offBoardColor + RESET_TEXT_COLOR);
    }
}
