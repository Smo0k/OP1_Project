package main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageLoader {

    private final WorkingDirectory workingDirectory;
    private PNMReader pnmReader = new PNMReader();

    public ImageLoader(WorkingDirectory workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public ImageData load(String path) throws IOException {

        String resolvedPath = workingDirectory.resolve(path);
        File file = new File(resolvedPath);

        BufferedImage img;
        String format = null;

        if (isPNM(resolvedPath)) {
            return pnmReader.read(file, resolvedPath);
        }

        img = ImageIO.read(file);

        if (img == null) {
            throw new IOException("Invalid image: " + resolvedPath);
        }

        return new ImageData(img, null, resolvedPath);
    }

    private boolean isPNM(String path) {
        String lower = path.toLowerCase();
        return lower.endsWith(".ppm") ||
                lower.endsWith(".pgm") ||
                lower.endsWith(".pbm");
    }
}

