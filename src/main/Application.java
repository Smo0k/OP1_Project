package main;

public class Application {

    public static void main(String[] args) {
        SessionManager manager = new SessionManager();
        ConsoleUI ui = new ConsoleUI(manager);

        ui.run();
    }
}
