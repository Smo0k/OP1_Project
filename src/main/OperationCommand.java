package main;

public class OperationCommand implements Command{
    private ImageOperation op;

    public OperationCommand(ImageOperation op) {
        this.op = op;
    }

    @Override
    public void execute(Session session) {
        for (ImageEntry img : session.getImages()) {
            img.addOperation(op);
        }
    }

    @Override
    public void undo(Session session) {
        for (ImageEntry img : session.getImages()) {
            img.getOperations().remove(op);
        }
    }
}
