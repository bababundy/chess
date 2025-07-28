package client;

import server.ResponseException;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ClientBase client;

    public Repl(String serverUrl) {
        client = new PreLoginClient(serverUrl, this);
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_BLUE + "Welcome to the chess client. Sign in or register to start.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + UNICODE_ESCAPE + "[" + client.state + "]" + ">>> " + SET_BG_COLOR_DARK_GREEN);
    }
}