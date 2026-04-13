package main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageEntry {

    BufferedImage image;
    List<ImageOperation> ops = new ArrayList<>();
    String format;
    String sourcePath;

    // ✔ ONLY constructor (no more overload confusion)
    public ImageEntry(BufferedImage image, String format, String sourcePath) {
        this.image = image;
        this.format = format;
        this.sourcePath = sourcePath;
    }

    public void addOperation(ImageOperation op) {
        ops.add(op);
    }

    public BufferedImage process() {
        BufferedImage result = image;

        for (ImageOperation op : ops) {
            result = op.apply(result);
        }

        return result;
    }

    public String getFormat() {
        return format;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }
}
