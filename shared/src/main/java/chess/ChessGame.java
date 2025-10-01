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

    ChessBoard board;
    TeamColor turn;
    ChessPosition[] kingPositions = new ChessPosition[3];

    @Override
    public int hashCode() {
        return board.hashCode() + turn.ordinal();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && turn == chessGame.turn;
    }

    public void setKingPositions() {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                if (board.getPiece(position) != null &&
                        board.getPiece(position).getPieceType() == ChessPiece.PieceType.KING) {
                    kingPositions[board.getPiece(position).getTeamColor().ordinal()] = position;
                }
            }
        }
    }

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        turn = ChessGame.TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
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
        Collection<ChessMove> moves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        Collection<ChessMove> invalidMoves = new ArrayList<>();
        ChessGame testGame = new ChessGame();
        ChessBoard testBoard = new ChessBoard();
        for (ChessMove move : moves) {

            // deep copy of board
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 8; j++) {
                    ChessPosition testPosition = new ChessPosition(i, j);
                    ChessPiece testPiece = board.getPiece(testPosition);
                    testBoard.addPiece(testPosition, null);
                    if (testPiece != null) {
                        testBoard.addPiece(testPosition, new ChessPiece(testPiece.getTeamColor(), testPiece.getPieceType()));
                    }
                }
            }
            testGame.setBoard(testBoard);

            try {
                testGame.makeMove(move);
            } catch (InvalidMoveException x) {
                invalidMoves.add(move);
            }
        }
        for (ChessMove move : invalidMoves) {
            moves.remove(move);
        }
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (move == null) {throw new InvalidMoveException();}
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null || piece.getTeamColor() != turn) {throw new InvalidMoveException();}
        if (!piece.pieceMoves(board, move.getStartPosition()).contains(move)) {throw new InvalidMoveException();}

        board.addPiece(move.getStartPosition(), null);
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN &&
                ((move.getEndPosition().getRow() == 8 && piece.getTeamColor() == TeamColor.WHITE) ||
                 (move.getEndPosition().getRow() == 1 && piece.getTeamColor() == TeamColor.BLACK))){
            board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }
        else board.addPiece(move.getEndPosition(), piece);

        if (isInCheck(piece.getTeamColor())) {
            throw new InvalidMoveException();
        }

        if (turn == TeamColor.WHITE) turn = TeamColor.BLACK;
        else turn = TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        boolean inCheck = false;
        setKingPositions();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                if (board.getPiece(position) != null && !board.getPiece(position).getTeamColor().equals(teamColor)) {
                    Collection<ChessMove> moves = board.getPiece(position).pieceMoves(board, position);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(kingPositions[teamColor.ordinal()])) {
                            inCheck = true;
                            i = 9;
                            j = 9;
                            break;
                        }
                    }
                }
            }
        }
        return inCheck;
    }

    public boolean hasNoValidMoves(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece checkPiece = board.getPiece(position);
                if (checkPiece != null &&
                        checkPiece.getTeamColor() == teamColor &&
                        !validMoves(position).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return hasNoValidMoves(teamColor) && isInCheck(teamColor) && teamColor == turn;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return hasNoValidMoves(teamColor) && !isInCheck(teamColor) && teamColor == turn;
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
