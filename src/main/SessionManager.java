package main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private Map<Integer, Session> sessions = new HashMap<>();
    private int nextId = 1;

    private static void checkSession(int id, SessionManager manager) {
        if (id == -1 || manager.getSession(id) == null) {
            throw new IllegalStateException("No active session");
        }
    }

    public void close(int id) {
        if (!sessions.containsKey(id)) {
            throw new RuntimeException("Session not found: " + id);
        }

        sessions.remove(id);
    }

    private ImageData decodeImage(String path) throws IOException {

        File file = new File(path);
        BufferedImage img;
        String format = null;

        if (isPNM(path)) {
            PNMResult result = readPNM(file);
            img = result.image;
            format = result.format;
        } else {
            img = ImageIO.read(file);
        }

        if (img == null) {
            throw new IOException("Invalid image: " + path);
        }

        return new ImageData(img, format, path);
    }

    // =========================
    // LOAD
    // =========================
    public int load(String path) throws IOException {

        ImageData data = decodeImage(path);

        Session session = new Session(
                nextId,
                data.image,
                data.format,
                data.path
        );

        sessions.put(nextId, session);
        return nextId++;
    }

    // =========================
    // ADD
    // =========================
    public ImageData loadImage(String path) throws IOException {

        ImageData data = decodeImage(path);

        return new ImageData(data.image, data.format, data.path);
    }

    private boolean isPNM(String path) {
        String lower = path.toLowerCase();
        return lower.endsWith(".ppm") ||
                lower.endsWith(".pgm") ||
                lower.endsWith(".pbm");
    }

    // =========================
    // PNM RESULT HOLDER
    // =========================
    private static class PNMResult {
        BufferedImage image;
        String format;

        PNMResult(BufferedImage image, String format) {
            this.image = image;
            this.format = format;
        }
    }

    // =========================
    // READ PNM
    // =========================
    private PNMResult readPNM(File file) throws IOException {

        InputStream is = new BufferedInputStream(new FileInputStream(file));

        String magic = readToken(is);
        int width = Integer.parseInt(readToken(is));
        int height = Integer.parseInt(readToken(is));

        int maxVal = (magic.equals("P1") || magic.equals("P4"))
                ? 1
                : Integer.parseInt(readToken(is));

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        switch (magic) {

            case "P1":
                for (int y = 0; y < height; y++)
                    for (int x = 0; x < width; x++) {
                        int v = Integer.parseInt(readToken(is));
                        img.setRGB(x, y, v == 1 ? 0x000000 : 0xFFFFFF);
                    }
                break;

            case "P2":
            case "P5":
                for (int y = 0; y < height; y++)
                    for (int x = 0; x < width; x++) {
                        int g = (magic.equals("P2"))
                                ? Integer.parseInt(readToken(is))
                                : is.read();

                        g = (g * 255) / maxVal;
                        img.setRGB(x, y, (g << 16) | (g << 8) | g);
                    }
                break;

            case "P3":
            case "P6":
                for (int y = 0; y < height; y++)
                    for (int x = 0; x < width; x++) {

                        int r = (magic.equals("P3"))
                                ? Integer.parseInt(readToken(is))
                                : is.read();

                        int g = (magic.equals("P3"))
                                ? Integer.parseInt(readToken(is))
                                : is.read();

                        int b = (magic.equals("P3"))
                                ? Integer.parseInt(readToken(is))
                                : is.read();

                        r = (r * 255) / maxVal;
                        g = (g * 255) / maxVal;
                        b = (b * 255) / maxVal;

                        img.setRGB(x, y, (r << 16) | (g << 8) | b);
                    }
                break;

            case "P4":
                for (int y = 0; y < height; y++)
                    for (int x = 0; x < width; ) {
                        int b = is.read();
                        for (int bit = 7; bit >= 0 && x < width; bit--) {
                            int v = (b >> bit) & 1;
                            img.setRGB(x, y, v == 1 ? 0x000000 : 0xFFFFFF);
                            x++;
                        }
                    }
                break;

            default:
                throw new IOException("Unsupported format: " + magic);
        }

        is.close();

        return new PNMResult(img, magic);
    }

    private String readToken(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        int b;

        while (true) {
            b = is.read();
            if (b == '#') {
                while (b != '\n') b = is.read();
            } else if (!Character.isWhitespace(b)) {
                break;
            }
        }

        while (b != -1 && !Character.isWhitespace(b)) {
            sb.append((char) b);
            b = is.read();
        }

        return sb.toString();
    }

    // =========================
    // ADD IMAGE
    // =========================
    public void addToSession(int id, String path) throws IOException {

        Session session = sessions.get(id);

        if (session == null) {
            throw new RuntimeException("Session not found: " + id);
        }

        BufferedImage img;
        String format = null;

        if (isPNM(path)) {
            PNMResult result = readPNM(new File(path));
            img = result.image;
            format = result.format;
        } else {
            img = ImageIO.read(new File(path));
        }

        if (img == null) {
            throw new IOException("Invalid image: " + path);
        }

        session.addImage(img, format, path);
    }

    // =========================
    // GET SESSION
    // =========================
    public Session getSession(int id) {
        return sessions.get(id);
    }

    // =========================
    // SAVE (use saveAs internally)
    // =========================
    public void save(int id) throws IOException {

        Session session = sessions.get(id);

        if (session == null) {
            throw new RuntimeException("Session not found: " + id);
        }

        for (ImageEntry entry : session.getImages()) {

            BufferedImage img = entry.process();

            String path = entry.getSourcePath();

            if (path == null) {
                throw new IOException("No original path found. Use saveAs instead.");
            }

            int dot = path.lastIndexOf('.');
            String format = (dot != -1)
                    ? path.substring(dot + 1).toLowerCase()
                    : "png";

            File file = new File(path);

            if (entry.getFormat() != null) {
                PNMUtil.writePNM(img, file, entry.getFormat());
            } else {
                boolean ok = ImageIO.write(img, format, file);
                if (!ok) throw new IOException("Unsupported format: " + format);
            }

            System.out.println("Saved to: " + file.getAbsolutePath());
        }
    }

    // =========================
    // SAVE AS
    // =========================
    public void saveAs(int id, String path) throws IOException {

        Session session = sessions.get(id);

        if (session == null) {
            throw new RuntimeException("Session not found: " + id);
        }

        var images = session.getImages();

        for (int i = 0; i < images.size(); i++) {

            var entry = images.get(i);
            BufferedImage img = entry.process();
            String pnmFormat = entry.getFormat();

            String outputPath = path;

            if (images.size() > 1) {
                int dot = path.lastIndexOf('.');
                if (dot != -1) {
                    outputPath = path.substring(0, dot) + "_" + i + path.substring(dot);
                } else {
                    outputPath = path + "_" + i;
                }
            }

            int dot = outputPath.lastIndexOf('.');
            String ext = (dot != -1)
                    ? outputPath.substring(dot + 1).toLowerCase()
                    : "png";

            File file = new File(outputPath);

            if (pnmFormat != null) {
                PNMUtil.writePNM(img, file, pnmFormat);
            } else {
                boolean ok = ImageIO.write(img, ext, file);
                if (!ok) {
                    throw new IOException("Unsupported format: " + ext);
                }
            }

            System.out.println("Saved to: " + file.getAbsolutePath());
        }
    }
}
