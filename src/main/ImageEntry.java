package main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageEntry {

    private final BufferedImage originalImage;
    private final List<ImageOperation> operations = new ArrayList<>();

    private final String format;
    private final String sourcePath; // logical name inside session

    public ImageEntry(BufferedImage image, String format, String sourcePath) {
        this.originalImage = image;
        this.format = format;
        this.sourcePath = sourcePath;
    }

    // =========================
    // OPERATIONS
    // =========================

    public void addOperation(ImageOperation op) {
        operations.add(op);
    }

    public void removeOperation(ImageOperation op) {
        operations.remove(op);
    }

    public List<ImageOperation> getOperations() {
        return Collections.unmodifiableList(operations);
    }

    // =========================
    // APPLY OPERATIONS
    // =========================

    public BufferedImage process() {
        BufferedImage result = originalImage;

        for (ImageOperation op : operations) {
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
}