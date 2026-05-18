package main.image_operation;

import java.awt.image.BufferedImage;

/**
 * Represents an operation that can be applied to an image.
 * <p>
 * Implementations of this interface define image transformations
 * such as grayscale conversion, rotation, negative effect, and more.
 * </p>
 */
public interface ImageOperation {

    /**
     * Applies the operation to the given image.
     *
     * @param img the source image
     * @return the transformed image
     */
    BufferedImage apply(BufferedImage img);
}