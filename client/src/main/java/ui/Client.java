package ui;

import chess.ChessGame;
import facade.ServerFacade;
import model.GameData;

import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    boolean isLoggedIn = false;
    Scanner sc = new Scanner(System.in);
    String input;
    ServerFacade server = new ServerFacade();

    public void runREPL() {
        while(true) {
            if(!isLoggedIn) {
                System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + "[logged out] > " + EscapeSequences.RESET_TEXT_COLOR);
            } else {
                System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + "[logged in] > " + EscapeSequences.RESET_TEXT_COLOR);
            }

            System.out.flush();
            String[] tokens = sc.nextLine().split(" ");

            switch (tokens[0]) {
                case "help":
                case "h":
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
                    break;

                case "quit":
                case "q":
                    if(isLoggedIn) {
                        System.out.println("You must log out before quitting\n");
                        break;
                    }
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW+
                            "Goodbye!\n" +
                            EscapeSequences.RESET_TEXT_COLOR);
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
                    try {
                        GameData gameData = server.listGames().get(Integer.parseInt(tokens[2]));
                        printGame(gameData, ChessGame.TeamColor.WHITE);
                    } catch (Exception e) {
                        System.out.println(e.getMessage() + "\n");
                    }


                default:
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                            "Unknown command: \"" + input + "\". Type \"help\" for a list of commands.\n" +
                            EscapeSequences.RESET_TEXT_COLOR);
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

    private void printGame(GameData data, ChessGame.TeamColor color) throws Exception {

    }

}
