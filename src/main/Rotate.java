package main;

import java.awt.image.BufferedImage;

public class Rotate implements ImageOperation {

    private RotateDirection direction;

    public Rotate(RotateDirection direction) {
        this.direction = direction;
    }

    @Override
    public BufferedImage apply(BufferedImage img) {

        int w = img.getWidth();
        int h = img.getHeight();

        BufferedImage result;

        switch (direction) {

            case RIGHT:
                result = new BufferedImage(h, w, img.getType());

                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        result.setRGB(h - 1 - y, x, img.getRGB(x, y));
                    }
                }
                break;

            case LEFT:
                result = new BufferedImage(h, w, img.getType());

                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        result.setRGB(y, w - 1 - x, img.getRGB(x, y));
                    }
                }
                break;

            default:
                throw new IllegalStateException("Unknown direction");
        }

        return result;
    }
}
