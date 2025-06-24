package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()][position.getColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()][position.getColumn()];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        squares = new ChessPiece[8][8]; //fresh reset, might need to manually go and clear if this doesn't work
        for(int i = 0; i < 8; i++) { //go through each column and add all the pieces
            //pawns will be placed on both sides with each i
            addPiece(new ChessPosition(1,i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(6,i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
            if(i == 0 || i == 7){ //rooks go in the four corners
                addPiece(new ChessPosition(0,i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
                addPiece(new ChessPosition(7,i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
            }
            if(i == 1 || i == 6){ //knights go on columns 1 and 6
                addPiece(new ChessPosition(0,i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
                addPiece(new ChessPosition(7,i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
            }
            if(i == 2 || i == 5){ //bishops go on columns 2 and 5
                addPiece(new ChessPosition(0,i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
                addPiece(new ChessPosition(7,i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
            }
            if(i == 3){ //queens go on column 3
                addPiece(new ChessPosition(0,i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
                addPiece(new ChessPosition(7,i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
            }
            if(i == 4){ //kings go on column 4
                addPiece(new ChessPosition(0,i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
                addPiece(new ChessPosition(7,i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
            }
        }
    }
}
