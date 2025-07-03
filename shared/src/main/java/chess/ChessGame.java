package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        //get the piece at the start position
        ChessPiece currentPiece = board.getPiece(startPosition);
        //get that pieces possible moves
        Collection<ChessMove> pieceMoves = currentPiece.pieceMoves(board, startPosition);
        //for each move in possiblemoves
        for(ChessMove possibleMove : pieceMoves){
            // (create deep copy of the board and move it?)
                //if moving != inCheck
                    //add to new collection
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //if move is in valid moves
        if(validMoves(move.getStartPosition()).contains(move)){
            //make the actual move in the overload method with the original board passed in
            makeMove(board, move);
        }
    }

    public void makeMove(ChessBoard workingBoard, ChessMove move) throws InvalidMoveException { //overload that accepts a board
        workingBoard.addPiece(move.getEndPosition(), workingBoard.getPiece(move.getStartPosition()));
        workingBoard.removePiece(move.getStartPosition());

        //if moving the king or one of the rooks and the board is the real board
            // then set their flag in chessPiece to false in order to show that they have been moved
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) { //overload with a board
        //for each of the opposite teamcolor pieces, do any of their pieces valid moves include the spot the king is on?
        //for each spot on the board
            //if opponent and the opposite color
                //if their valid moves contain a piece and it's a king
                    //return true
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //if teamcolor is in check
        //if none of teamcolors pieces have valid moves then checkmate
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //if not in check
        //for each piece on the board, do they have any valid moves
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
