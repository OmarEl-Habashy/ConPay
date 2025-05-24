package aammo.ppv.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {

    public static List<String> listFilesInDirectory(String directoryPath) throws IOException {
        return Files.list(Paths.get(directoryPath))
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    public static boolean ensureDirectoryExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            return directory.mkdirs();
        }
        return true;
    }

    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        return file.length();
    }

    public static String getContentType(Path filePath) throws IOException {
        return Files.probeContentType(filePath);
    }
}