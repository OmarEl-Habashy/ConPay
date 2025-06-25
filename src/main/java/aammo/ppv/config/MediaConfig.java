package aammo.ppv.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class MediaConfig {
    private static final Properties properties = new Properties();
    private static final String UPLOAD_DIR_PROPERTY = "media.upload.dir";
    private static final String MAX_FILE_SIZE_PROPERTY = "media.max.file.size";
    private static final String ALLOWED_EXTENSIONS_PROPERTY = "media.allowed.extensions";

    // Default upload directory within the project
    private static final String DEFAULT_UPLOAD_DIR = "uploaded-media";

    static {
        try (InputStream input = MediaConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Ensure the upload directory exists
        ensureUploadDirectoryExists();
    }

    private static void ensureUploadDirectoryExists() {
        String uploadDir = getUploadDirectory();
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            System.out.println("Creating upload directory: " + uploadDir + " - Success: " + created);
        }
    }

    public static String getUploadDirectory() {
        return properties.getProperty(UPLOAD_DIR_PROPERTY, DEFAULT_UPLOAD_DIR);
    }

    public static long getMaxFileSize() {
        return Long.parseLong(properties.getProperty(MAX_FILE_SIZE_PROPERTY, "10242880"));
    }

    public static String[] getAllowedExtensions() {
        return properties.getProperty(ALLOWED_EXTENSIONS_PROPERTY, "jpg,jpeg,png,gif").split(",");
    }
}