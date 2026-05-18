package main.commands;

import java.io.File;

/**
 * Manages the application's working directory for file operations.
 * <p>
 * This class is responsible for resolving relative file paths
 * into absolute paths based on a configured base directory.
 * </p>
 */
public class WorkingDirectory {

    /**
     * The base directory used for resolving relative paths.
     */
    private String basePath = "";

    /**
     * Sets the working directory.
     * <p>
     * If the provided path is null or blank, the base directory
     * is reset to an empty string.
     * </p>
     *
     * @param path the directory path to set
     */
    public void setDirectory(String path) {

        if (path == null || path.isBlank()) {
            basePath = "";
            return;
        }

        if (!path.endsWith("/") && !path.endsWith("\\")) {
            path += File.separator;
        }

        basePath = path;
    }

    /**
     * Resolves a file name into an absolute path using the working directory.
     *
     * @param fileName the file name or path
     * @return the resolved absolute path if relative, otherwise the original path
     */
    public String resolve(String fileName) {

        File f = new File(fileName);

        if (f.isAbsolute()) {
            return fileName;
        }

        return basePath + fileName;
    }

    /**
     * Returns the current working directory path.
     *
     * @return the base directory
     */
    public String getDirectory() {
        return basePath;
    }
}