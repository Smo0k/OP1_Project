package main.commands;

import main.image_operation.ImageOperation;

import java.awt.image.BufferedImage;

/**
 * An image operation that inverts all colors in an image.
 * <p>
 * This operation produces a "negative" effect by subtracting
 * each RGB component from 255.
 * </p>
 */
public class Negative implements ImageOperation {

    /**
     * Applies the negative transformation to the given image.
     *
     * @param img the source image
     * @return a new image with inverted colors
     */
    @Override
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

    /**
     * Returns the name of this operation.
     *
     * @return the string {@code "Negative"}
     */
    @Override
    public String toString() {
        return "Negative";
    }
}