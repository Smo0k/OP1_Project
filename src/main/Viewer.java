package main;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Viewer extends JFrame {

    private final JLabel imageLabel;
    private BufferedImage originalImage;

    private double zoom = 50.0; // 1.0 = 100%

    public Viewer() {
        setTitle("PNM Viewer (P1–P6)");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();

        JButton openButton = new JButton("Open Image");
        JButton zoomInButton = new JButton("Zoom In");
        JButton zoomOutButton = new JButton("Zoom Out");

        openButton.addActionListener(e -> openImage());
        zoomInButton.addActionListener(e -> zoomIn());
        zoomOutButton.addActionListener(e -> zoomOut());

        topPanel.add(openButton);
        topPanel.add(zoomInButton);
        topPanel.add(zoomOutButton);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(imageLabel), BorderLayout.CENTER);
    }

    // Open image
    private void openImage() {
        JFileChooser chooser = new JFileChooser();

        // Set starting directory here
        chooser.setCurrentDirectory(new File("C:/Users/Smook/Desktop/University/2025-2026/Java/OP1_Project/src/images"));

        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                originalImage = readPNM(chooser.getSelectedFile());
                updateImageDisplay();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to load image");
            }
        }
    }

    // Zoom In
    private void zoomIn() {
        if (originalImage == null) return;
        zoom *= 1.25;
        updateImageDisplay();
    }

    // Zoom Out
    private void zoomOut() {
        if (originalImage == null) return;
        zoom /= 1.25;
        updateImageDisplay();
    }

    // Update display
    private void updateImageDisplay() {
        int newW = (int) (originalImage.getWidth() * zoom);
        int newH = (int) (originalImage.getHeight() * zoom);

        Image scaled = originalImage.getScaledInstance(newW, newH, Image.SCALE_FAST);
        imageLabel.setIcon(new ImageIcon(scaled));
    }

    // Full PNM reader (same as before)
    private BufferedImage readPNM(File file) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(file));

        String magic = readToken(is);
        int width = Integer.parseInt(readToken(is));
        int height = Integer.parseInt(readToken(is));

        int maxVal = (magic.equals("P1") || magic.equals("P4")) ? 1 : Integer.parseInt(readToken(is));

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        switch (magic) {

            case "P1":
                for (int y = 0; y < height; y++)
                    for (int x = 0; x < width; x++) {
                        int val = Integer.parseInt(readToken(is));
                        img.setRGB(x, y, val == 1 ? 0x000000 : 0xFFFFFF);
                    }
                break;

            case "P2":
                for (int y = 0; y < height; y++)
                    for (int x = 0; x < width; x++) {
                        int gray = Integer.parseInt(readToken(is));
                        int s = (gray * 255) / maxVal;
                        img.setRGB(x, y, (s << 16) | (s << 8) | s);
                    }
                break;

            case "P3":
                for (int y = 0; y < height; y++)
                    for (int x = 0; x < width; x++) {
                        int r = Integer.parseInt(readToken(is));
                        int g = Integer.parseInt(readToken(is));
                        int b = Integer.parseInt(readToken(is));
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
                            int val = (b >> bit) & 1;
                            img.setRGB(x, y, val == 1 ? 0x000000 : 0xFFFFFF);
                            x++;
                        }
                    }
                break;

            case "P5":
                for (int y = 0; y < height; y++)
                    for (int x = 0; x < width; x++) {
                        int gray = is.read();
                        int s = (gray * 255) / maxVal;
                        img.setRGB(x, y, (s << 16) | (s << 8) | s);
                    }
                break;

            case "P6":
                for (int y = 0; y < height; y++)
                    for (int x = 0; x < width; x++) {
                        int r = is.read();
                        int g = is.read();
                        int b = is.read();
                        r = (r * 255) / maxVal;
                        g = (g * 255) / maxVal;
                        b = (b * 255) / maxVal;
                        img.setRGB(x, y, (r << 16) | (g << 8) | b);
                    }
                break;

            default:
                throw new IOException("Unsupported format: " + magic);
        }

        is.close();
        return img;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Viewer().setVisible(true);
        });
    }
}