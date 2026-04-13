package main;

import java.awt.image.BufferedImage;

public interface ImageOperation {
    BufferedImage apply(BufferedImage img);
}
