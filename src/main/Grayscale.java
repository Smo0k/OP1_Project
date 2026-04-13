package main;

import java.awt.image.BufferedImage;

public class Grayscale implements ImageOperation {
    public BufferedImage apply(BufferedImage img) {
        BufferedImage result = new BufferedImage(
                img.getWidth(), img.getHeight(), img.getType());

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);

                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                int gray = (r + g + b) / 3;
                int newRGB = (gray << 16) | (gray << 8) | gray;

                result.setRGB(x, y, newRGB);
            }
        }
        return result;
    }
}
