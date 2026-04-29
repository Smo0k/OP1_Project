package main;

import java.awt.image.BufferedImage;
import java.io.*;

public class PNMReader {

    public ImageData read(File file, String path) throws IOException {

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
                        int g = magic.equals("P2")
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

                        int r = magic.equals("P3") ? Integer.parseInt(readToken(is)) : is.read();
                        int g = magic.equals("P3") ? Integer.parseInt(readToken(is)) : is.read();
                        int b = magic.equals("P3") ? Integer.parseInt(readToken(is)) : is.read();

                        r = (r * 255) / maxVal;
                        g = (g * 255) / maxVal;
                        b = (b * 255) / maxVal;

                        img.setRGB(x, y, (r << 16) | (g << 8) | b);
                    }
                break;

            case "P4":
                for (int y = 0; y < height; y++)
                    for (int x = 0; x < width;) {
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

        return new ImageData(img, magic, path);
    }

    private String readToken(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        int b;

        while (true) {
            b = is.read();
            if (b == '#') while (b != '\n') b = is.read();
            else if (!Character.isWhitespace(b)) break;
        }

        while (b != -1 && !Character.isWhitespace(b)) {
            sb.append((char) b);
            b = is.read();
        }

        return sb.toString();
    }
}

