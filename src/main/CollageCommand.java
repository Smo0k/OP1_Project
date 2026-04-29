package main;

import java.awt.image.BufferedImage;

public class CollageCommand implements Command {

    private ImageEntry img1;
    private ImageEntry img2;
    private ImageEntry result;

    private CollageDirection direction;
    private String outputPath;
    private WorkingDirectory workingDirectory;

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

    @Override
    public void undo(Session session) {
        session.getImages().remove(result);
    }
}