package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece currentPiece = board.getPiece(myPosition);
        PieceType currentType = currentPiece.getPieceType();
        ChessGame.TeamColor currentColor = currentPiece.getTeamColor();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();

        return switch (currentType) {
            case KING -> PieceMovesCalculator.kingMovesCalculator(board, currentCol, currentRow, currentColor);
            case QUEEN -> PieceMovesCalculator.queenMovesCalculator(board, currentCol, currentRow, currentColor);
            case ROOK -> PieceMovesCalculator.rookMovesCalculator(board, currentCol, currentRow, currentColor);
            case BISHOP -> PieceMovesCalculator.bishopMovesCalculator(board, myPosition, currentColor);
            case KNIGHT -> PieceMovesCalculator.knightMovesCalculator(board, currentCol, currentRow, currentColor);
            case PAWN -> PieceMovesCalculator.pawnMovesCalculator(board, currentCol, currentRow, currentColor);
            default -> new ArrayList<>();
        };
    }
}
