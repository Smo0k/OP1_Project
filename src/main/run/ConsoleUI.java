package main.run;

import main.commands.*;
import main.image_operation.ImageData;
import main.image_operation.ImageEntry;
import main.image_operation.ImageOperation;
import main.session.Session;
import main.session.SessionManager;

import java.util.Scanner;

/**
 * Provides a console-based user interface for interacting
 * with the image editor application.
 * <p>
 * The class is responsible for:
 * </p>
 * <ul>
 *     <li>Reading user input from the console</li>
 *     <li>Parsing and executing commands</li>
 *     <li>Managing the active session</li>
 *     <li>Displaying status and error messages</li>
 * </ul>
 */
public class ConsoleUI {

    /**
     * Manages all application sessions.
     */
    private SessionManager manager;

    /**
     * Scanner used for reading console input.
     */
    private Scanner sc;

    /**
     * The ID of the currently active session.
     * A value of {@code -1} indicates that no session is active.
     */
    private int activeSession = -1;

    /**
     * Constructs a new {@code ConsoleUI}.
     *
     * @param manager the session manager used by the application
     */
    public ConsoleUI(SessionManager manager) {
        this.manager = manager;
        this.sc = new Scanner(System.in);
    }

    /**
     * Starts the main command loop of the application.
     * <p>
     * The method continuously reads user input and executes
     * commands until the application is terminated.
     * </p>
     */
    public void run() {

        // Factory Generater

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

    /**
     * Parses and executes a user command.
     *
     * @param cmd the command tokens entered by the user
     * @throws Exception if command execution fails
     */
    private void handleCommand(String[] cmd) throws Exception {

        String action = cmd[0].toLowerCase();

        switch (action) {

            case "help":{
                System.out.println("Commands: \n" +
                        "---------\n" +
                        "- help: Prints this sheet.\n" +
                        "- set-directory <folder>: sets directory path to be used when loading images.\n" +
                        "- load <img>: creates a new session and adds the image in the session.\n" +
                        "- switch <id>: switches to a different session with the passed id.\n" +
                        "- rotate <right|left>: rotates all images in current session.\n" +
                        "- grayscale: converts all images in session to grayscale.\n" +
                        "- negative: converts all images in session to negative.\n" +
                        "- add <img>: adds an image to the current session.\n" +
                        "- undo: removes the last added operation.\n" +
                        "- redo: adds back the last removed operation.\n" +
                        "- session-info: prints the data for the session (images and their operations).\n" +
                        "- collage <horizontal|vertical> <img1> <img2> <out>: makes a collage of 2 images.\n" +
                        "- save: saves all images in the current session.\n" +
                        "- saveas <out>: saves an image as an other file.\n" +
                        "- close: closes the current session.\n" +
                        "- exit: exits the program without saving anything.\n");
                break;
            }

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

                s.execute(new CollageCommand(dir, img1, img2,
                        manager.getWorkingDirectory(), cmd[4]));

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

    /**
     * Loads an image and creates a new session.
     *
     * @param cmd the command arguments
     * @throws Exception if the image cannot be loaded
     */
    private void load(String[] cmd) throws Exception {
        if (cmd.length < 2) {
            System.out.println("Usage: load <file>");
            return;
        }

        int id = manager.load(cmd[1]);
        activeSession = id;

        System.out.println("Loaded session ID: " + id);
    }

    /**
     * Switches the currently active session.
     *
     * @param cmd the command arguments
     */
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

    /**
     * Rotates all images in the current session.
     *
     * @param cmd the command arguments
     */
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

    /**
     * Adds an image to the current session.
     *
     * @param cmd the command arguments
     * @throws Exception if the image cannot be loaded
     */
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

    /**
     * Saves the current session as another file.
     *
     * @param cmd the command arguments
     * @throws Exception if saving fails
     */
    private void saveAs(String[] cmd) throws Exception {
        checkSession();

        if (cmd.length < 2) {
            System.out.println("Usage: saveas <file>");
            return;
        }

        manager.saveAs(activeSession, cmd[1]);
        System.out.println("Saved as " + cmd[1]);
    }

    /**
     * Executes an image operation on the current session.
     *
     * @param op  the image operation to execute
     * @param msg the success message to display
     */
    private void executeOp(ImageOperation op, String msg) {
        checkSession();
        getSession().execute(new OperationCommand(op));
        System.out.println(msg);
    }

    /**
     * Returns the currently active session.
     *
     * @return the active session
     * @throws IllegalStateException if no session is active
     */
    private Session getSession() {
        checkSession();
        return manager.getSession(activeSession);
    }

    /**
     * Ensures that there is an active session.
     *
     * @throws IllegalStateException if no active session exists
     */
    private void checkSession() {
        if (activeSession == -1 || manager.getSession(activeSession) == null) {
            throw new IllegalStateException("No active session");
        }
    }
}