package main;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private WorkingDirectory workingDirectory = new WorkingDirectory();
    private Map<Integer, Session> sessions = new HashMap<>();
    private int nextId = 1;

    private ImageLoader loader = new ImageLoader(workingDirectory);
    private ImageSaver saver = new ImageSaver(workingDirectory);

    public int load(String path) throws IOException {

        ImageData data = loader.load(path);

        Session session = new Session(
                nextId,
                data.image,
                data.format,
                data.path
        );

        sessions.put(nextId, session);
        return nextId++;
    }

    public ImageData loadImage(String path) throws IOException {
        return loader.load(path);
    }

    public void close(int id) {
        if (!sessions.containsKey(id)) {
            throw new RuntimeException("Session not found: " + id);
        }
        sessions.remove(id);
    }

    public Session getSession(int id) {
        return sessions.get(id);
    }

    public void save(int id) throws IOException {
        Session s = requireSession(id);
        saver.save(s);
    }

    public void saveAs(int id, String path) throws IOException {
        Session s = requireSession(id);
        saver.saveAs(s, path);
    }

    private Session requireSession(int id) {
        Session s = sessions.get(id);
        if (s == null) throw new RuntimeException("Session not found: " + id);
        return s;
    }

    public void setDirectory(String path) {
        workingDirectory.setDirectory(path);
    }

    public WorkingDirectory getWorkingDirectory() {
        return workingDirectory;
    }
}

