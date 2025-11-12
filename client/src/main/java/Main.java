import chess.*;
import ui.EscapeSequences;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boolean loggedIn = false;
        Scanner sc = new Scanner(System.in);

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
            sc.nextLine();

        }


    }
}