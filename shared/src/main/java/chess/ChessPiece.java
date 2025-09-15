package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.lang.Math;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    ChessGame.TeamColor color;
    PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        color = pieceColor;
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
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     *
     * @param board chess board
     * @param pos chess position
     * @param piece piece to be checked against
     * @return 0 if no piece, -1 if different colors, 1 if same color, 2 if out of bounds
     */
    private int checkCollision(ChessBoard board, ChessPiece piece, ChessPosition pos) {
        int tempRow = pos.getRow();
        int tempCol = pos.getColumn();
        if (tempRow < 1 || tempCol < 1 || tempRow > 8 || tempCol > 8) {
            return 2;
        }
        ChessPiece checkPiece = board.getPiece(new ChessPosition(tempRow, tempCol));
        if (checkPiece != null) {
            if (piece.getTeamColor() == checkPiece.getTeamColor()) {
                return 1;
            } else {
                return -1;
            }
        }
        return 0;
    }

    private void checkDirection(ChessBoard board, ChessPosition myPosition, int rowDir, int colDir, Collection<ChessMove> moves) {
        int tempRow = myPosition.getRow();
        int tempCol = myPosition.getColumn();
        ChessPiece checkPiece = board.getPiece(myPosition);
        while(true) {
            tempCol += colDir;
            tempRow += rowDir;
            int check = checkCollision(board, checkPiece, new ChessPosition(tempRow, tempCol));
            if (check > 0) break;
            moves.add(new ChessMove(myPosition, new ChessPosition(tempRow, tempCol), null));
            if (check == -1) break;
        }
    }

    private void addPromotionMoves(ChessPosition pos, ChessPosition newPos,  Collection<ChessMove> moves) {
        moves.add(new ChessMove(pos, newPos, PieceType.QUEEN));
        moves.add(new ChessMove(pos, newPos, PieceType.ROOK));
        moves.add(new ChessMove(pos, newPos, PieceType.BISHOP));
        moves.add(new ChessMove(pos, newPos, PieceType.KNIGHT));
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(pos);
        int row, col;
        ChessPosition newPos;
        switch(piece.getPieceType()) {
            case PieceType.PAWN:
                int color;
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {color = 1;}
                else {color = -1;}
                row = pos.getRow();
                col = pos.getColumn();
                newPos = new ChessPosition(row + color, col);
                if (checkCollision(board, piece, newPos) == 0) {
                    if (row == 4.5 + (color * 2.5)) {
                        addPromotionMoves(pos, newPos, moves);
                    }
                    else moves.add(new ChessMove(pos, newPos, null));
                    if (row == 4.5 - (color * 2.5)) {
                        newPos = new ChessPosition(row + (color * 2), col);
                        if (checkCollision(board, piece, newPos) == 0) moves.add(new ChessMove(pos, newPos, null));
                    }
                }
                newPos = new ChessPosition(row + color, col + 1);
                if (checkCollision(board, piece, newPos) == -1) {
                    if (row == 4.5 + (color * 2.5)) {
                        addPromotionMoves(pos, newPos, moves);
                    }
                    else moves.add(new ChessMove(pos, newPos, null));
                }
                newPos = new ChessPosition(row + color, col - 1);
                if (checkCollision(board, piece, newPos) == -1) {
                    if (row == 4.5 + (color * 2.5)) {
                        addPromotionMoves(pos, newPos, moves);
                    }
                    else moves.add(new ChessMove(pos, newPos, null));
                }
                break;
            case PieceType.KNIGHT:
                row = pos.getRow();
                col = pos.getColumn();
                newPos = new ChessPosition(row + 2, col + 1);
                if (checkCollision(board, piece, newPos) <= 0) moves.add(new ChessMove(pos, newPos, null));
                newPos = new ChessPosition(row + 2, col - 1);
                if (checkCollision(board, piece, newPos) <= 0) moves.add(new ChessMove(pos, newPos, null));
                newPos = new ChessPosition(row - 2, col + 1);
                if (checkCollision(board, piece, newPos) <= 0) moves.add(new ChessMove(pos, newPos, null));
                newPos = new ChessPosition(row - 2, col - 1);
                if (checkCollision(board, piece, newPos) <= 0) moves.add(new ChessMove(pos, newPos, null));
                newPos = new ChessPosition(row + 1, col + 2);
                if (checkCollision(board, piece, newPos) <= 0) moves.add(new ChessMove(pos, newPos, null));
                newPos = new ChessPosition(row - 1, col + 2);
                if (checkCollision(board, piece, newPos) <= 0) moves.add(new ChessMove(pos, newPos, null));
                newPos = new ChessPosition(row + 1, col - 2);
                if (checkCollision(board, piece, newPos) <= 0) moves.add(new ChessMove(pos, newPos, null));
                newPos = new ChessPosition(row - 1, col - 2);
                if (checkCollision(board, piece, newPos) <= 0) moves.add(new ChessMove(pos, newPos, null));
                break;
            case PieceType.BISHOP:
                checkDirection(board, pos, 1, 1, moves);
                checkDirection(board, pos, 1, -1, moves);
                checkDirection(board, pos, -1, 1, moves);
                checkDirection(board, pos, -1, -1, moves);
                break;
            case PieceType.ROOK:
                checkDirection(board, pos, 0, 1, moves);
                checkDirection(board, pos, 0, -1, moves);
                checkDirection(board, pos, 1, 0, moves);
                checkDirection(board, pos, -1, 0, moves);
                break;
            case PieceType.QUEEN:
                checkDirection(board, pos, 1, 1, moves);
                checkDirection(board, pos, 1, -1, moves);
                checkDirection(board, pos, -1, 1, moves);
                checkDirection(board, pos, -1, -1, moves);
                checkDirection(board, pos, 0, 1, moves);
                checkDirection(board, pos, 0, -1, moves);
                checkDirection(board, pos, 1, 0, moves);
                checkDirection(board, pos, -1, 0, moves);
                break;
            case PieceType.KING:
                row = pos.getRow();
                col = pos.getColumn();
                newPos = new ChessPosition(row + 1, col);
                if (checkCollision(board, piece, newPos) <= 0) moves.add(new ChessMove(pos, newPos, null));
                newPos = new ChessPosition(row -1, col);
                if (checkCollision(board, piece, newPos) <= 0) moves.add(new ChessMove(pos, newPos, null));
                newPos = new ChessPosition(row, col + 1);
                if (checkCollision(board, piece, newPos) <= 0) moves.add(new ChessMove(pos, newPos, null));
                newPos = new ChessPosition(row, col - 1);
                if (checkCollision(board, piece, newPos) <= 0) moves.add(new ChessMove(pos, newPos, null));
                newPos = new ChessPosition(row + 1, col + 1);
                if (checkCollision(board, piece, newPos) <= 0) moves.add(new ChessMove(pos, newPos, null));
                newPos = new ChessPosition(row + 1, col - 1);
                if (checkCollision(board, piece, newPos) <= 0) moves.add(new ChessMove(pos, newPos, null));
                newPos = new ChessPosition(row - 1, col + 1);
                if (checkCollision(board, piece, newPos) <= 0) moves.add(new ChessMove(pos, newPos, null));
                newPos = new ChessPosition(row - 1, col - 1);
                if (checkCollision(board, piece, newPos) <= 0) moves.add(new ChessMove(pos, newPos, null));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + board.getPiece(pos));
        }
        return moves;
    }
}
