package ui;

import facade.ServerFacade;
import model.GameData;

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
                                play [gamenumber/gamename] [color]: join specified game as the specified color
                                observe [gamenumber/gamename]: observe an active game \n
                                """);
                    }
                    break;

                case "quit":
                case "q":
                    if(isLoggedIn) {
                        System.out.println("You must log out before quitting\n");
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
                    }
                    try {
                        GameData[] data = server.listGames();
                    } catch (Exception e) {
                        System.out.println(e.getMessage() + "\n");
                    }
                    break;

                default:
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                            "Unknown command: \"" + input + "\". Type \"help\" for a list of commands.\n" +
                            EscapeSequences.RESET_TEXT_COLOR);
                    break;
            }



        }
    }

}
