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

                    // =========================
                    // LOAD
                    // =========================
                    case "load": {

                        if (cmd.length < 2) {
                            System.out.println("Usage: load <file>");
                            break;
                        }

                        int id = manager.load(cmd[1]);
                        activeSession = id;

                        System.out.println("Loaded session ID: " + id);
                        break;
                    }

                    // =========================
                    // SWITCH
                    // =========================
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

                    // =========================
                    // ROTATE (NOW COMMAND-BASED)
                    // =========================
                    case "rotate": {

                        checkSession(activeSession, manager);

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

                        manager.getSession(activeSession)
                                .execute(new OperationCommand(new Rotate(dir)));

                        System.out.println("Rotate " + dir + " executed");
                        break;
                    }

                    // =========================
                    // GRAYSCALE
                    // =========================
                    case "grayscale": {

                        checkSession(activeSession, manager);

                        manager.getSession(activeSession)
                                .execute(new OperationCommand(new Grayscale()));

                        System.out.println("Grayscale executed");
                        break;
                    }

                    // =========================
                    // NEGATIVE
                    // =========================
                    case "negative": {

                        checkSession(activeSession, manager);

                        manager.getSession(activeSession)
                                .execute(new OperationCommand(new Negative()));

                        System.out.println("Negative executed");
                        break;
                    }

                    // =========================
                    // ADD IMAGE (COMMAND)
                    // =========================
                    case "add": {

                        checkSession(activeSession, manager);

                        if (cmd.length < 2) {
                            System.out.println("Usage: add <file>");
                            break;
                        }

                        ImageData data = manager.loadImage(cmd[1]);

                        manager.getSession(activeSession).execute(new AddImageCommand(data.image, data.format, data.path));

                        System.out.println("Image added to session " + activeSession);
                        break;
                    }

                    // =========================
                    // UNDO
                    // =========================
                    case "undo": {

                        checkSession(activeSession, manager);

                        manager.getSession(activeSession).undo();
                        System.out.println("Undo executed");

                        break;
                    }

                    // =========================
                    // REDO
                    // =========================
                    case "redo": {

                        checkSession(activeSession, manager);

                        manager.getSession(activeSession).redo();
                        System.out.println("Redo executed");

                        break;
                    }

                    // =========================
                    // SAVE
                    // =========================
                    case "save": {

                        checkSession(activeSession, manager);

                        manager.save(activeSession);
                        System.out.println("Session " + activeSession + " saved successfully");
                        break;
                    }

                    // =========================
                    // SAVE AS
                    // =========================
                    case "saveas": {

                        checkSession(activeSession, manager);

                        if (cmd.length < 2) {
                            System.out.println("Usage: saveas <file>");
                            break;
                        }

                        manager.saveAs(activeSession, cmd[1]);

                        System.out.println("Saved as " + cmd[1]);
                        break;
                    }

                    case "close": {

                        checkSession(activeSession, manager);

                        manager.close(activeSession);

                        System.out.println("Closed session " + activeSession);

                        activeSession = -1;

                        break;
                    }

                    // =========================
                    // EXIT
                    // =========================
                    case "exit": {
                        System.out.println("Done!");
                        sc.close();
                        return;
                    }

                    default:
                        System.out.println("Unknown command. Try: load, switch, rotate, grayscale, negative, add, undo, redo, save, saveas, exit");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void checkSession(int id, SessionManager manager) {
        if (id == -1 || manager.getSession(id) == null) {
            throw new IllegalStateException("No active session");
        }
    }
}
