package main;

import java.awt.image.BufferedImage;
import java.util.List;

public class AddImageCommand implements Command {

    private final BufferedImage img;
    private final String format;
    private final String path;

    public AddImageCommand(BufferedImage img, String format, String path) {
        this.img = img;
        this.format = format;
        this.path = path;
    }

    @Override
    public void execute(Session session) {
        session.getImages().add(new ImageEntry(img, format, path));
    }

    @Override
    public void undo(Session session) {
        List<ImageEntry> images = session.getImages();

        if (!images.isEmpty()) {
            images.remove(images.size() - 1);
        }
    }
}
