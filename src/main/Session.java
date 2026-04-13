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

    // =========================
    // IMAGE MANAGEMENT
    // =========================

    public void addImage(BufferedImage img, String format, String path) {
        ImageEntry entry = new ImageEntry(img, format, path);
        images.add(entry);
    }

    public void removeImage(ImageEntry entry) {
        images.remove(entry);
    }

    public List<ImageEntry> getImages() {
        return images;
    }

    // =========================
    // OPERATIONS (DIRECT USAGE)
    // =========================
    public void addOperation(ImageOperation op) {
        for (ImageEntry img : images) {
            img.addOperation(op);
        }
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
}
