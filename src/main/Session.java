package main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Session {

    private int id;
    private List<ImageEntry> images = new ArrayList<>();

    private Stack<Command> history = new Stack<>();
    private Stack<Command> undone = new Stack<>();

    public Session(int id, BufferedImage img, String format, String path) {
        this.id = id;
        images.add(new ImageEntry(img, format, path));
    }

    public int getId() {
        return id;
    }

    public List<ImageEntry> getImages() {
        return images;
    }

    // =========================
    // COMMAND SYSTEM
    // =========================

    public void execute(Command cmd) {
        cmd.execute(this);
        history.push(cmd);
        undone.clear(); // new action breaks redo chain
    }

    public void undo() {
        if (history.isEmpty()) return;

        Command cmd = history.pop();
        cmd.undo(this);
        undone.push(cmd);
    }

    public void redo() {
        if (undone.isEmpty()) return;

        Command cmd = undone.pop();
        cmd.execute(this);
        history.push(cmd);
    }

    public ImageEntry findByPath(String path) {
        for (ImageEntry e : images) {
            if (e.getSourcePath().equals(path) ||
                    e.getSourcePath().endsWith(path)) {
                return e;
            }
        }
        return null;
    }

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
                            (entry.getFormat() != null ? " [" + entry.getFormat() + "]" : "")
            );

            List<ImageOperation> ops = entry.getOperations();

            if (ops.isEmpty()) {
                System.out.println("   └─ no operations");
            } else {
                for (int j = 0; j < ops.size(); j++) {
                    String prefix = (j == ops.size() - 1) ? "   └─ " : "   ├─ ";
                    System.out.println(prefix + ops.get(j));
                }
            }
        }
    }
}
