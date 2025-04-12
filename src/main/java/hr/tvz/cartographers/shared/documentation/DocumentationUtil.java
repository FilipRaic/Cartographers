package hr.tvz.cartographers.shared.documentation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentationUtil {

    public static void writeToFile(Path path, String content) throws IOException {
        Files.createDirectories(path.getParent());
        try {
            Files.writeString(path, content);
        } catch (FileAlreadyExistsException e) {
            Files.writeString(path, content);
        }
    }

    public static void openInBrowser(Path filePath) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(filePath.toUri());
            } else {
                log.error("Desktop browsing is not supported.");
            }
        } catch (Exception e) {
            log.error("Failed to open documentation: {}", e.getMessage());
        }
    }
}
