package main.image_operation;

import main.rnm_helpers.PNMUtil;
import main.session.Session;
import main.commands.WorkingDirectory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Saves processed images from a session to the file system.
 * <p>
 * The saver supports:
 * </p>
 * <ul>
 *     <li>Saving images to their original paths</li>
 *     <li>Saving images to new paths</li>
 *     <li>Saving standard image formats through {@link ImageIO}</li>
 *     <li>Saving PNM image formats through {@link PNMUtil}</li>
 * </ul>
 */
public class ImageSaver {

    /**
     * The working directory used for resolving file paths.
     */
    private final WorkingDirectory workingDirectory;

    /**
     * Constructs a new {@code ImageSaver}.
     *
     * @param workingDirectory the working directory used for path resolution
     */
    public ImageSaver(WorkingDirectory workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    /**
     * Saves all images in the session to their original file paths.
     *
     * @param session the session containing the images
     * @throws IOException if saving fails or an image has no source path
     */
    public void save(Session session) throws IOException {

        for (ImageEntry entry : session.getImages()) {

            BufferedImage img = entry.process();

            String path = entry.getSourcePath();

            if (path == null) {
                throw new IOException("No original path. Use saveAs.");
            }

            path = workingDirectory.resolve(path);

            write(img, path, entry.getFormat());
        }
    }

    /**
     * Saves all images in the session to a new file path.
     * <p>
     * If the session contains multiple images,
     * numbered suffixes are automatically added
     * to the generated file names.
     * </p>
     *
     * @param session the session containing the images
     * @param path    the output file path
     * @throws IOException if saving fails
     */
    public void saveAs(Session session, String path) throws IOException {

        path = workingDirectory.resolve(path);

        var images = session.getImages();

        for (int i = 0; i < images.size(); i++) {

            ImageEntry entry = images.get(i);
            BufferedImage img = entry.process();

            String outputPath = buildPath(path, i, images.size());

            write(img, outputPath, entry.getFormat());
        }
    }

    /**
     * Writes an image to disk.
     * <p>
     * PNM images are written using {@link PNMUtil},
     * while other formats are written using {@link ImageIO}.
     * </p>
     *
     * @param img       the image to write
     * @param path      the destination file path
     * @param pnmFormat the PNM format if applicable
     * @throws IOException if writing fails or the format is unsupported
     */
    private void write(BufferedImage img,
                       String path,
                       String pnmFormat) throws IOException {

        File file = new File(path);

        if (pnmFormat != null) {

            PNMUtil.writePNM(img, file, pnmFormat);

        } else {

            String ext = getExt(path);

            boolean ok = ImageIO.write(img, ext, file);

            if (!ok) {
                throw new IOException("Unsupported format: " + ext);
            }
        }
    }

    /**
     * Builds a unique output path for an image.
     * <p>
     * If multiple images are being saved,
     * an index suffix is appended to the file name.
     * </p>
     *
     * @param base  the base output path
     * @param index the image index
     * @param total the total number of images
     * @return the generated output path
     */
    private String buildPath(String base, int index, int total) {

        if (total == 1) {
            return base;
        }

        int dot = base.lastIndexOf('.');

        if (dot != -1) {
            return base.substring(0, dot)
                    + "_"
                    + index
                    + base.substring(dot);
        }

        return base + "_" + index;
    }

    /**
     * Extracts the file extension from a path.
     *
     * @param path the file path
     * @return the lowercase file extension,
     *         or {@code "png"} if no extension exists
     */
    private String getExt(String path) {

        int dot = path.lastIndexOf('.');

        return (dot != -1)
                ? path.substring(dot + 1).toLowerCase()
                : "png";
    }
}