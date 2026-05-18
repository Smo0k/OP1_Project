package main.session;

import main.commands.Command;
import main.image_operation.ImageEntry;
import main.image_operation.ImageOperation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Represents a working session in the image editing application.
 * <p>
 * A session stores images and supports a command history system
 * that allows undo and redo functionality.
 * </p>
 */
public class Session {

    /**
     * Unique session identifier.
     */
    private int id;

    /**
     * List of images contained in this session.
     */
    private List<ImageEntry> images = new ArrayList<>();

    /**
     * Stack of executed commands for undo operations.
     */
    private Stack<Command> history = new Stack<>();

    /**
     * Stack of undone commands for redo operations.
     */
    private Stack<Command> undone = new Stack<>();

    /**
     * Creates a new session with an initial image.
     *
     * @param id     the session ID
     * @param img    the initial image
     * @param format the image format
     * @param path   the image source path
     */
    public Session(int id, BufferedImage img, String format, String path) {
        this.id = id;
        images.add(new ImageEntry(img, format, path));
    }

    /**
     * Returns the session ID.
     *
     * @return session identifier
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the list of images in the session.
     *
     * @return list of images
     */
    public List<ImageEntry> getImages() {
        return images;
    }

    /**
     * Executes a command and stores it in history.
     *
     * @param cmd the command to execute
     */
    public void execute(Command cmd) {
        cmd.execute(this);
        history.push(cmd);
        undone.clear(); // new action breaks redo chain
    }

    /**
     * Undoes the last executed command.
     */
    public void undo() {
        if (history.isEmpty()) return;

        Command cmd = history.pop();
        cmd.undo(this);
        undone.push(cmd);
    }

    /**
     * Redoes the last undone command.
     */
    public void redo() {
        if (undone.isEmpty()) return;

        Command cmd = undone.pop();
        cmd.execute(this);
        history.push(cmd);
    }

    /**
     * Finds an image in the session by its path.
     *
     * @param path the image path to search for
     * @return the matching image entry, or {@code null} if not found
     */
    public ImageEntry findByPath(String path) {
        for (ImageEntry e : images) {
            if (e.getSourcePath().equals(path)
                    || e.getSourcePath().endsWith(path)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Prints detailed information about the session,
     * including images and their applied operations.
     */
    public void printInfo() {

        System.out.println("Session ID: " + id);

        if (images.isEmpty()) {
            System.out.println("No images in session.");
            return;
        }

        for (int i = 0; i < images.size(); i++) {

            ImageEntry entry = images.get(i);

            System.out.println(
                    i + ": " +
                            entry.getSourcePath() +
                            (entry.getFormat() != null
                                    ? " [" + entry.getFormat() + "]"
                                    : "")
            );

            List<ImageOperation> ops = entry.getOperations();

            if (ops.isEmpty()) {
                System.out.println("   └─ no operations");
            } else {
                for (int j = 0; j < ops.size(); j++) {
                    String prefix = (j == ops.size() - 1)
                            ? "   └─ "
                            : "   ├─ ";
                    System.out.println(prefix + ops.get(j));
                }
            }
        }
    }
}