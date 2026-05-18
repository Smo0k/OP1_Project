package main.commands;

import main.image_operation.ImageOperation;

import java.awt.image.BufferedImage;

/**
 * An image operation that converts an image to grayscale.
 * <p>
 * Each pixel is transformed by averaging its red, green,
 * and blue color components into a single grayscale value.
 * </p>
 */
public class Grayscale implements ImageOperation {

    /**
     * Applies the grayscale transformation to the given image.
     *
     * @param img the source image
     * @return a new grayscale version of the image
     */
    @Override
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

    /**
     * Returns the name of the operation.
     *
     * @return the string {@code "Grayscale"}
     */
    @Override
    public String toString() {
        return "Grayscale";
    }
}