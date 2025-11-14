package ui;

import facade.ServerFacade;

import java.util.Scanner;

public class Client {

    String authToken = null;
    Scanner sc = new Scanner(System.in);
    String input;
    ServerFacade server = new ServerFacade();

    public void runREPL() {
        while(true) {
            if(authToken == null) {
                System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + "[logged out] > " + EscapeSequences.RESET_TEXT_COLOR);
            } else {
                System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + "[logged in] > " + EscapeSequences.RESET_TEXT_COLOR);
            }

            System.out.flush();
            String[] tokens = sc.nextLine().split(" ");

            switch (tokens[0]) {
                case "help":
                case "h":
                    if(authToken == null) {
                        System.out.println("""
                                help: lists commands
                                quit: terminates program
                                login [username] [password]: logs an existing user in
                                register [username] [password] [email]: registers a new user and logs them in \
                                """);
                    } else {
                        System.out.println("""
                                help: lists commands
                                logout: logs user out
                                create [gamename]: creates a new game with specified name
                                list: list all games currently on the server
                                play [gamenumber/gamename] [color]: join specified game as the specified color
                                observe [gamenumber/gamename]: observe an active game \
                                """);
                    }
                    break;

                case "quit":
                case "q":
                    if(authToken != null) {
                        System.out.println("You must log out before quitting\n");
                    }
                    System.out.println("Goodbye!\n");
                    System.exit(0);
                    break;

                case "register":
                case "r":
                    if (authToken != null) {
                        System.out.println("User already logged in!\n");
                    }
                    try {
                        authToken = server.register(tokens[1],
                                                    tokens[2],
                                                    tokens[3])
                                .authToken();
                        System.out.println("Successfully registered user!\n");
                    } catch (Exception e) {
                        System.out.println(e.getMessage() + "\n");
                    }
                    break;

                case "login":
                case "li":
                    if (authToken != null) {
                        System.out.println("User already logged in!\n");
                    }
                    try {
                        authToken = server.login(tokens[1],
                                                 tokens[2])
                                .authToken();
                        System.out.println("Successfully logged in!\n");
                    } catch (Exception e) {
                        System.out.println(e.getMessage() + "\n");
                    }
                    break;

                case "logout":
                case "lo":
                    if (authToken == null) {
                        System.out.println("No user logged in!\n");
                    }
                    try {
                        server.logout();
                        System.out.println("Successfully logged out!\n");
                        authToken = null;
                    } catch (Exception e) {
                        System.out.println(e.getMessage() + "\n");
                    }
                    break;

                case "create":
                case "c":
                    if (authToken == null) {
                        System.out.println("No user logged in!\n");
                    }
                    try {
                        server.createGame(tokens[1]);
                        System.out.println("Successfully created game!\n");
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
