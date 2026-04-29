package main;

import java.util.Scanner;

public class ConsoleUI {

    private SessionManager manager;
    private Scanner sc;
    private int activeSession = -1;

    public ConsoleUI(SessionManager manager) {
        this.manager = manager;
        this.sc = new Scanner(System.in);
    }

    public void run() {

        while (true) {
            System.out.print("> ");
            String[] cmd = sc.nextLine().trim().split("\\s+");

            if (cmd.length == 0 || cmd[0].isEmpty()) continue;

            try {
                handleCommand(cmd);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void handleCommand(String[] cmd) throws Exception {

        String action = cmd[0].toLowerCase();

        switch (action) {

            case "set-directory": {

                if (cmd.length < 2) {
                    System.out.println("Usage: set-directory <folder>");
                    break;
                }

                manager.setDirectory(cmd[1]);
                System.out.println("Directory set to " + cmd[1]);
                break;
            }

            case "load":
                load(cmd);
                break;

            case "switch":
                switchSession(cmd);
                break;

            case "rotate":
                rotate(cmd);
                break;

            case "grayscale":
                executeOp(new Grayscale(), "Grayscale executed");
                break;

            case "negative":
                executeOp(new Negative(), "Negative executed");
                break;

            case "add":
                add(cmd);
                break;

            case "undo":
                getSession().undo();
                System.out.println("Undo executed");
                break;

            case "redo":
                getSession().redo();
                System.out.println("Redo executed");
                break;

            case "session-info": {

                checkSession();
                getSession().printInfo();
                break;
            }

            case "collage": {

                checkSession();

                if (cmd.length < 5) {
                    System.out.println("Usage: collage <horizontal|vertical> <img1> <img2> <out>");
                    break;
                }

                CollageDirection dir;

                if (cmd[1].equalsIgnoreCase("horizontal")) {
                    dir = CollageDirection.HORIZONTAL;
                } else if (cmd[1].equalsIgnoreCase("vertical")) {
                    dir = CollageDirection.VERTICAL;
                } else {
                    System.out.println("Invalid direction");
                    break;
                }

                Session s = getSession();

                ImageEntry img1 = s.findByPath(cmd[2]);
                ImageEntry img2 = s.findByPath(cmd[3]);

                if (img1 == null || img2 == null) {
                    System.out.println("Images must exist in session");
                    break;
                }

                s.execute(new CollageCommand(dir,img1, img2,manager.getWorkingDirectory(), cmd[4]));

                System.out.println("Collage created: " + cmd[4]);
                break;
            }

            case "save":
                manager.save(activeSession);
                System.out.println("Saved");
                break;

            case "saveas":
                saveAs(cmd);
                break;

            case "close":
                manager.close(activeSession);
                System.out.println("Closed session " + activeSession);
                activeSession = -1;
                break;

            case "exit":
                System.out.println("Done!");
                sc.close();
                System.exit(0);

            default:
                System.out.println("Unknown command");
        }
    }

    private void load(String[] cmd) throws Exception {
        if (cmd.length < 2) {
            System.out.println("Usage: load <file>");
            return;
        }

        int id = manager.load(cmd[1]);
        activeSession = id;

        System.out.println("Loaded session ID: " + id);
    }

    private void switchSession(String[] cmd) {
        if (cmd.length < 2) {
            System.out.println("Usage: switch <id>");
            return;
        }

        int id = Integer.parseInt(cmd[1]);

        if (manager.getSession(id) == null) {
            System.out.println("Session not found");
            return;
        }

        activeSession = id;
        System.out.println("Switched to session " + id);
    }

    private void rotate(String[] cmd) {
        checkSession();

        if (cmd.length < 2) {
            System.out.println("Usage: rotate <left|right>");
            return;
        }

        RotateDirection dir = cmd[1].equalsIgnoreCase("left")
                ? RotateDirection.LEFT
                : RotateDirection.RIGHT;

        getSession().execute(new OperationCommand(new Rotate(dir)));

        System.out.println("Rotate " + dir + " executed");
    }

    private void add(String[] cmd) throws Exception {
        checkSession();

        if (cmd.length < 2) {
            System.out.println("Usage: add <file>");
            return;
        }

        ImageData data = manager.loadImage(cmd[1]);

        getSession().execute(
                new AddImageCommand(data.image, data.format, data.path)
        );

        System.out.println("Image added");
    }

    private void saveAs(String[] cmd) throws Exception {
        checkSession();

        if (cmd.length < 2) {
            System.out.println("Usage: saveas <file>");
            return;
        }

        manager.saveAs(activeSession, cmd[1]);
        System.out.println("Saved as " + cmd[1]);
    }

    private void executeOp(ImageOperation op, String msg) {
        checkSession();
        getSession().execute(new OperationCommand(op));
        System.out.println(msg);
    }

    private Session getSession() {
        checkSession();
        return manager.getSession(activeSession);
    }

    private void checkSession() {
        if (activeSession == -1 || manager.getSession(activeSession) == null) {
            throw new IllegalStateException("No active session");
        }
    }
}