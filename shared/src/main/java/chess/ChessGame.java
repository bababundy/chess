package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board = new ChessBoard();
    private TeamColor teamTurn;

    public ChessGame() {
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
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
    public Collection<ChessMove> validMoves(ChessPosition startPosition){
        return validMoves(board, startPosition);
    }

    public Collection<ChessMove> validMoves(ChessBoard workingBoard, ChessPosition startPosition){
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece currentPiece = workingBoard.getPiece(startPosition); //get the piece at the start position
        if (currentPiece == null) return null;
        Collection<ChessMove> pieceMoves = currentPiece.pieceMoves(workingBoard, startPosition); //get that pieces possible moves
        for(ChessMove possibleMove : pieceMoves){ //for each move in pieceMoves
            ChessBoard copyBoard = workingBoard.clone(); // (create deep copy of the board and move it?)
            try {
                makeMove(copyBoard, possibleMove);
                if (!isInCheck(copyBoard, currentPiece.getTeamColor())) {
                    validMoves.add(possibleMove);
                }
            } catch (InvalidMoveException e) {
                //ignore
            }
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
        makeMove(board, move);
    }

    public void makeMove(ChessBoard workingBoard, ChessMove move) throws InvalidMoveException { //overload that accepts a board
        ChessPiece currentPiece = workingBoard.getPiece(move.getStartPosition());
        if(currentPiece == null) {
            throw new InvalidMoveException();
        }
        Collection<ChessMove> pieceMoves = currentPiece.pieceMoves(workingBoard, move.getStartPosition());

        if(workingBoard == this.board && currentPiece.getTeamColor() == teamTurn) {
            if(pieceMoves.contains(move)){
                workingBoard.removePiece(move.getEndPosition());
                if(move.getPromotionPiece() != null){
                    workingBoard.addPiece(move.getEndPosition(), new ChessPiece(teamTurn, move.getPromotionPiece()));
                } else {
                    workingBoard.addPiece(move.getEndPosition(), workingBoard.getPiece(move.getStartPosition()));
                }
                workingBoard.removePiece(move.getStartPosition());
            } else{
                throw new InvalidMoveException();
            }
            if (isInCheck(workingBoard, teamTurn)) {
                throw new InvalidMoveException("In Check");
            }
            if(teamTurn == TeamColor.WHITE){
                teamTurn = TeamColor.BLACK;
            } else {
                teamTurn = TeamColor.WHITE;
            }
        } else if (workingBoard == this.board && currentPiece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Incorrect Turn");
        } else {
            if (pieceMoves.contains(move)) {
                workingBoard.removePiece(move.getEndPosition());
                if (move.getPromotionPiece() != null) {
                    workingBoard.addPiece(move.getEndPosition(), new ChessPiece(teamTurn, move.getPromotionPiece()));
                } else {
                    workingBoard.addPiece(move.getEndPosition(), workingBoard.getPiece(move.getStartPosition()));
                }
                workingBoard.removePiece(move.getStartPosition());
            } else {
                throw new InvalidMoveException();
            }
            if (isInCheck(workingBoard, teamTurn)) {
                throw new InvalidMoveException("In Check");
            }
        }
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
        return isInCheck(board, teamColor);
    }

    /**
     * Determines if the given team is in check
     *
     * @param workingBoard the board state to check
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(ChessBoard workingBoard, TeamColor teamColor) { //overload with a board
        //for each spot on the board
        for(int row = 1; row <= 8; row++) {
            for(int col = 1; col <= 8; col++) {
                //if opponent and the opposite color
                ChessPosition currentSpot = new ChessPosition(row, col);
                ChessPiece currentPiece = workingBoard.getPiece(currentSpot);
                if(currentPiece != null && currentPiece.getTeamColor() != teamColor) {
                    //if their valid moves contain a piece and it's a king
                    Collection<ChessMove> dangerSpots = currentPiece.pieceMoves(workingBoard, currentSpot);
                    for(ChessMove danger : dangerSpots){
                        ChessPiece target = workingBoard.getPiece(danger.getEndPosition());
                        if(target != null && target.getPieceType() == ChessPiece.PieceType.KING) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}
