package websocket;

import chess.*;
import client.*;
import server.ResponseException;
import websocket.messages.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class ChessClient extends ClientBase implements NotificationHandler {

    private final String outerColor = SET_BG_COLOR_LIGHT_GREY;
    private final String letterColor = SET_TEXT_COLOR_DARK_GREY;
    private final String lightColor = SET_BG_COLOR_WHITE;
    private final String darkColor = SET_BG_COLOR_BLACK;
    private final String offBoardColor = RESET_BG_COLOR;

    private String authToken;
    private int playerColor; // 1 for white, 0 observe, and -1 for black
    private Repl repl;
    private int gameID;
    private WebSocketFacade ws;

    private String[][] board;
    private ChessGame currentGame;

    public ChessClient(String serverUrl, Repl repl, String authToken, int direction, int gameID) {
        super(serverUrl);
        this.repl = repl;
        this.authToken = authToken;
        this.playerColor = direction;
        this.gameID = gameID;
    }

    public void setWebSocketFacade(WebSocketFacade ws) {
        this.ws = ws;
    }

    @Override
    public void notify(ServerMessage message) {
        switch(message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification((NotificationMessage) message);
            case ERROR -> displayError((ErrorMessage) message);
            case LOAD_GAME -> loadGame((LoadGameMessage) message);
        }
    }

    private void displayNotification(NotificationMessage message) {
        System.out.println(SET_TEXT_COLOR_YELLOW + message.getMessage() + RESET_TEXT_COLOR);
        repl.printPrompt();
    }

    private void displayError(ErrorMessage message) {
        System.out.println(SET_TEXT_COLOR_RED + message.getErrorMessage() + RESET_TEXT_COLOR);
        repl.printPrompt();
    }

    private void loadGame(LoadGameMessage message) {
        this.currentGame = message.getGame();
        this.board = convertBoardToUnicode(currentGame.getBoard());
        System.out.println();
        drawBoard(playerColor);

        if (currentGame.isGameOver()) {
            System.out.println(SET_TEXT_COLOR_YELLOW + "Game over." + RESET_TEXT_COLOR);
        } else {
            System.out.println(SET_TEXT_COLOR_BLUE + "Turn: " + currentGame.getTeamTurn() + RESET_TEXT_COLOR);
        }
        repl.printPrompt();
    }

    private String[][] convertBoardToUnicode(ChessBoard board) {
        String[][] unicodeBoard = new String[8][8];

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row + 1, col + 1));
                if (piece == null) {
                    unicodeBoard[row][col] = " ";
                    continue;
                }

                ChessPiece.PieceType type = piece.getPieceType();
                ChessGame.TeamColor color = piece.getTeamColor();

                String symbol = switch (type) {
                    case KING -> (color == ChessGame.TeamColor.WHITE) ? "♔" : "♚";
                    case QUEEN -> (color == ChessGame.TeamColor.WHITE) ? "♕" : "♛";
                    case ROOK -> (color == ChessGame.TeamColor.WHITE) ? "♖" : "♜";
                    case BISHOP -> (color == ChessGame.TeamColor.WHITE) ? "♗" : "♝";
                    case KNIGHT -> (color == ChessGame.TeamColor.WHITE) ? "♘" : "♞";
                    case PAWN -> (color == ChessGame.TeamColor.WHITE) ? "♙" : "♟";
                };

                unicodeBoard[row][col] = symbol;
            }
        }
        return unicodeBoard;
    }


    @Override
    public String help() {
        return """
                Options:
                Make a move: "m", "move", "make" <source> <destination> <optional promotion>(e.g. f5 e4 q)
                Highlight moves: "hl", "highlight"
                Redraw Chess Board: "r", "redraw"
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
            case "hl", "highlight" -> highlight();
            case "m", "move", "make" -> move(params);
            case "r", "redraw" -> drawBoard(playerColor);
            case "res", "resign" -> resign();
            case "leave" -> leave();
            default -> help();
        };
    }

    private String highlight() {
        if (currentGame == null) {
            return SET_TEXT_COLOR_RED + "No game loaded yet." + RESET_TEXT_COLOR;
        }

        System.out.print("Enter the position to highlight (e.g., e2): ");
        String input = repl.readLine();
        if (input == null) return "";

        input = input.trim().toLowerCase();
        ChessPosition startPos;
        try {
            startPos = parsePosition(input);
        } catch (IllegalArgumentException ex) {
            return SET_TEXT_COLOR_RED + "Invalid input. Use like 'e2'." + RESET_TEXT_COLOR;
        }

        var validMoves = currentGame.validMoves(startPos);
        if (validMoves == null || validMoves.isEmpty()) {
            return SET_TEXT_COLOR_YELLOW + "No valid moves for " + input + "." + RESET_TEXT_COLOR;
        }

        boolean[][] highlights = new boolean[8][8];
        for (ChessMove move : validMoves) {
            ChessPosition end = move.getEndPosition(); // 1-based
            highlights[end.getRow() - 1][end.getColumn() - 1] = true;
        }
        drawBoard(playerColor, highlights);
        return "";
    }

    private String resign() {
        System.out.print("Are you sure you want to resign? (y/n): ");
        String response = repl.readLine();
        if (response == null || !response.trim().equalsIgnoreCase("y")) {
            return SET_TEXT_COLOR_YELLOW + "Resign cancelled." + RESET_TEXT_COLOR;
        }

        try {
            ws.resign(authToken, gameID);
            System.out.println(SET_TEXT_COLOR_YELLOW + "You resigned from the game." + RESET_TEXT_COLOR);
        } catch (IOException | ResponseException e) {
            return SET_TEXT_COLOR_RED + "Failed to resign: " + e.getMessage() + RESET_TEXT_COLOR;
        }
        return "Error in resign";
    }


    private String move(String[] params) {
        if (params.length < 2 || params.length > 3) {
            return "Expected: <startPos> <endPos> <optional promotionType>";
        }
        if (currentGame == null) {
            return SET_TEXT_COLOR_RED + "No game loaded yet." + RESET_TEXT_COLOR;
        }

        try {
            ChessPosition from = parsePosition(params[0]);
            ChessPosition to   = parsePosition(params[1]);

            ChessPiece.PieceType promotion = null;
            if (params.length == 3) {
                promotion = switch (params[2].toLowerCase()) {
                    case "q" -> ChessPiece.PieceType.QUEEN;
                    case "r" -> ChessPiece.PieceType.ROOK;
                    case "b" -> ChessPiece.PieceType.BISHOP;
                    case "n" -> ChessPiece.PieceType.KNIGHT;
                    default  -> null;
                };
                if (promotion == null) {
                    return "Invalid promotion piece. Use: q r b n";
                }
            }

            ChessMove move = new ChessMove(from, to, promotion);
            ws.makeMove(authToken, gameID, move);

            return "Move sent: " + params[0] + " -> " + params[1] + (promotion != null ? " = " + promotion : "");
        } catch (IllegalArgumentException e) {
            return SET_TEXT_COLOR_RED + "Invalid square. Use like: e2 e4 [q|r|b|n]" + RESET_TEXT_COLOR;
        } catch (ResponseException | IOException e) {
            return SET_TEXT_COLOR_RED + "Failed to send move: " + e.getMessage() + RESET_TEXT_COLOR;
        }
    }

    private ChessPosition parsePosition(String pos) {
        if (pos == null || pos.length() != 2) {
            throw new IllegalArgumentException("Invalid square: " + pos);
        }
        char file = Character.toLowerCase(pos.charAt(0)); // 'a'..'h'
        char rank = pos.charAt(1);                         // '1'..'8'
        if (file < 'a' || file > 'h' || rank < '1' || rank > '8') {
            throw new IllegalArgumentException("Invalid square: " + pos);
        }

        int col = (file - 'a') + 1; // a→1, b→2, ..., h→8
        int row = (rank - '0');     // '1'→1, ..., '8'→8
        return new ChessPosition(row, col);
    }

    private String leave() {
        if (ws != null) {
            try {
                ws.leave(authToken, gameID);
            } catch (ResponseException | IOException e) {
                throw new RuntimeException(e);
            }
            ws.close();
            ws = null;
        }

        state = State.SIGNEDIN;
        PostLoginClient newClient = new PostLoginClient(serverUrl, repl, authToken);
        repl.setClient(newClient);
        return "you left the game";
    }

    private String drawBoard(int dir) {
        return drawBoard(dir, null);
    }

    private String drawBoard(int dir, boolean[][] highlights) {
        if (dir == 0) { dir = 1; }

        // Row/col iteration order matches your existing working output
        int init = (dir == 1) ? 7 : 0;     // start row
        int end  = (dir == 1) ? -1 : 8;    // end (exclusive)
        int step = (dir == 1) ? -1 : 1;    // step
        int checkerRow = 1;

        printColHeaders(dir);
        for (int row = init; row != end; row += step, checkerRow++) {
            System.out.print(outerColor + letterColor + " " + (row + 1) + " "); // left rank label

            String squareColor = (checkerRow % 2 == 0) ? lightColor : darkColor;
            for (int i = 0; i < 8; i++) {
                int col = (dir == 1) ? i : 7 - i; // your working column order
                // alternate square color
                squareColor = (Objects.equals(squareColor, darkColor)) ? lightColor : darkColor;

                // apply highlight if provided
                String bg = squareColor;
                if (highlights != null && row >= 0 && row < 8 && col >= 0 && col < 8 && highlights[row][col]) {
                    bg = SET_BG_COLOR_GREEN;
                }

                String piece = board[row][col];
                String pieceColor =
                        "♙♖♘♗♕♔".contains(piece) ? SET_TEXT_COLOR_LIGHT_GREY:
                                "♟♜♞♝♛♚".contains(piece) ? SET_TEXT_COLOR_DARK_GREY : letterColor;

                if (piece == null || piece.equals(" ")) {
                    System.out.print(bg + pieceColor + EMPTY);
                } else {
                    System.out.print(bg + pieceColor + " " + piece + " ");
                }
            }

            System.out.println(outerColor + letterColor + " " + (row + 1) + " " + offBoardColor);
        }
        printColHeaders(dir);
        System.out.print(RESET_TEXT_COLOR);
        return "move?";
    }

    private void printColHeaders(int dir) {
        String[] cols = { "a", "b", "c", "d", "e", "f", "g", "h" };

        int start = (dir == 1) ? 0 : 7;
        int end = (dir == 1) ? 8 : -1;
        int step = (dir == 1) ? 1 : -1;

        System.out.print(outerColor + "   ");
        System.out.print(letterColor);
        for (int col = start; col != end; col += step) {
            System.out.print(UNICODE_SPACE + cols[col] + " ");
        }
        System.out.println(outerColor + "   " + offBoardColor + RESET_TEXT_COLOR);
    }
}