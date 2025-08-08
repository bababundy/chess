package client;

import websocket.NotificationHandler;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private ClientBase client;
    private final Scanner scanner = new Scanner(System.in);

    public Repl(String serverUrl) {
        client = new PreLoginClient(serverUrl, this);
    }

    public void setClient(ClientBase newClient) {
        this.client = newClient;
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_BLUE + "Welcome to the chess client. Sign in or register to start." + RESET_TEXT_COLOR);
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

    public String readLine() {
        return scanner.nextLine();
    }

    public void printPrompt() {
        System.out.print(RESET_TEXT_COLOR + "\n" + "[" + client.state + "]" + ">>> ");
    }

    @Override
    public void notify(ServerMessage message) {
        System.out.println(SET_TEXT_COLOR_RED + message.toString());
        printPrompt();
    }
}