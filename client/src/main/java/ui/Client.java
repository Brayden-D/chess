package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import facade.ServerFacade;
import model.GameData;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Client {

    boolean isLoggedIn = false;
    Scanner sc = new Scanner(System.in);
    public ServerFacade server = new ServerFacade();


    public void runREPL() {
        while (true) {
            printPrompt();
            String[] tokens = sc.nextLine().trim().split(" ");
            String cmd = tokens[0].toLowerCase();

            try {
                switch (cmd) {
                    case "help", "h" -> printCommands();
                    case "quit", "q" -> quit();
                    case "register", "r" -> register(tokens);
                    case "login", "li" -> login(tokens);
                    case "logout", "lo" -> logout();
                    case "create", "c" -> create(tokens);
                    case "list", "l" -> listGames();
                    case "join", "j" -> join(tokens);
                    case "observe", "o" -> observe(tokens);
                    default -> unknown(cmd);
                }
            } catch (Exception e) {
                if (e.getMessage().contains("401")) {
                    System.out.println("Invalid username or password\n");
                } else if (e.getMessage().contains("403")) {
                    System.out.println("Already taken\n");
                } else if (e.getMessage().contains("out of bounds")) {
                    System.out.println("invalid input\n");
                } else if (e.getMessage().contains("For input string:")) {
                    System.out.println("invalid input\n");
                }else {
                    System.out.println(e.getMessage() + "\n");
                }
            }
        }
    }

    public void playGame() {
        while (true) {
            System.out.print(SET_TEXT_COLOR_WHITE +
                    "[playing game] > " +
                    RESET_TEXT_COLOR);
            String[] tokens = sc.nextLine().trim().split(" ");
            String cmd = tokens[0].toLowerCase();
            try {
                switch (cmd) {
                    case "help", "h" -> printCommands();
                    case "redraw", "r" -> unknown("redraw");
                    case "leave", "l" -> unknown("leave");
                    case "move", "m" -> unknown("move");
                    case "resign", "forfeit", "f" -> unknown("resign");
                    case "highlight", "hl" -> unknown("highlight");
                    default -> unknown(cmd);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage() + "\n");
            }
        }
    }

    private void printPrompt() {
        System.out.print(SET_TEXT_COLOR_WHITE +
                (isLoggedIn ? "[logged in] > " : "[logged out] > ") +
                RESET_TEXT_COLOR);
    }

    private void requireArgs(String[] t, int expected) throws Exception {
        if (t.length != expected) {
            throw new Exception("Wrong number of arguments!");
        }
    }

    private void requireLoggedIn() throws Exception {
        if (!isLoggedIn) {
            throw new Exception("No user logged in!");
        }
    }

    private void requireLoggedOut() throws Exception {
        if (isLoggedIn) {
            throw new Exception("User already logged in!");
        }
    }

    private void quit() throws Exception {
        if (isLoggedIn) {
            throw new Exception("You must log out before quitting");
        }
        System.out.println(SET_TEXT_COLOR_YELLOW + "Goodbye!\n" + RESET_TEXT_COLOR);
        System.exit(0);
    }

    private void register(String[] t) throws Exception {
        requireLoggedOut();
        requireArgs(t, 4);
        server.register(t[1], t[2], t[3]);
        isLoggedIn = true;
        System.out.println("Successfully registered user!\n");
    }

    private void login(String[] t) throws Exception {
        requireLoggedOut();
        requireArgs(t, 3);
        server.login(t[1], t[2]);
        isLoggedIn = true;
        System.out.println("Successfully logged in!\n");
    }

    private void logout() throws Exception {
        requireLoggedIn();
        server.logout();
        isLoggedIn = false;
        System.out.println("Successfully logged out!\n");
    }

    private void create(String[] t) throws Exception {
        requireLoggedIn();
        requireArgs(t, 2);
        server.createGame(t[1]);
        System.out.println("Successfully created game!\n");
    }

    private void listGames() throws Exception {
        requireLoggedIn();
        var games = server.listGames();
        for (int i = 0; i < games.size(); i++) {
            var g = games.get(i);
            System.out.printf(
                    "%d. %s\n   White Player: %s\n   Black Player: %s\n",
                    i, g.gameName(),
                    g.whiteUsername() == null ? "---" : g.whiteUsername(),
                    g.blackUsername() == null ? "---" : g.blackUsername()
            );
        }
        System.out.println();
    }

    private void join(String[] t) throws Exception {
        requireLoggedIn();
        requireArgs(t, 3);
        int index = Integer.parseInt(t[2]);
        GameData game = server.listGames().get(index);
        ChessGame.TeamColor color = parseTeamColor(t[1]);

        if ((game.whiteUsername() != null && color == ChessGame.TeamColor.WHITE) ||
                (game.blackUsername() != null && color == ChessGame.TeamColor.BLACK)) {
            throw new Exception("Spot already taken!");
        }

        server.playGame(color, game.gameID());
        server.joinWebSocket(game.gameID(), color.name());
        game = server.listGames().get(index);
        printGame(game, color);
        
        playGame();
    }

    private void observe(String[] t) throws Exception {
        requireLoggedIn();
        requireArgs(t, 2);
        GameData game = server.listGames().get(Integer.parseInt(t[1]));
        server.joinWebSocket(game.gameID(), "observe");
        printGame(game, ChessGame.TeamColor.WHITE);
    }

    private void unknown(String cmd) {
        System.out.println(SET_TEXT_COLOR_RED +
                "Unknown command: \"" + cmd + "\". Type \"help\" for a list of commands.\n" +
                RESET_TEXT_COLOR);
    }

    private ChessGame.TeamColor parseTeamColor(String color) throws Exception {
        return switch (color.toLowerCase()) {
            case "w", "white" -> ChessGame.TeamColor.WHITE;
            case "b", "black" -> ChessGame.TeamColor.BLACK;
            default -> throw new Exception("Invalid color");
        };
    }

    private void printCommands() {
        if (!isLoggedIn) {
            System.out.println("""
                    help: lists commands
                    quit: terminates program
                    login [username] [password]: logs an existing user in
                    register [username] [password] [email]: registers a new user and logs them in 

                    """);
        } else {
            System.out.println("""
                    help: lists commands
                    logout: logs user out
                    create [gamename]: creates a new game with specified name
                    list: list all games currently on the server
                    join [color] [gamenumber]: join specified game as the specified color
                    observe [gamenumber]: observe an active game 

                    """);
        }
    }

    private void printGame(GameData data, ChessGame.TeamColor color) throws Exception {
        ChessBoard board = data.game().getBoard();
        boolean isWhite = (color == ChessGame.TeamColor.WHITE);

        if (isWhite) {
            System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK +
                    "    a  b  c  d  e  f  g  h    " +
                    RESET_BG_COLOR + RESET_TEXT_COLOR + "\n");
        } else {
            System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK +
                    "    h  g  f  e  d  c  b  a    " +
                    RESET_BG_COLOR + RESET_TEXT_COLOR + "\n");
        }

        for (int displayRow = 0; displayRow < 8; displayRow++) {
            int row = isWhite ? displayRow : 7 - displayRow;
            int printedRank = isWhite ? 8 - displayRow : displayRow + 1;

            System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK +
                    " " + printedRank + " " +
                    RESET_BG_COLOR + RESET_TEXT_COLOR);

            for (int displayCol = 0; displayCol < 8; displayCol++) {
                int col = isWhite ? displayCol : 7 - displayCol;
                boolean lightSquare = (row + col) % 2 == 0;
                System.out.print(lightSquare ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK);
                printPiece(board.getPiece(new ChessPosition(row + 1, col + 1)));
                System.out.print(RESET_BG_COLOR);
            }

            System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK +
                    " " + printedRank + " " +
                    RESET_BG_COLOR + RESET_TEXT_COLOR + "\n");
        }

        if (isWhite) {
            System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK +
                    "    a  b  c  d  e  f  g  h    " +
                    RESET_BG_COLOR + RESET_TEXT_COLOR + "\n\n");
        } else {
            System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK +
                    "    h  g  f  e  d  c  b  a    " +
                    RESET_BG_COLOR + RESET_TEXT_COLOR + "\n\n");
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
