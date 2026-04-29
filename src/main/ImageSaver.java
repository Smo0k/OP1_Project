package main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageSaver {

    private final WorkingDirectory workingDirectory;

    public ImageSaver(WorkingDirectory workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

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

    private void write(BufferedImage img, String path, String pnmFormat) throws IOException {

        File file = new File(path);

        if (pnmFormat != null) {
            PNMUtil.writePNM(img, file, pnmFormat);
        } else {
            String ext = getExt(path);
            boolean ok = ImageIO.write(img, ext, file);

            if (!ok) throw new IOException("Unsupported format: " + ext);
        }
    }

    private String buildPath(String base, int index, int total) {

        if (total == 1) return base;

        int dot = base.lastIndexOf('.');
        if (dot != -1) {
            return base.substring(0, dot) + "_" + index + base.substring(dot);
        }
        return base + "_" + index;
    }

    private String getExt(String path) {
        int dot = path.lastIndexOf('.');
        return (dot != -1) ? path.substring(dot + 1).toLowerCase() : "png";
    }
}

