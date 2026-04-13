package main;

import java.awt.image.BufferedImage;

public class Negative implements ImageOperation {
    public BufferedImage apply(BufferedImage img) {
        BufferedImage result = new BufferedImage(
                img.getWidth(), img.getHeight(), img.getType());

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);

                int r = 255 - ((rgb >> 16) & 0xFF);
                int g = 255 - ((rgb >> 8) & 0xFF);
                int b = 255 - (rgb & 0xFF);

                int newRGB = (r << 16) | (g << 8) | b;
                result.setRGB(x, y, newRGB);
            }
        }
        return result;
    }
}
