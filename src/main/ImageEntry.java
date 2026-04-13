package main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageEntry {

    BufferedImage image;
    List<ImageOperation> ops = new ArrayList<>();

    String format;
    String sourcePath;

    // ONLY constructor
    public ImageEntry(BufferedImage image, String format, String sourcePath) {
        this.image = image;
        this.format = format;
        this.sourcePath = sourcePath;
    }

    // =========================
    // OPERATIONS
    // =========================

    public void addOperation(ImageOperation op) {
        ops.add(op);
    }

    public void removeOperation(ImageOperation op) {
        ops.remove(op);
    }

    // =========================
    // APPLY OPERATIONS
    // =========================

    public BufferedImage process() {
        BufferedImage result = image;

        for (ImageOperation op : ops) {
            result = op.apply(result);
        }

        return result;
    }

    // =========================
    // GETTERS
    // =========================

    public String getFormat() {
        return format;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public List<ImageOperation> getOperations() {
        return ops;
    }
}