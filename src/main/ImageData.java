package main;

import java.awt.image.BufferedImage;

public class ImageData {
    public BufferedImage image;
    public String format;
    public String path;

    public ImageData(BufferedImage image, String format, String path) {
        this.image = image;
        this.format = format;
        this.path = path;
    }
}
