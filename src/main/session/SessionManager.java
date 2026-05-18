package main.session;

import main.commands.WorkingDirectory;
import main.image_operation.ImageData;
import main.image_operation.ImageLoader;
import main.image_operation.ImageSaver;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages all active image editing sessions.
 * <p>
 * This class is responsible for:
 * </p>
 * <ul>
 *     <li>Creating and tracking sessions</li>
 *     <li>Loading images into sessions</li>
 *     <li>Saving session data</li>
 *     <li>Managing the working directory</li>
 * </ul>
 */
public class SessionManager {

    /**
     * Current working directory used for file operations.
     */
    private WorkingDirectory workingDirectory = new WorkingDirectory();

    /**
     * Map of active sessions indexed by session ID.
     */
    private Map<Integer, Session> sessions = new HashMap<>();

    /**
     * Next available session ID.
     */
    private int nextId = 1;

    /**
     * Loader responsible for reading images from disk.
     */
    private ImageLoader loader = new ImageLoader(workingDirectory);

    /**
     * Saver responsible for writing images to disk.
     */
    private ImageSaver saver = new ImageSaver(workingDirectory);

    /**
     * Loads an image and creates a new session containing it.
     *
     * @param path the image file path
     * @return the newly created session ID
     * @throws IOException if the image cannot be loaded
     */
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

    /**
     * Loads an image without creating a session.
     *
     * @param path the image file path
     * @return the loaded image data
     * @throws IOException if loading fails
     */
    public ImageData loadImage(String path) throws IOException {
        return loader.load(path);
    }

    /**
     * Closes and removes a session.
     *
     * @param id the session ID
     * @throws RuntimeException if the session does not exist
     */
    public void close(int id) {
        if (!sessions.containsKey(id)) {
            throw new RuntimeException("Session not found: " + id);
        }
        sessions.remove(id);
    }

    /**
     * Retrieves a session by its ID.
     *
     * @param id the session ID
     * @return the session, or {@code null} if not found
     */
    public Session getSession(int id) {
        return sessions.get(id);
    }

    /**
     * Saves a session to its original image paths.
     *
     * @param id the session ID
     * @throws IOException if saving fails
     */
    public void save(int id) throws IOException {
        Session s = requireSession(id);
        saver.save(s);
    }

    /**
     * Saves a session to a new output path.
     *
     * @param id   the session ID
     * @param path the output path
     * @throws IOException if saving fails
     */
    public void saveAs(int id, String path) throws IOException {
        Session s = requireSession(id);
        saver.saveAs(s, path);
    }

    /**
     * Ensures a session exists for the given ID.
     *
     * @param id the session ID
     * @return the session
     * @throws RuntimeException if the session does not exist
     */
    private Session requireSession(int id) {
        Session s = sessions.get(id);
        if (s == null) {
            throw new RuntimeException("Session not found: " + id);
        }
        return s;
    }

    /**
     * Sets the working directory for file operations.
     *
     * @param path the directory path
     */
    public void setDirectory(String path) {
        workingDirectory.setDirectory(path);
    }

    /**
     * Returns the current working directory.
     *
     * @return the working directory
     */
    public WorkingDirectory getWorkingDirectory() {
        return workingDirectory;
    }
}