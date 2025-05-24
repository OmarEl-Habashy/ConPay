package aammo.ppv.service;

import aammo.ppv.config.MediaConfig;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

public class MediaService {

    private static final String[] ALLOWED_EXTENSIONS = MediaConfig.getAllowedExtensions();
    private static final long MAX_FILE_SIZE = MediaConfig.getMaxFileSize();

    public String saveMedia(Part filePart, int userId) throws IOException {
        validateFile(filePart);

        String fileName = getUniqueFileName(filePart.getSubmittedFileName(), userId);
        String uploadDir = MediaConfig.getUploadDirectory();

        // Create the upload directory path - using absolute path
        Path dirPath = Paths.get(uploadDir).toAbsolutePath();

        // Ensure directory exists
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
            System.out.println("Created directory: " + dirPath);
        }

        Path filePath = dirPath.resolve(fileName);
        filePart.write(filePath.toString());

        LogManager.logAction("MEDIA_UPLOAD", "User=" + userId + ", File=" + fileName);

        // Return the relative path to access the file
        return "/media/" + fileName;
    }

    private void validateFile(Part filePart) throws IOException {
        // Check file size
        if (filePart.getSize() > MAX_FILE_SIZE) {
            throw new IOException("File size exceeds the maximum allowed size of " +
                    (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }

        // Check file extension
        String fileName = filePart.getSubmittedFileName();
        String extension = getFileExtension(fileName).toLowerCase();

        if (!Arrays.asList(ALLOWED_EXTENSIONS).contains(extension)) {
            throw new IOException("File type not allowed. Supported types: " +
                    String.join(", ", ALLOWED_EXTENSIONS));
        }
    }

    private String getUniqueFileName(String originalFilename, int userId) {
        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return userId + "_" + timestamp + "_" + UUID.randomUUID().toString().substring(0, 8) + "." + extension;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }

    public boolean deleteMedia(String filePath) {
        try {
            Path path = Paths.get(MediaConfig.getUploadDirectory(),
                    filePath.substring(filePath.lastIndexOf("/") + 1));
            Files.deleteIfExists(path);
            LogManager.logAction("MEDIA_DELETE", "File=" + filePath);
            return true;
        } catch (IOException e) {
            LogManager.logAction("MEDIA_DELETE_FAILED", "File=" + filePath + ", Error=" + e.getMessage());
            return false;
        }
    }
}