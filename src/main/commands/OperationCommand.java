package main.commands;

import main.session.Session;
import main.image_operation.ImageEntry;
import main.image_operation.ImageOperation;

/**
 * A command that applies an {@link ImageOperation} to all images
 * in a session.
 * <p>
 * This command follows the Command design pattern and allows
 * operations to be executed and undone across an entire session.
 * </p>
 */
public class OperationCommand implements Command {

    /**
     * The image operation to apply.
     */
    private ImageOperation op;

    /**
     * Constructs a new operation command.
     *
     * @param op the image operation to apply
     */
    public OperationCommand(ImageOperation op) {
        this.op = op;
    }

    /**
     * Executes the operation by adding it to all images in the session.
     *
     * @param session the session containing images
     */
    @Override
    public void execute(Session session) {
        for (ImageEntry img : session.getImages()) {
            img.addOperation(op);
        }
    }

    /**
     * Undoes the operation by removing it from all images in the session.
     *
     * @param session the session containing images
     */
    @Override
    public void undo(Session session) {
        for (ImageEntry img : session.getImages()) {
            img.removeOperation(op);
        }
    }
}