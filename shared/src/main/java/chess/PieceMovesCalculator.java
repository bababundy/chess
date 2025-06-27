package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {
    private static final ArrayList<ChessMove> pieceMoves = new ArrayList<>();
    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public static Collection<ChessMove> kingMovesCalculator(ChessBoard board, int currentCol, int currentRow, ChessGame.TeamColor currentColor){
        pieceMoves.clear();
        //for each spot around the king on the board
        //if the spot is empty or contains an enemy then he can move
        for(int i = currentRow - 1; i <= currentRow + 1; i++) {
            for(int j = currentCol - 1; j <= currentCol + 1; j++) {
                if(i >= 1 && i <= 8 && j >= 1 && j <= 8) { //if on the board
                    if(board.getPiece(new ChessPosition(i,j)) == null || board.getPiece(new ChessPosition(i,j)).getTeamColor() != currentColor){ //empty or contains enemy
                        pieceMoves.add(new ChessMove(new ChessPosition(currentRow, currentCol), new ChessPosition(i, j)));
                    }
                }
            }
        }
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
        pieceMoves.clear();
        //starting from the rook and positive row
        //until hitting the end of the board or an enemy or teammate
        //repeat for negative direction
        //repeat all above for the column direction
        for (int i = currentRow + 1; i <= 8 ; i++) { //check up
            if(board.getPiece(new ChessPosition(i,currentCol)) == null) {
                pieceMoves.add(new ChessMove(new ChessPosition(currentRow, currentCol), new ChessPosition(i, currentCol)));
            } else if(board.getPiece(new ChessPosition(i,currentCol)).getTeamColor() != currentColor) { //enemy piece
                pieceMoves.add(new ChessMove(new ChessPosition(currentRow, currentCol), new ChessPosition(i, currentCol)));
                i = 9;
            } else { //teammate blocking road
                i = 9;
            }
        }
        for (int i = currentRow - 1; i >= 1; i--) { //check down
            if(board.getPiece(new ChessPosition(i,currentCol)) == null) {
                pieceMoves.add(new ChessMove(new ChessPosition(currentRow, currentCol), new ChessPosition(i, currentCol)));
            } else if(board.getPiece(new ChessPosition(i,currentCol)).getTeamColor() != currentColor) { //enemy piece
                pieceMoves.add(new ChessMove(new ChessPosition(currentRow, currentCol), new ChessPosition(i, currentCol)));
                i = 0;
            } else { //teammate blocking road
                i = 0;
            }
        }
        for (int j = currentCol + 1; j <= 8; j++) { //check right
            if(board.getPiece(new ChessPosition(currentRow,j)) == null) {
                pieceMoves.add(new ChessMove(new ChessPosition(currentRow, currentCol), new ChessPosition(currentRow,j)));
            } else if(board.getPiece(new ChessPosition(currentRow,j)).getTeamColor() != currentColor) { //enemy piece
                pieceMoves.add(new ChessMove(new ChessPosition(currentRow, currentCol), new ChessPosition(currentRow,j)));
                j = 9;
            } else { //teammate blocking road
                j = 9;
            }
        }
        for (int j = currentCol - 1; j >= 1; j--) { //check left
            if(board.getPiece(new ChessPosition(currentRow,j)) == null) {
                pieceMoves.add(new ChessMove(new ChessPosition(currentRow, currentCol), new ChessPosition(currentRow,j)));
            } else if(board.getPiece(new ChessPosition(currentRow,j)).getTeamColor() != currentColor) { //enemy piece
                pieceMoves.add(new ChessMove(new ChessPosition(currentRow, currentCol), new ChessPosition(currentRow,j)));
                j = 0;
            } else { //teammate blocking road
                j = 0;
            }
        }
        return pieceMoves;
    }

    public static Collection<ChessMove> bishopMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor currentColor){
        pieceMoves.clear();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        //starting from the bishop and positive diagonal
        //until hitting the end of the board or an enemy or teammate
        //repeat for negative direction
        //repeat all above for the opposite diagonal
        for (int i = currentRow + 1, j = currentCol + 1; i <= 8 && j <= 8; i++, j++) { //up and right
            if(board.getPiece(new ChessPosition(i,j)) == null) {
                pieceMoves.add(new ChessMove(myPosition, new ChessPosition(i, j)));
            } else {
                if(board.getPiece(new ChessPosition(i,j)).getTeamColor() != currentColor) { //enemy piece, can take but blocked after that
                    pieceMoves.add(new ChessMove(myPosition, new ChessPosition(i, j)));
                }
                break;
            }
        }
        for (int i = currentRow - 1, j = currentCol + 1; i >= 1 && j <= 8; i--, j++) { //down and right
            if(board.getPiece(new ChessPosition(i,j)) == null) {
                pieceMoves.add(new ChessMove(myPosition, new ChessPosition(i, j)));
            } else {
                if(board.getPiece(new ChessPosition(i,j)).getTeamColor() != currentColor) { //enemy piece, can take but blocked after that
                    pieceMoves.add(new ChessMove(myPosition, new ChessPosition(i, j)));
                }
                break;
            }
        }
        for (int i = currentRow - 1, j = currentCol - 1; i >= 1 && j >= 1; i--, j--) { //down and left
            if(board.getPiece(new ChessPosition(i,j)) == null) {
                pieceMoves.add(new ChessMove(myPosition, new ChessPosition(i, j)));
            } else {
                if(board.getPiece(new ChessPosition(i,j)).getTeamColor() != currentColor) { //enemy piece, can take but blocked after that
                    pieceMoves.add(new ChessMove(myPosition, new ChessPosition(i, j)));
                }
                break;
            }
        }
        for (int i = currentRow + 1, j = currentCol - 1; i <= 8 && j >= 1; i++, j--) { //up and left
            if(board.getPiece(new ChessPosition(i,j)) == null) {
                pieceMoves.add(new ChessMove(myPosition, new ChessPosition(i, j)));
            } else {
                if(board.getPiece(new ChessPosition(i,j)).getTeamColor() != currentColor) { //enemy piece, can take but blocked after that
                    pieceMoves.add(new ChessMove(myPosition, new ChessPosition(i, j)));
                }
                break;
            }
        }

        return pieceMoves;
    }

    public static Collection<ChessMove> knightMovesCalculator(ChessBoard board, int currentCol, int currentRow, ChessGame.TeamColor currentColor){
        pieceMoves.clear();
        //check the 8 spots
        //if (they are on the board AND (are empty OR contain an enemy then move))
        int[] iSpots = {1, -1};
        int[] jSpots = {2, -2};
        for(int i : iSpots) {
            for(int j : jSpots) {
                if(currentRow+i >= 1 && currentRow+i <= 8 && currentCol+j >= 1 && currentCol+j <= 8) { //if on the board
                    if(board.getPiece(new ChessPosition(currentRow+i,currentCol+j)) == null || board.getPiece(new ChessPosition(currentRow+i,currentCol+j)).getTeamColor() != currentColor){ //empty or contains enemy
                        pieceMoves.add(new ChessMove(new ChessPosition(currentRow, currentCol), new ChessPosition(currentRow+i,currentCol+j)));
                    }
                }
                if(currentRow+j >= 1 && currentRow+j <= 8 && currentCol+i >= 1 && currentCol+i <= 8) { //if on the board
                    if(board.getPiece(new ChessPosition(currentRow+j,currentCol+i)) == null || board.getPiece(new ChessPosition(currentRow+j,currentCol+i)).getTeamColor() != currentColor){ //empty or contains enemy
                        pieceMoves.add(new ChessMove(new ChessPosition(currentRow, currentCol), new ChessPosition(currentRow+j,currentCol+i)));
                    }
                }
            }
        }

        return pieceMoves;
    }

    public static Collection<ChessMove> pawnMovesCalculator(ChessBoard board, int currentCol, int currentRow, ChessGame.TeamColor currentColor){
        pieceMoves.clear();
        if(currentColor == ChessGame.TeamColor.BLACK){ //moving towards row 0
            if(board.getPiece(new ChessPosition(currentRow - 1, currentCol)) == null) { //if the spot in front of it is empty then can move forwards
                if(currentRow == 2) {//on last row before promotion and can move forwards
                    pieceMoves.add(new ChessMove(new ChessPosition(2, currentCol), new ChessPosition(1, currentCol), ChessPiece.PieceType.QUEEN));
                    pieceMoves.add(new ChessMove(new ChessPosition(2, currentCol), new ChessPosition(1, currentCol), ChessPiece.PieceType.ROOK));
                    pieceMoves.add(new ChessMove(new ChessPosition(2, currentCol), new ChessPosition(1, currentCol), ChessPiece.PieceType.BISHOP));
                    pieceMoves.add(new ChessMove(new ChessPosition(2, currentCol), new ChessPosition(1, currentCol), ChessPiece.PieceType.KNIGHT));
                } else{
                    pieceMoves.add(new ChessMove(new ChessPosition(currentRow, currentCol), new ChessPosition(currentRow - 1, currentCol)));
                    if(currentRow == 7 && board.getPiece(new ChessPosition(5, currentCol)) == null) {//on home row and can move two spots
                        pieceMoves.add(new ChessMove(new ChessPosition(7, currentCol), new ChessPosition(5, currentCol)));
                    }
                }
            }
            if(currentCol != 1 && board.getPiece(new ChessPosition(currentRow - 1, currentCol - 1)) != null && board.getPiece(new ChessPosition(currentRow - 1, currentCol - 1)).getTeamColor() != currentColor) { //there is a piece available diagonally to take
                if(currentRow == 2) {//on last row before promotion and can move forwards
                    pieceMoves.add(new ChessMove(new ChessPosition(2, currentCol), new ChessPosition(1, currentCol - 1), ChessPiece.PieceType.QUEEN));
                    pieceMoves.add(new ChessMove(new ChessPosition(2, currentCol), new ChessPosition(1, currentCol - 1), ChessPiece.PieceType.ROOK));
                    pieceMoves.add(new ChessMove(new ChessPosition(2, currentCol), new ChessPosition(1, currentCol - 1), ChessPiece.PieceType.BISHOP));
                    pieceMoves.add(new ChessMove(new ChessPosition(2, currentCol), new ChessPosition(1, currentCol - 1), ChessPiece.PieceType.KNIGHT));
                }
                else {
                    pieceMoves.add(new ChessMove(new ChessPosition(currentRow, currentCol), new ChessPosition(currentRow - 1, currentCol - 1)));
                }
            }
            if(currentCol != 8 && board.getPiece(new ChessPosition(currentRow - 1, currentCol + 1)) != null && board.getPiece(new ChessPosition(currentRow - 1, currentCol + 1)).getTeamColor() != currentColor) { //there is a piece available diagonally to take
                if(currentRow == 2) {//on last row before promotion and can move forwards
                    pieceMoves.add(new ChessMove(new ChessPosition(2, currentCol), new ChessPosition(1, currentCol + 1), ChessPiece.PieceType.QUEEN));
                    pieceMoves.add(new ChessMove(new ChessPosition(2, currentCol), new ChessPosition(1, currentCol + 1), ChessPiece.PieceType.ROOK));
                    pieceMoves.add(new ChessMove(new ChessPosition(2, currentCol), new ChessPosition(1, currentCol + 1), ChessPiece.PieceType.BISHOP));
                    pieceMoves.add(new ChessMove(new ChessPosition(2, currentCol), new ChessPosition(1, currentCol + 1), ChessPiece.PieceType.KNIGHT));
                }
                else {
                    pieceMoves.add(new ChessMove(new ChessPosition(currentRow, currentCol), new ChessPosition(currentRow - 1, currentCol + 1)));
                }
            }
        } else { //white pieces moving towards row 7
            if(board.getPiece(new ChessPosition(currentRow + 1, currentCol)) == null) { //if the spot in front of it is empty then can move forwards
                if(currentRow == 7) {//on last row before promotion and can move forwards
                    pieceMoves.add(new ChessMove(new ChessPosition(7, currentCol), new ChessPosition(8, currentCol), ChessPiece.PieceType.QUEEN));
                    pieceMoves.add(new ChessMove(new ChessPosition(7, currentCol), new ChessPosition(8, currentCol), ChessPiece.PieceType.ROOK));
                    pieceMoves.add(new ChessMove(new ChessPosition(7, currentCol), new ChessPosition(8, currentCol), ChessPiece.PieceType.BISHOP));
                    pieceMoves.add(new ChessMove(new ChessPosition(7, currentCol), new ChessPosition(8, currentCol), ChessPiece.PieceType.KNIGHT));
                } else{
                    pieceMoves.add(new ChessMove(new ChessPosition(currentRow, currentCol), new ChessPosition(currentRow + 1, currentCol)));
                    if(currentRow == 2 && board.getPiece(new ChessPosition(4, currentCol)) == null) {//on home row and can move two spots
                        pieceMoves.add(new ChessMove(new ChessPosition(2, currentCol), new ChessPosition(4, currentCol)));
                    }
                }
            }
            if(currentCol != 8 && board.getPiece(new ChessPosition(currentRow + 1, currentCol + 1)) != null && board.getPiece(new ChessPosition(currentRow + 1, currentCol + 1)).getTeamColor() != currentColor) { //there is a piece available diagonally to take
                if(currentRow == 7) {//on last row before promotion and can move forwards
                    pieceMoves.add(new ChessMove(new ChessPosition(7, currentCol), new ChessPosition(8, currentCol + 1), ChessPiece.PieceType.QUEEN));
                    pieceMoves.add(new ChessMove(new ChessPosition(7, currentCol), new ChessPosition(8, currentCol + 1), ChessPiece.PieceType.ROOK));
                    pieceMoves.add(new ChessMove(new ChessPosition(7, currentCol), new ChessPosition(8, currentCol + 1), ChessPiece.PieceType.BISHOP));
                    pieceMoves.add(new ChessMove(new ChessPosition(7, currentCol), new ChessPosition(8, currentCol + 1), ChessPiece.PieceType.KNIGHT));
                }
                else {
                    pieceMoves.add(new ChessMove(new ChessPosition(currentRow, currentCol), new ChessPosition(currentRow + 1, currentCol + 1)));
                }            }
            if(currentCol != 1 && board.getPiece(new ChessPosition(currentRow + 1, currentCol - 1)) != null && board.getPiece(new ChessPosition(currentRow + 1, currentCol - 1)).getTeamColor() != currentColor) { //there is a piece available diagonally to take
                if(currentRow == 7) {//on last row before promotion and can move forwards
                    pieceMoves.add(new ChessMove(new ChessPosition(7, currentCol), new ChessPosition(8, currentCol - 1), ChessPiece.PieceType.QUEEN));
                    pieceMoves.add(new ChessMove(new ChessPosition(7, currentCol), new ChessPosition(8, currentCol - 1), ChessPiece.PieceType.ROOK));
                    pieceMoves.add(new ChessMove(new ChessPosition(7, currentCol), new ChessPosition(8, currentCol - 1), ChessPiece.PieceType.BISHOP));
                    pieceMoves.add(new ChessMove(new ChessPosition(7, currentCol), new ChessPosition(8, currentCol - 1), ChessPiece.PieceType.KNIGHT));
                }
                else {
                    pieceMoves.add(new ChessMove(new ChessPosition(currentRow, currentCol), new ChessPosition(currentRow + 1, currentCol - 1)));
                }
            }
        }

        return pieceMoves;
    }

}
