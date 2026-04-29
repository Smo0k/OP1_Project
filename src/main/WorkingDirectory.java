package main;

import java.io.File;

public class WorkingDirectory {

    private String basePath = "";

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

    public String resolve(String fileName) {

        File f = new File(fileName);

        if (f.isAbsolute()) {
            return fileName;
        }

        return basePath + fileName;
    }

    public String getDirectory() {
        return basePath;
    }
}
