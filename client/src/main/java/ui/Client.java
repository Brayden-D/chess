package ui;

import chess.ChessGame;
import facade.ServerFacade;
import model.GameData;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Client {

    Printer printer =  new Printer();
    boolean isLoggedIn = false;
    boolean isInGame = false;
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
        isInGame = true;
        boolean playing = true;
        while (playing) {
            String input = sc.nextLine();
            String[] tokens = input.trim().split(" ");
            String cmd = tokens[0].toLowerCase();
            try {
                switch (cmd) {
                    case "help", "h" -> printCommands();
                    case "redraw", "r" -> unknown("redraw");
                    case "leave", "l" -> {
                        server.leave();
                        playing = false;
                    }
                    case "move", "m" -> server.move(input);
                    case "resign", "forfeit", "f" -> server.resign();
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
        server.joinWebSocket(game.gameID(), color);
        
        playGame();
    }

    private void observe(String[] t) throws Exception {
        requireLoggedIn();
        requireArgs(t, 2);
        GameData game = server.listGames().get(Integer.parseInt(t[1]));
        server.playGame(ChessGame.TeamColor.OBSERVER, game.gameID());
        server.joinWebSocket(game.gameID(), ChessGame.TeamColor.OBSERVER);
        playGame();
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
        } else if (!isInGame) {
            System.out.println("""
                    help: lists commands
                    logout: logs user out
                    create [gamename]: creates a new game with specified name
                    list: list all games currently on the server
                    join [color] [gamenumber]: join specified game as the specified color
                    observe [gamenumber]: observe an active game 

                    """);
        } else {
            System.out.println("""
                    help: lists commands
                    redraw: redraws the board
                    leave: leaves game
                    move [start square] [end square]: make a move if it is your turn
                    resign: concede the game
                    highlight [square]: highlight the square and all moves the piece on that square can make
                    
                    """);
        }
    }

}
