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
            input = sc.nextLine();

            switch (input.split(" ")[0]) {
                case "help":
                case "h":
                    if(authToken == null) {
                        System.out.println("help: lists commands\n" +
                                "quit: terminates program\n" +
                                "login [username] [password]: logs an existing user in\n" +
                                "register [username] [password] [email]: registers a new user and logs them in");
                    } else {
                        System.out.println("help: lists commands\n" +
                                "logout: logs user out\n" +
                                "create [gamename]: creates a new game with specified name\n" +
                                "list: list all games currently on the server\n" +
                                "play [gamenumber/gamename] [optional: color]: join specified game " +
                                "(optional: with specified color)\n" +
                                "observe [gamenumber/gamename]: observe an active game\n");
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
                        authToken = server.register(input.split(" ")[1],
                                        input.split(" ")[2],
                                        input.split(" ")[3])
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
                        authToken = server.login(input.split(" ")[1],
                                        input.split(" ")[2])
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



                default:
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                            "Unknown command: \"" + input + "\". Type \"help\" for a list of commands.\n" +
                            EscapeSequences.RESET_TEXT_COLOR);
                    break;
            }



        }
    }

}
