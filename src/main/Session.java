package main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Session {

    private int id;
    private List<ImageEntry> images = new ArrayList<>();

    public Session(int id, BufferedImage img, String format, String path) {
        this.id = id;
        images.add(new ImageEntry(img, format, path));
    }

    public int getId() {
        return id;
    }

    public void addImage(BufferedImage img, String format, String path) {
        images.add(new ImageEntry(img, format, path));
    }

    public void addOperation(ImageOperation op) {
        for (ImageEntry img : images) {
            img.addOperation(op);
        }
    }

    public List<ImageEntry> getImages() {
        return images;
    }
}
