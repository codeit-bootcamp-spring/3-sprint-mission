package com.sprint.mission.discodeit.util;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
@ConfigurationProperties(prefix = "discodeit.repository")
public class FilePathProperties {

    private String fileDirectory;

    private Path userDirectory;
    private Path channelDirectory;
    private Path messageDirectory;
    private Path userStatusDirectory;
    private Path binaryContentDirectory;
    private Path readStatusDirectory;

    private static final String TO_SER = ".ser";

    @PostConstruct
    public void init() {
        Path base = Paths.get(fileDirectory);

        this.userDirectory = base.resolve("user");
        this.channelDirectory = base.resolve("channel");
        this.messageDirectory = base.resolve("message");
        this.userStatusDirectory = base.resolve("userStatus");
        this.binaryContentDirectory = base.resolve("binaryContent");
        this.readStatusDirectory = base.resolve("readStatus");

        for (Path dir : new Path[]{
                userDirectory, channelDirectory, messageDirectory,
                userStatusDirectory, binaryContentDirectory, readStatusDirectory
        }) {
            createIfNotExists(dir);
        }
    }

    private void createIfNotExists(Path dir) {
        try {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            throw new RuntimeException("디렉토리 생성 실패: " + dir, e);
        }
    }

    public void setFileDirectory(String fileDirectory) {
        this.fileDirectory = fileDirectory;
    }

    public Path getUserDirectory() {
        return userDirectory;
    }

    public Path getChannelDirectory() {
        return channelDirectory;
    }

    public Path getMessageDirectory() {
        return messageDirectory;
    }

    public Path getUserStatusDirectory() {
        return userStatusDirectory;
    }

    public Path getBinaryContentDirectory() {
        return binaryContentDirectory;
    }

    public Path getReadStatusDirectory() {
        return readStatusDirectory;
    }

    public Path getUserFilePath(UUID id) {
        return userDirectory.resolve(id + TO_SER);
    }

    public Path getChannelFilePath(UUID id) {
        return channelDirectory.resolve(id + TO_SER);
    }

    public Path getMessageFilePath(UUID id) {
        return messageDirectory.resolve(id + TO_SER);
    }

    public Path getUserStatusFilePath(UUID id) {
        return userStatusDirectory.resolve(id + TO_SER);
    }

    public Path getBinaryContentFilePath(UUID id) {
        return binaryContentDirectory.resolve(id + TO_SER);
    }

    public Path getReadStatusFilePath(UUID id) {
        return readStatusDirectory.resolve(id + TO_SER);
    }

}

