package main;

public interface Command {
    void execute(Session session);
    void undo(Session session);
}
