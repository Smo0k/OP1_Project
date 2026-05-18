package main.commands;

import main.session.Session;

/**
 * Represents a command that can be executed and undone.
 * <p>
 * Implementations of this interface encapsulate an operation
 * that modifies the state of a {@link Session}.
 * </p>
 *
 * <p>
 * This interface follows the Command design pattern,
 * allowing commands to be executed, stored, and reversed.
 * </p>
 */
public interface Command {

    /**
     * Executes the command.
     *
     * @param session the active session on which the command operates
     */
    void execute(Session session);

    /**
     * Reverts the effects of the command.
     *
     * @param session the active session on which the command operates
     */
    void undo(Session session);
}