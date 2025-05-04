package com.sprint.mission.discodeit.util;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.FilePath
 * fileName       : FilePath
 * author         : doungukkim
 * date           : 2025. 4. 14.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 14.        doungukkim       최초 생성
 */
@Component
public class FilePathProperties {
private static final String TO_FILES = "src/main/java/com/sprint/mission/data/";
    private static final Path USER_DIRECTORY = Paths.get(TO_FILES+"user");
    private static final Path CHANNEL_DIRECTORY = Paths.get(TO_FILES+"channel");
    private static final Path MESSAGE_DIRECTORY = Paths.get(TO_FILES+"message");
    private static final Path USER_STATUS_DIRECTORY = Paths.get(TO_FILES + "userStatus");
    private static final Path BINARY_CONTENT_DIRECTORY = Paths.get(TO_FILES + "binaryContent");
    private static final Path READ_STATUS_DIRECTORY = Paths.get(TO_FILES + "readStatus");

    private static final String TO_SER = ".ser";

    private static final Path[] DIRECTORIES = {USER_DIRECTORY, CHANNEL_DIRECTORY, MESSAGE_DIRECTORY, USER_STATUS_DIRECTORY,BINARY_CONTENT_DIRECTORY,READ_STATUS_DIRECTORY};

    public FilePathProperties() {
        for (Path directory : DIRECTORIES) {
            init(directory);
        }
    }

    public void init(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public Path getUserDirectory() {
        return USER_DIRECTORY;
    }

    public Path getUserFilePath(UUID userId) {
        return USER_DIRECTORY.resolve(userId + TO_SER);
    }

    public Path getChannelDirectory() {
        return CHANNEL_DIRECTORY;
    }

    public Path getChannelFilePath(UUID channelId) {
        return CHANNEL_DIRECTORY.resolve(channelId + TO_SER);
    }

    public Path getMessageDirectory() {
        return MESSAGE_DIRECTORY;
    }

    public Path getMessageFilePath(UUID messageId) {
        return MESSAGE_DIRECTORY.resolve(messageId + TO_SER);
    }

    public Path getUserStatusDirectory() {
        return USER_STATUS_DIRECTORY;
    }

    public Path getUserStatusFilePath(UUID userStatusId) {return USER_STATUS_DIRECTORY.resolve(userStatusId + TO_SER);}

    public Path getBinaryContentDirectory() {
        return BINARY_CONTENT_DIRECTORY;
    }

    public Path getBinaryContentFilePath(UUID binaryContentId) {return BINARY_CONTENT_DIRECTORY.resolve(binaryContentId + TO_SER);}

    public Path getReadStatusDirectory() {
        return READ_STATUS_DIRECTORY;
    }

    public Path getReadStatusFilePath(UUID readStatusId) {return READ_STATUS_DIRECTORY.resolve(readStatusId + TO_SER);}

}
