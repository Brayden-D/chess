import chess.*;
import ui.EscapeSequences;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boolean loggedIn = false;
        Scanner sc = new Scanner(System.in);
        String input;

        System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "♕ 240 Chess Client" + EscapeSequences.RESET_TEXT_COLOR);
        System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE + "enter \"help\" for commands " + EscapeSequences.RESET_TEXT_COLOR);
        System.out.println();

        while(true) {
            if(!loggedIn) {
                System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + "[logged out] " + EscapeSequences.RESET_TEXT_COLOR);
            } else {
                System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + "[logged in] " + EscapeSequences.RESET_TEXT_COLOR);
            }

            System.out.flush();
            input = sc.nextLine();

            switch (input) {
                case "help":
                case "h":
                    if(!loggedIn) {
                        System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE +
                                "help: lists commands\n" +
                                "quit: terminates program\n" +
                                "login [username] [password]: logs an existing user in\n" +
                                "register [username] [password] [email]: registers a new user and logs them in" +
                                EscapeSequences.RESET_TEXT_COLOR);
                    } else {
                        System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE +
                                "help: lists commands\n" +
                                "logout: logs user out\n" +
                                "create [gamename]: creates a new game with specified name\n" +
                                "list: list all games currently on the server\n" +
                                "play [gamenumber/gamename] [optional: color]: join specified game (optional: with specified color)\n" +
                                "observe [gamenumber/gamename]: observe an active game" +
                                EscapeSequences.RESET_TEXT_COLOR);
                    }
                    break;

                default:
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                            "Unknown command: \"" + input + "\". Type \"help\" for a list of commands." +
                            EscapeSequences.RESET_TEXT_COLOR);
                    break;
            }



        }


    }
}