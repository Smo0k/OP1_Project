package main.image_operation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an image stored inside a session.
 * <p>
 * An {@code ImageEntry} keeps:
 * </p>
 * <ul>
 *     <li>The original image</li>
 *     <li>A list of image operations applied to it</li>
 *     <li>The image format</li>
 *     <li>The original source path</li>
 * </ul>
 *
 * <p>
 * Operations are applied dynamically when
 * {@link #process()} is called.
 * </p>
 */
public class ImageEntry {

    /**
     * The original unmodified image.
     */
    private final BufferedImage originalImage;

    /**
     * The list of operations applied to the image.
     */
    private final List<ImageOperation> operations = new ArrayList<>();

    /**
     * The image file format.
     */
    private final String format;

    /**
     * The original image source path inside the session.
     */
    private final String sourcePath;

    /**
     * Constructs a new {@code ImageEntry}.
     *
     * @param image      the original image
     * @param format     the image format
     * @param sourcePath the source file path
     */
    public ImageEntry(BufferedImage image, String format, String sourcePath) {
        this.originalImage = image;
        this.format = format;
        this.sourcePath = sourcePath;
    }

    /**
     * Adds an image operation to this image.
     *
     * @param op the operation to add
     */
    public void addOperation(ImageOperation op) {
        operations.add(op);
    }

    /**
     * Removes an image operation from this image.
     *
     * @param op the operation to remove
     */
    public void removeOperation(ImageOperation op) {
        operations.remove(op);
    }

    /**
     * Returns an unmodifiable list of image operations.
     *
     * @return the applied image operations
     */
    public List<ImageOperation> getOperations() {
        return Collections.unmodifiableList(operations);
    }

    /**
     * Applies all stored operations to the image in order.
     *
     * @return the processed image
     */
    public BufferedImage process() {

        BufferedImage result = originalImage;

        for (ImageOperation op : operations) {
            result = op.apply(result);
        }

        return result;
    }

    /**
     * Returns the image format.
     *
     * @return the image format
     */
    public String getFormat() {
        return format;
    }

    /**
     * Returns the original source path of the image.
     *
     * @return the source path
     */
    public String getSourcePath() {
        return sourcePath;
    }
}