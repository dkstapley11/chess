package ui;

import ui.websocket.ServerMessageHandler;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;
import websocket.messages.Error;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements ServerMessageHandler {
    private final ChessClient client;


    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl, this);
    }

    public void run() {
        System.out.println("\uD83D\uDC36 Welcome to Chess. Sign in or register to start.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.getMessage();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    @Override
    public void notify(ServerMessage message, String stringMessage) {
        client.notify(message, stringMessage);
        printPrompt();
    }
}