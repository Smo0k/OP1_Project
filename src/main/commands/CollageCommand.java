package main.commands;

import main.image_operation.ImageEntry;
import main.session.Session;

import java.awt.image.BufferedImage;

/**
 * A command that creates a collage from two images.
 * <p>
 * The collage can be created either horizontally or vertically,
 * depending on the provided {@link CollageDirection}.
 * Both images must have the same dimensions and format.
 * </p>
 *
 * <p>
 * The resulting image is stored in the session after execution
 * and can be removed through {@link #undo(Session)}.
 * </p>
 */
public class CollageCommand implements Command {

    /**
     * The first image used in the collage.
     */
    private ImageEntry img1;

    /**
     * The second image used in the collage.
     */
    private ImageEntry img2;

    /**
     * The resulting collage image.
     */
    private ImageEntry result;

    /**
     * The direction in which the collage is created.
     */
    private CollageDirection direction;

    /**
     * The output path for the resulting image.
     */
    private String outputPath;

    /**
     * The working directory used to resolve output paths.
     */
    private WorkingDirectory workingDirectory;

    /**
     * Constructs a new {@code CollageCommand}.
     *
     * @param direction         the collage direction
     *                          ({@link CollageDirection#HORIZONTAL}
     *                          or {@link CollageDirection#VERTICAL})
     * @param img1              the first image
     * @param img2              the second image
     * @param workingDirectory  the working directory used for resolving paths
     * @param outputPath        the output file path for the collage image
     */
    public CollageCommand(
            CollageDirection direction,
            ImageEntry img1,
            ImageEntry img2,
            WorkingDirectory workingDirectory,
            String outputPath) {


        this.direction = direction;
        this.img1 = img1;
        this.img2 = img2;
        this.workingDirectory = workingDirectory;
        this.outputPath = outputPath;
    }

    /**
     * Executes the collage creation command.
     * <p>
     * This method:
     * </p>
     * <ul>
     *     <li>Processes both input images</li>
     *     <li>Validates that they have the same size</li>
     *     <li>Validates that they have the same format</li>
     *     <li>Creates a new combined image</li>
     *     <li>Adds the resulting image to the session</li>
     * </ul>
     *
     * @param session the active session
     *
     * @throws RuntimeException if the images do not have the same size
     *                          or format
     */
    @Override
    public void execute(Session session) {

        String resolvedPath = workingDirectory.resolve(outputPath);

        BufferedImage a = img1.process();
        BufferedImage b = img2.process();

        // validation
        if (a.getWidth() != b.getWidth() || a.getHeight() != b.getHeight()) {
            throw new RuntimeException("Images must be same size");
        }

        if (img1.getFormat() != null && img2.getFormat() != null &&
                !img1.getFormat().equals(img2.getFormat())) {
            throw new RuntimeException("Images must have same format");
        }

        BufferedImage out;

        if (direction == CollageDirection.HORIZONTAL) {

            out = new BufferedImage(
                    a.getWidth() + b.getWidth(),
                    a.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            // left
            for (int y = 0; y < a.getHeight(); y++) {
                for (int x = 0; x < a.getWidth(); x++) {
                    out.setRGB(x, y, a.getRGB(x, y));
                }
            }

            // right
            for (int y = 0; y < b.getHeight(); y++) {
                for (int x = 0; x < b.getWidth(); x++) {
                    out.setRGB(x + a.getWidth(), y, b.getRGB(x, y));
                }
            }

        } else {

            out = new BufferedImage(
                    a.getWidth(),
                    a.getHeight() + b.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            // top
            for (int y = 0; y < a.getHeight(); y++) {
                for (int x = 0; x < a.getWidth(); x++) {
                    out.setRGB(x, y, a.getRGB(x, y));
                }
            }

            // bottom
            for (int y = 0; y < b.getHeight(); y++) {
                for (int x = 0; x < b.getWidth(); x++) {
                    out.setRGB(x, y + a.getHeight(), b.getRGB(x, y));
                }
            }
        }

        // preserve format if possible
        String format = img1.getFormat();

        result = new ImageEntry(out, format, resolvedPath);

        session.getImages().add(result);
    }

    /**
     * Undoes the collage command by removing the generated image
     * from the session.
     *
     * @param session the active session
     */
    @Override
    public void undo(Session session) {
        session.getImages().remove(result);
    }
}