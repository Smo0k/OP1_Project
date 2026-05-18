package main.image_operation;

import java.awt.image.BufferedImage;

/**
 * Stores image-related data loaded into the application.
 * <p>
 * This class acts as a simple container for:
 * </p>
 * <ul>
 *     <li>The image itself</li>
 *     <li>The image format</li>
 *     <li>The image file path</li>
 * </ul>
 */
public class ImageData {

    /**
     * The loaded image.
     */
    public BufferedImage image;

    /**
     * The image file format
     * (for example: {@code png}, {@code jpg}, etc.).
     */
    public String format;

    /**
     * The file path of the image.
     */
    public String path;

    /**
     * Constructs a new {@code ImageData} object.
     *
     * @param image  the image data
     * @param format the image format
     * @param path   the image file path
     */
    public ImageData(BufferedImage image, String format, String path) {
        this.image = image;
        this.format = format;
        this.path = path;
    }
}