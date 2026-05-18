package main.commands;

import main.session.Session;
import main.image_operation.ImageEntry;

import java.awt.image.BufferedImage;

/**
 * Command that adds a new image to a {@link Session}.
 *
 * <p>This command encapsulates the creation and insertion of an {@link ImageEntry}
 * into a session. It supports undo/redo by keeping a reference to the exact
 * {@code ImageEntry} instance it adds, ensuring safe and consistent reversal
 * regardless of other changes to the session.</p>
 */
public class AddImageCommand implements Command {

    /** The image data to be added. */
    private final BufferedImage img;

    /** The format of the image (e.g., P1–P6 for PNM or null for standard formats). */
    private final String format;

    /** The original file path of the image. */
    private final String path;

    /** Reference to the image entry created during execution (used for undo). */
    private ImageEntry addedEntry;

    /**
     * Constructs a command for adding a new image to a session.
     *
     * @param img    the image data
     * @param format the image format (PNM type or null)
     * @param path   the source file path of the image
     */
    public AddImageCommand(BufferedImage img, String format, String path) {
        this.img = img;
        this.format = format;
        this.path = path;
    }

    /**
     * Executes the command by creating a new {@link ImageEntry}
     * and adding it to the given session.
     *
     * <p>The created entry is stored internally to allow precise undo.</p>
     *
     * @param session the session to modify
     */
    @Override
    public void execute(Session session) {
        addedEntry = new ImageEntry(img, format, path);
        session.getImages().add(addedEntry);
    }

    /**
     * Undoes the command by removing the exact {@link ImageEntry}
     * that was previously added during execution.
     *
     * <p>This approach is robust and does not rely on list ordering,
     * making it safe even if other operations modify the session.</p>
     *
     * @param session the session to modify
     */
    @Override
    public void undo(Session session) {
        if (addedEntry != null) {
            session.getImages().remove(addedEntry);
        }
    }
}