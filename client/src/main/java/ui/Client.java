package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import facade.ServerFacade;
import model.GameData;

import java.util.ArrayList;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Client {

    boolean isLoggedIn = false;
    Scanner sc = new Scanner(System.in);
    String input;
    ServerFacade server = new ServerFacade();

    public void runREPL() {
        while(true) {
            if(!isLoggedIn) {
                System.out.print(SET_TEXT_COLOR_WHITE + "[logged out] > " + RESET_TEXT_COLOR);
            } else {
                System.out.print(SET_TEXT_COLOR_WHITE + "[logged in] > " + RESET_TEXT_COLOR);
            }

            System.out.flush();
            String[] tokens = sc.nextLine().split(" ");

            switch (tokens[0]) {
                case "help":
                case "h":
                    printCommands();
                    break;

                case "quit":
                case "q":
                    if(isLoggedIn) {
                        System.out.println("You must log out before quitting\n");
                        break;
                    }
                    System.out.println(SET_TEXT_COLOR_YELLOW+
                            "Goodbye!\n" +
                            RESET_TEXT_COLOR);
                    System.exit(0);
                    break;

                case "register":
                case "r":
                    if (isLoggedIn) {
                        System.out.println("User already logged in!\n");
                        break;
                    }
                    try {
                        server.register(tokens[1], tokens[2], tokens[3]);
                        System.out.println("Successfully registered user!\n");
                        isLoggedIn = true;
                    } catch (Exception e) {
                        System.out.println(e.getMessage() + "\n");
                    }
                    break;

                case "login":
                case "li":
                    if (isLoggedIn) {
                        System.out.println("User already logged in!\n");
                        break;
                    }
                    try {
                        server.login(tokens[1], tokens[2]);
                        System.out.println("Successfully logged in!\n");
                        isLoggedIn = true;
                    } catch (Exception e) {
                        System.out.println(e.getMessage() + "\n");
                    }
                    break;

                case "logout":
                case "lo":
                    if (!isLoggedIn) {
                        System.out.println("No user logged in!\n");
                        break;
                    }
                    try {
                        server.logout();
                        System.out.println("Successfully logged out!\n");
                        isLoggedIn = false;
                    } catch (Exception e) {
                        System.out.println(e.getMessage() + "\n");
                    }
                    break;

                case "create":
                case "c":
                    if (!isLoggedIn) {
                        System.out.println("No user logged in!\n");
                        break;
                    }
                    try {
                        server.createGame(tokens[1]);
                        System.out.println("Successfully created game!\n");
                    } catch (Exception e) {
                        System.out.println(e.getMessage() + "\n");
                    }
                    break;

                case "list":
                case "l":
                    if (!isLoggedIn) {
                        System.out.println("No user logged in!\n");
                        break;
                    }
                    try {
                        ArrayList<GameData> data = server.listGames();
                        for (int i = 0; i < data.size(); i++) {
                            GameData g = data.get(i);
                            String white = (g.whiteUsername() != null) ? g.whiteUsername() : "---";
                            String black = (g.blackUsername() != null) ? g.blackUsername() : "---";

                            System.out.println(i + ". " + g.gameName() +
                                    "\n   White Player: " + white +
                                    "\n   Black Player: " + black);
                        }
                        System.out.println();
                    } catch (Exception e) {
                        System.out.println(e.getMessage() + "\n");
                    }
                    break;

                case "join":
                case "j":
                    if (!isLoggedIn) {
                        System.out.println("No user logged in!\n");
                        break;
                    }
                    try {
                        GameData gameData = server.listGames().get(Integer.parseInt(tokens[2]));
                        ChessGame.TeamColor color = parseTeamColor(tokens[1]);
                        server.playGame(color, gameData.gameID());
                        gameData = server.listGames().get(Integer.parseInt(tokens[2]));
                        printGame(gameData, color);
                    } catch (Exception e) {
                        System.out.println(e.getMessage() + "\n");
                    }
                    break;

                case "observe":
                case "o":
                    if (!isLoggedIn) {
                        System.out.println("No user logged in!\n");
                        break;
                    }
                    try {
                        GameData gameData = server.listGames().get(Integer.parseInt(tokens[1]));
                        printGame(gameData, ChessGame.TeamColor.WHITE);
                    } catch (Exception e) {
                        System.out.println(e.getMessage() + "\n");
                    }
                    break;


                default:
                    System.out.println(SET_TEXT_COLOR_RED +
                            "Unknown command: \"" + input + "\". Type \"help\" for a list of commands.\n" +
                            RESET_TEXT_COLOR);
                    break;
            }



        }
    }

    private ChessGame.TeamColor parseTeamColor(String color) throws Exception {
        ChessGame.TeamColor playerColor;
        if (color.equals("w") ||
                color.equals("white") ||
                color.equals("White") ||
                color.equals("WHITE") ||
                color.equals("W")) {
            playerColor = ChessGame.TeamColor.WHITE;
        } else if (color.equals("b") ||
                color.equals("black") ||
                color.equals("Black") ||
                color.equals("BLACK") ||
                color.equals("B")) {
            playerColor = ChessGame.TeamColor.BLACK;
        } else  {
            throw new Exception("Invalid color");
        }
        return playerColor;
    }

    private void printCommands() {
        if(!isLoggedIn) {
            System.out.println("""
                                help: lists commands
                                quit: terminates program
                                login [username] [password]: logs an existing user in
                                register [username] [password] [email]: registers a new user and logs them in \n
                                """);
        } else {
            System.out.println("""
                                help: lists commands
                                logout: logs user out
                                create [gamename]: creates a new game with specified name
                                list: list all games currently on the server
                                play [color] [gamenumber]: join specified game as the specified color
                                observe [gamenumber]: observe an active game \n
                                """);
        }
    }

    private void printGame(GameData data, ChessGame.TeamColor color) throws Exception {
        ChessBoard board = data.game().getBoard();
        boolean isWhite = (color == ChessGame.TeamColor.WHITE);

        if (isWhite) {
            System.out.print(
                    SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK +
                            "    a  b  c  d  e  f  g  h    " +
                            RESET_BG_COLOR + RESET_TEXT_COLOR + "\n"
            );
        } else {
            System.out.print(
                    SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK +
                            "    h  g  f  e  d  c  b  a    " +
                            RESET_BG_COLOR + RESET_TEXT_COLOR + "\n"
            );
        }

        for (int displayRow = 0; displayRow < 8; displayRow++) {
            int row = isWhite ? displayRow : (7 - displayRow);
            int printedRank = isWhite ? (8 - displayRow) : (displayRow + 1);

            System.out.print(
                    SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK +
                            " " + printedRank + " " +
                            RESET_BG_COLOR + RESET_TEXT_COLOR
            );

            for (int displayCol = 0; displayCol < 8; displayCol++) {
                int col = isWhite ? displayCol : (7 - displayCol);

                boolean lightSquare = (row + col) % 2 == 0;
                System.out.print(lightSquare ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK);

                printPiece(board.getPiece(new ChessPosition(row + 1, col + 1)));

                System.out.print(RESET_BG_COLOR);
            }

            System.out.print(
                    SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK +
                            " " + printedRank + " " +
                            RESET_BG_COLOR + RESET_TEXT_COLOR + "\n"
            );
        }

        if (isWhite) {
            System.out.print(
                    SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK +
                            "    a  b  c  d  e  f  g  h    " +
                            RESET_BG_COLOR + RESET_TEXT_COLOR + "\n\n"
            );
        } else {
            System.out.print(
                    SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK +
                            "    h  g  f  e  d  c  b  a    " +
                            RESET_BG_COLOR + RESET_TEXT_COLOR + "\n\n"
            );
        }
    }


    void printPiece(ChessPiece piece) {
        if (piece == null) {
            System.out.print("   ");
            return;
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            System.out.print(SET_TEXT_COLOR_RED);
        } else {
            System.out.print(SET_TEXT_COLOR_BLUE);
        }

        String symbol = switch (piece.getPieceType()) {
            case KING -> " K ";
            case QUEEN -> " Q ";
            case ROOK -> " R ";
            case BISHOP -> " B ";
            case KNIGHT -> " N ";
            case PAWN -> " P ";
        };

        System.out.print(symbol);
        System.out.print(RESET_TEXT_COLOR);
    }

}
