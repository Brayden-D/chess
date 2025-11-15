import chess.*;
import facade.ServerFacade;
import ui.Client;
import ui.EscapeSequences;

import java.util.Scanner;
import facade.ServerFacade.*;

public class Main {
    public static void main(String[] args) {

        System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN +
                "♕ 240 Chess Client" +
                EscapeSequences.RESET_TEXT_COLOR);
        System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE +
                "enter \"help\" for commands " +
                EscapeSequences.RESET_TEXT_COLOR);
        System.out.println();

        Client client = new Client();
        client.server.setServerURL("http://localhost:8080");
        client.runREPL();

    }
}