package com.sprint.mission.discodeit.util;

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
public class FilePathUtil {
    private static final String TO_FILES = "/Users/doungukkim/Desktop/workspace/Codit_Sprint_workspace/codit-organization/some/path/3-sprint-mission/src/main/java/com/sprint/mission/discodeit/files/";

    private static final Path USER_DIRECTORY = Paths.get(TO_FILES+"user");
    private static final Path CHANNEL_DIRECTORY = Paths.get(TO_FILES+"channel");
    private static final Path MESSAGE_DIRECTORY = Paths.get(TO_FILES+"message");
    private static final String TO_SER = ".ser";

    private static final Path[] DIRECTORIES = {USER_DIRECTORY, CHANNEL_DIRECTORY, MESSAGE_DIRECTORY};

    public FilePathUtil() {
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


}
