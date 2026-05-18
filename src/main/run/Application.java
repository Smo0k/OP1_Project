package main.run;

import main.session.SessionManager;

/**
 * Entry point of the application.
 * <p>
 * Starts both the console interface and the graphical viewer.
 * The console runs on a separate thread, while the GUI is launched
 * on the Swing Event Dispatch Thread.
 * </p>
 */
public class Application {

    /**
     * Main method that starts the application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        SessionManager manager = new SessionManager();

        // Start console UI in a separate thread
        new Thread(() -> {
            ConsoleUI ui = new ConsoleUI(manager);
            ui.run();
        }).start();

        // Start Swing GUI on EDT
        javax.swing.SwingUtilities.invokeLater(() -> {
            Viewer viewer = new Viewer();
            viewer.setVisible(true);
        });
    }
}