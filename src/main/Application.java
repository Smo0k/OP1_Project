package main;

import java.io.IOException;
import java.util.Scanner;

public class Application {

    public static void main(String[] args) throws IOException {

        SessionManager manager = new SessionManager();
        Scanner sc = new Scanner(System.in);

        int activeSession = -1;

        while (true) {
            System.out.print("> ");
            String[] cmd = sc.nextLine().trim().split("\\s+");

            if (cmd.length == 0 || cmd[0].isEmpty()) continue;

            String action = cmd[0].toLowerCase();

            try {
                switch (action) {

                    // LOAD → creates + switches
                    case "load": {

                        if (cmd.length < 2) {
                            System.out.println("Usage: load <file>");
                            break;
                        }

                        try {
                            int id = manager.load(cmd[1]);
                            activeSession = id;

                            System.out.println("Loaded session ID: " + id);

                        } catch (Exception e) {
                            System.out.println("Error: " + e.getMessage());
                        }

                        break;
                    }

                    // SWITCH SESSION
                    case "switch": {

                        if (cmd.length < 2) {
                            System.out.println("Usage: switch <id>");
                            break;
                        }

                        int id;
                        try {
                            id = Integer.parseInt(cmd[1]);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid session id");
                            break;
                        }

                        if (manager.getSession(id) == null) {
                            System.out.println("Session not found");
                            break;
                        }

                        activeSession = id;
                        System.out.println("Switched to session " + id);
                        break;
                    }

                    // ROTATE
                    case "rotate": {
                        checkSession(activeSession);

                        if (cmd.length < 2) {
                            System.out.println("Usage: rotate <left|right>");
                            break;
                        }

                        RotateDirection dir;

                        if (cmd[1].equalsIgnoreCase("left")) {
                            dir = RotateDirection.LEFT;
                        } else if (cmd[1].equalsIgnoreCase("right")) {
                            dir = RotateDirection.RIGHT;
                        } else {
                            System.out.println("Invalid direction (left/right)");
                            break;
                        }

                        manager.getSession(activeSession).addOperation(new Rotate(dir));

                        System.out.println("Rotate " + dir + " added");
                        break;
                    }

                    // GRAYSCALE
                    case "grayscale": {
                        checkSession(activeSession);

                        manager.getSession(activeSession).addOperation(new Grayscale());

                        System.out.println("Grayscale added");
                        break;
                    }

                    // NEGATIVE
                    case "negative": {
                        checkSession(activeSession);

                        manager.getSession(activeSession).addOperation(new Negative());

                        System.out.println("Negative added");
                        break;
                    }

                    // ADD IMAGE
                    case "add": {
                        checkSession(activeSession);

                        if (cmd.length < 2) {
                            System.out.println("Usage: add <file>");
                            break;
                        }

                        manager.addToSession(activeSession, cmd[1]);

                        System.out.println("Image added to session " + activeSession);
                        break;
                    }

                    // SAVE
                    case "save": {
                        checkSession(activeSession);

                        try {
                            manager.save(activeSession);
                            System.out.println("Session " + activeSession + " saved successfully");
                        } catch (Exception e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    }

                    // SAVE AS
                    case "saveas": {
                        checkSession(activeSession);

                        if (cmd.length < 2) {
                            System.out.println("Usage: saveas <file>");
                            break;
                        }

                        manager.saveAs(activeSession, cmd[1]);

                        System.out.println("Saved as " + cmd[1]);
                        break;
                    }

                    // EXIT
                    case "exit": {
                        System.out.println("Done!");
                        sc.close();
                        return;
                    }

                    default:
                        System.out.println("Unknown command. Try: load, switch, rotate, grayscale, negative, add, save, saveas, exit");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void checkSession(int id) {
        if (id == -1) {
            System.out.println("No active session. Use 'load' or 'switch <id>'");
            throw new IllegalStateException();
        }
    }
}
