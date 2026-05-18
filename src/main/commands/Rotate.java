package main.commands;

import main.image_operation.ImageOperation;

import java.awt.image.BufferedImage;

/**
 * An image operation that rotates an image either left or right.
 * <p>
 * The rotation is performed by transforming pixel coordinates
 * into a new image with swapped width and height.
 * </p>
 */
public class Rotate implements ImageOperation {

    /**
     * The direction of rotation.
     */
    private RotateDirection direction;

    /**
     * Constructs a rotation operation.
     *
     * @param direction the direction to rotate the image
     */
    public Rotate(RotateDirection direction) {
        this.direction = direction;
    }

    /**
     * Applies the rotation operation to the given image.
     *
     * @param img the source image
     * @return a new rotated image
     * @throws IllegalStateException if the rotation direction is invalid
     */
    @Override
    public BufferedImage apply(BufferedImage img) {

        int w = img.getWidth();
        int h = img.getHeight();

        BufferedImage result;

        switch (direction) {

            case RIGHT:
                result = new BufferedImage(h, w, img.getType());

                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        result.setRGB(h - 1 - y, x, img.getRGB(x, y));
                    }
                }
                break;

            case LEFT:
                result = new BufferedImage(h, w, img.getType());

                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        result.setRGB(y, w - 1 - x, img.getRGB(x, y));
                    }
                }
                break;

            default:
                throw new IllegalStateException("Unknown direction");
        }

        return result;
    }

    /**
     * Returns a string representation of this operation.
     *
     * @return a string describing the rotation direction
     */
    @Override
    public String toString() {
        return "Rotate(" + direction + ")";
    }
}