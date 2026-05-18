package main.image_operation;

import main.rnm_helpers.PNMReader;
import main.commands.WorkingDirectory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Loads images from the file system.
 * <p>
 * The loader supports:
 * </p>
 * <ul>
 *     <li>Standard image formats supported by {@link ImageIO}</li>
 *     <li>PNM image formats ({@code .ppm}, {@code .pgm}, {@code .pbm})</li>
 * </ul>
 *
 * <p>
 * File paths are resolved using the application's
 * {@link WorkingDirectory}.
 * </p>
 */
public class ImageLoader {

    /**
     * The working directory used for resolving image paths.
     */
    private final WorkingDirectory workingDirectory;

    /**
     * Reader used for loading PNM images.
     */
    private PNMReader pnmReader = new PNMReader();

    /**
     * Constructs a new {@code ImageLoader}.
     *
     * @param workingDirectory the working directory used for path resolution
     */
    public ImageLoader(WorkingDirectory workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    /**
     * Loads an image from the specified path.
     * <p>
     * If the file is a PNM image, it is loaded using
     * {@link PNMReader}. Otherwise, the image is loaded
     * using {@link ImageIO}.
     * </p>
     *
     * @param path the image file path
     * @return the loaded image data
     * @throws IOException if the image cannot be loaded
     *                     or the file format is invalid
     */
    public ImageData load(String path) throws IOException {

        String resolvedPath = workingDirectory.resolve(path);
        File file = new File(resolvedPath);

        BufferedImage img;

        if (isPNM(resolvedPath)) {
            return pnmReader.read(file, resolvedPath);
        }

        img = ImageIO.read(file);

        if (img == null) {
            throw new IOException("Invalid image: " + resolvedPath);
        }

        return new ImageData(img, null, resolvedPath);
    }

    /**
     * Checks whether the given file path refers
     * to a PNM image format.
     *
     * @param path the file path
     * @return {@code true} if the file is a PNM image;
     *         otherwise {@code false}
     */
    private boolean isPNM(String path) {

        String lower = path.toLowerCase();

        return lower.endsWith(".ppm") ||
                lower.endsWith(".pgm") ||
                lower.endsWith(".pbm");
    }
}