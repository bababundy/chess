package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {
    private static boolean rightRook;
    private static boolean leftRook; //not static

    public static void setRightRook(boolean rightRook) {
        PieceMovesCalculator.rightRook = rightRook;
    }

    public static void setLeftRook(boolean leftRook) {
        PieceMovesCalculator.leftRook = leftRook;
    }


    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public static Collection<ChessMove> kingMovesCalculator(ChessBoard board, int currentCol, int currentRow, ChessGame.TeamColor currentColor){
        ArrayList<ChessMove> pieceMoves = new ArrayList<>();
        //for each spot around the king on the board
        //if the spot is empty or contains an enemy then he can move
        for(int i = currentRow - 1; i <= currentRow + 1; i++) {
            for(int j = currentCol - 1; j <= currentCol + 1; j++) {
                if(i >= 1 && i <= 8 && j >= 1 && j <= 8) { //if on the board
                    if(board.getPiece(new ChessPosition(i,j)) == null || board.getPiece(new ChessPosition(i,j)).getTeamColor() != currentColor){
                        //empty or contains enemy
                        pieceMoves.add(new ChessMove(new ChessPosition(currentRow, currentCol), new ChessPosition(i, j)));
                    }
                }
            }
        }
        //if(rightRook && board.getPiece(new ChessPosition(currentRow, currentCol + 1)) == null
        // && board.getPiece(new ChessPosition(currentRow, currentCol + 2)) == null
        // && board.getPiece(new ChessPosition(currentRow, currentCol + 3)) == null)
        return pieceMoves;
    }

    public static Collection<ChessMove> queenMovesCalculator(ChessBoard board, int currentCol, int currentRow, ChessGame.TeamColor currentColor){
        Collection<ChessMove> queenMoves = new ArrayList<>();
        //use the rook and bishop moves calculator
        queenMoves.addAll(rookMovesCalculator(board, currentCol, currentRow, currentColor));
        queenMoves.addAll(bishopMovesCalculator(board, new ChessPosition(currentRow, currentCol), currentColor));
        return queenMoves;
    }

    public static Collection<ChessMove> rookMovesCalculator(ChessBoard board, int currentCol, int currentRow, ChessGame.TeamColor currentColor){
        ArrayList<ChessMove> pieceMoves = new ArrayList<>();
        ChessPosition currentPosition = new ChessPosition(currentRow, currentCol);

        //starting from the rook and positive row
        //until hitting the end of the board or an enemy or teammate
        //repeat for negative direction
        //repeat all above for the column direction
        int[][] directions = {
                {1, 0},   // up
                {-1, 0},  //down
                {0, 1},   // right
                {0, -1}   // left
        };

        for (int[] dir : directions) {
            addMovesInDirection(board, currentPosition, currentColor, dir[0], dir[1], pieceMoves);
        }
        return pieceMoves;
    }

    public static Collection<ChessMove> bishopMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor currentColor){
        ArrayList<ChessMove> pieceMoves = new ArrayList<>();
        int[][] directions = {
                {1, 1},   //up-right
                {1, -1},  // up-left
                {-1, 1},  // down-right
                {-1, -1}  //down-left
        };
        for (int[] dir : directions) {
            addMovesInDirection(board, myPosition, currentColor, dir[0], dir[1], pieceMoves);
        }
        return pieceMoves;
    }

    private static void addMovesInDirection(ChessBoard board, ChessPosition startPos, ChessGame.TeamColor color,
                                            int rowDir, int colDir, ArrayList<ChessMove> moves) {
        int row = startPos.getRow()+rowDir;
        int col = startPos.getColumn()+colDir;

        while (row >= 1 && row <= 8 && col >= 1 && col <=8) {
            ChessPosition newPos = new ChessPosition(row, col);
            ChessPiece piece = board.getPiece(newPos);
            if(piece == null){
                moves.add(new ChessMove(startPos, newPos));
            } else{
                if(piece.getTeamColor() != color) {
                    moves.add(new ChessMove(startPos, newPos));
                }
                break;
            }
            row += rowDir;
            col += colDir;
        }
    }

    public static Collection<ChessMove> knightMovesCalculator(ChessBoard board, int currentCol, int currentRow, ChessGame.TeamColor currentColor){
        ArrayList<ChessMove> pieceMoves = new ArrayList<>();
        //check the 8 spots
        //if (they are on the board AND (are empty OR contain an enemy then move))
        int[] iSpots = {1, -1};
        int[] jSpots = {2, -2};
        for(int i : iSpots) {
            for(int j : jSpots) {
                if(currentRow+i >= 1 && currentRow+i <= 8 && currentCol+j >= 1 && currentCol+j <= 8) { //if on the board
                    ChessPosition spotToCheck = new ChessPosition(currentRow+i,currentCol+j);
                    if(board.getPiece(spotToCheck) == null || board.getPiece(spotToCheck).getTeamColor() != currentColor){
                        //empty or contains enemy
                        pieceMoves.add(new ChessMove(new ChessPosition(currentRow, currentCol), spotToCheck));
                    }
                }
                if(currentRow+j >= 1 && currentRow+j <= 8 && currentCol+i >= 1 && currentCol+i <= 8) { //if on the board
                    ChessPosition spotToCheck = new ChessPosition(currentRow+j,currentCol+i);
                    if(board.getPiece(spotToCheck) == null || board.getPiece(spotToCheck).getTeamColor() != currentColor){
                        //empty or contains enemy
                        pieceMoves.add(new ChessMove(new ChessPosition(currentRow, currentCol), spotToCheck));
                    }
                }
            }
        }
        return pieceMoves;
    }

    public static Collection<ChessMove> pawnMovesCalculator(ChessBoard board, int currentCol, int currentRow, ChessGame.TeamColor currentColor){
        ChessPosition myPosition = new ChessPosition(currentRow, currentCol);
        ArrayList<ChessMove> pieceMoves = new ArrayList<>();
        int dir = 1;
        if (currentColor == ChessGame.TeamColor.BLACK) {dir = -1;};

        //if the spot in front of it is empty then can move forwards
        if(board.getPiece(new ChessPosition(currentRow + dir, currentCol)) == null) {
            if((dir == -1 && currentRow == 2) || (dir == 1 && currentRow == 7)){//on last row before promotion and can move forwards
                pieceMoves.addAll(promotePawn(currentRow, currentCol, currentRow+dir, currentCol));
            }
            else{
                pieceMoves.add(new ChessMove(myPosition, new ChessPosition(currentRow + dir, currentCol)));
                if((dir == -1 && currentRow == 7 && board.getPiece(new ChessPosition(5, currentCol)) == null) ||
                        (dir == 1 && currentRow == 2 && board.getPiece(new ChessPosition(4, currentCol)) == null)) {
                    //on home row and can move two spots
                    pieceMoves.add(new ChessMove(myPosition, new ChessPosition(currentRow + (2 * dir), currentCol)));
                }
            }
        }

        //check if there is a piece available diagonally to the left to take
        if(currentCol != 1 && board.getPiece(new ChessPosition(currentRow + dir, currentCol - 1)) != null
                && board.getPiece(new ChessPosition(currentRow + dir, currentCol - 1)).getTeamColor() != currentColor) {
            if(currentRow + dir == 1 || currentRow + dir == 8) {//on last row before promotion and can move forwards
                pieceMoves.addAll(promotePawn(currentRow, currentCol, currentRow + dir, currentCol - 1));
            }
            else {
                pieceMoves.add(new ChessMove(myPosition, new ChessPosition(currentRow + dir, currentCol - 1)));
            }
        }
        //check if there is a piece available diagonally to the right to take
        if(currentCol != 8 && board.getPiece(new ChessPosition(currentRow + dir, currentCol + 1)) != null
                && board.getPiece(new ChessPosition(currentRow + dir, currentCol + 1)).getTeamColor() != currentColor) {
            if(currentRow + dir == 1 || currentRow + dir == 8) {//on last row before promotion and can move forwards
                pieceMoves.addAll(promotePawn(currentRow, currentCol, currentRow + dir, currentCol + 1));
            }
            else {
                pieceMoves.add(new ChessMove(myPosition, new ChessPosition(currentRow + dir, currentCol + 1)));
            }
        }
        return pieceMoves;
    }

    public static Collection<ChessMove> promotePawn (int currentRow, int currentCol, int promotionRow, int promotionCol) {
        ArrayList<ChessMove> pieceMoves = new ArrayList<>();
        ChessPosition startPosition = new ChessPosition(currentRow, currentCol);
        ChessPosition endPosition = new ChessPosition(promotionRow, promotionCol);
        pieceMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.QUEEN));
        pieceMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.ROOK));
        pieceMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.BISHOP));
        pieceMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.KNIGHT));
        return pieceMoves;
    }

}
