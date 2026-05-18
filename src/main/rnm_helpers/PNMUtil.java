package main.rnm_helpers;

import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Utility class for writing images in the PNM (Portable Any Map) format family.
 * <p>
 * Supports all major PNM variants:
 * </p>
 * <ul>
 *     <li>P1 / P4 - black and white (ASCII / binary)</li>
 *     <li>P2 / P5 - grayscale (ASCII / binary)</li>
 *     <li>P3 / P6 - RGB color (ASCII / binary)</li>
 * </ul>
 *
 * <p>
 * This class converts a {@link BufferedImage} into the requested PNM format
 * and writes it directly to a file.
 * </p>
 */
public class PNMUtil {

    /**
     * Writes an image in the specified PNM format.
     *
     * @param img    the image to write
     * @param file   the output file
     * @param format the PNM format (P1–P6)
     * @throws IOException if the format is unsupported or writing fails
     */
    public static void writePNM(BufferedImage img, File file, String format) throws IOException {

        format = format.toUpperCase();

        switch (format) {

            case "P1":
                writeP1(img, file);
                break;

            case "P2":
                writeP2(img, file);
                break;

            case "P3":
                writeP3(img, file);
                break;

            case "P4":
                writeP4(img, file);
                break;

            case "P5":
                writeP5(img, file);
                break;

            case "P6":
                writeP6(img, file);
                break;

            default:
                throw new IOException("Unsupported PNM format: " + format);
        }
    }

    /**
     * Writes a black-and-white ASCII PNM image (P1).
     */
    private static void writeP1(BufferedImage img, File file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));

        int w = img.getWidth(), h = img.getHeight();

        bw.write("P1\n");
        bw.write(w + " " + h + "\n");

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                int rgb = img.getRGB(x, y);

                int gray = (rgb >> 16 & 0xFF)
                        + (rgb >> 8 & 0xFF)
                        + (rgb & 0xFF);

                bw.write((gray < 384 ? "1 " : "0 "));
            }
            bw.write("\n");
        }

        bw.close();
    }

    /**
     * Writes a grayscale ASCII PNM image (P2).
     */
    private static void writeP2(BufferedImage img, File file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));

        int w = img.getWidth(), h = img.getHeight();

        bw.write("P2\n");
        bw.write(w + " " + h + "\n255\n");

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                int rgb = img.getRGB(x, y);

                int gray = ((rgb >> 16) & 0xFF)
                        + ((rgb >> 8) & 0xFF)
                        + (rgb & 0xFF);

                gray /= 3;

                bw.write(gray + " ");
            }
            bw.write("\n");
        }

        bw.close();
    }

    /**
     * Writes an RGB ASCII PNM image (P3).
     */
    private static void writeP3(BufferedImage img, File file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));

        int w = img.getWidth(), h = img.getHeight();

        bw.write("P3\n");
        bw.write(w + " " + h + "\n255\n");

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                int rgb = img.getRGB(x, y);

                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                bw.write(r + " " + g + " " + b + " ");
            }
            bw.write("\n");
        }

        bw.close();
    }

    /**
     * Writes a black-and-white binary PNM image (P4).
     */
    private static void writeP4(BufferedImage img, File file) throws IOException {
        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));

        int w = img.getWidth(), h = img.getHeight();

        os.write(("P4\n" + w + " " + h + "\n").getBytes());

        for (int y = 0; y < h; y++) {

            int bitCount = 0;
            int currentByte = 0;

            for (int x = 0; x < w; x++) {

                int rgb = img.getRGB(x, y);

                int gray = ((rgb >> 16) & 0xFF)
                        + ((rgb >> 8) & 0xFF)
                        + (rgb & 0xFF);

                int bit = (gray < 384) ? 1 : 0;

                currentByte = (currentByte << 1) | bit;
                bitCount++;

                if (bitCount == 8) {
                    os.write(currentByte);
                    bitCount = 0;
                    currentByte = 0;
                }
            }

            if (bitCount > 0) {
                currentByte <<= (8 - bitCount);
                os.write(currentByte);
            }
        }

        os.close();
    }

    /**
     * Writes a grayscale binary PNM image (P5).
     */
    private static void writeP5(BufferedImage img, File file) throws IOException {
        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));

        int w = img.getWidth(), h = img.getHeight();

        os.write(("P5\n" + w + " " + h + "\n255\n").getBytes());

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                int rgb = img.getRGB(x, y);

                int gray = ((rgb >> 16) & 0xFF)
                        + ((rgb >> 8) & 0xFF)
                        + (rgb & 0xFF);

                gray /= 3;

                os.write(gray);
            }
        }

        os.close();
    }

    /**
     * Writes an RGB binary PNM image (P6).
     */
    private static void writeP6(BufferedImage img, File file) throws IOException {
        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));

        int w = img.getWidth(), h = img.getHeight();

        os.write(("P6\n" + w + " " + h + "\n255\n").getBytes());

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                int rgb = img.getRGB(x, y);

                os.write((rgb >> 16) & 0xFF);
                os.write((rgb >> 8) & 0xFF);
                os.write(rgb & 0xFF);
            }
        }

        os.close();
    }
}