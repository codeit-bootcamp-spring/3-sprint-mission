package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.common.ErrorMessages;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String rootPath) {
        this.root = Paths.get(rootPath);
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException(ErrorMessages.format(
                "LocalBinaryContentStorage", ErrorMessages.ERROR_FILE_INIT_FAILED), e);
        }
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        try {
            Path path = resovlePath(id);
            Files.write(path, bytes);
            return id;
        } catch (IOException e) {
            throw new RuntimeException(ErrorMessages.format(
                "BinaryContent", ErrorMessages.ERROR_FILE_SAVE_FAILED), e);
        }
    }

    @Override
    public InputStream get(UUID id) {
        try {
            return Files.newInputStream(resovlePath(id));
        } catch (IOException e) {
            throw new RuntimeException(ErrorMessages.format(
                "BinaryContent", ErrorMessages.ERROR_FILE_READ_FAILED), e);
        }
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto binaryContentDto) {
        try {
            Path path = resovlePath(binaryContentDto.id());
            InputStream inputStream = Files.newInputStream(path);
            Resource resource = new InputStreamResource(inputStream);

            return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + binaryContentDto.fileName() + "\"")
                .contentType(MediaType.parseMediaType(binaryContentDto.contentType()))
                .contentLength(binaryContentDto.size())
                .body(resource);

        } catch (IOException e) {
            throw new RuntimeException(ErrorMessages.format(
                "BinaryContent", ErrorMessages.ERROR_FILE_DOWNLOAD_FAILED), e);
        }
    }

    private Path resovlePath(UUID id) {
        return root.resolve(id.toString());
    }
}
