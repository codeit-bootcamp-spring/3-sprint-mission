package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path ROOT;

    public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") Path root) {
        ROOT = root;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(ROOT);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage folder", e);
        }
    }

    public Path resolvePath(UUID id) {
        return ROOT.resolve(id.toString());
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        Path path = resolvePath(id);
        try {
            Files.write(path, bytes);
            return id;
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file: " + path, e);
        }
    }

    @Override
    public InputStream get(UUID id) {
        Path path = resolvePath(id);

        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new RuntimeException("File not found: " + path, e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto binaryContent) {
        try {
            Path path = resolvePath(binaryContent.id());
            Resource resource = new InputStreamResource(get(binaryContent.id()));

            return ResponseEntity.ok()
                    .contentLength(binaryContent.size())
                    .contentType(MediaType.parseMediaType(binaryContent.contentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + binaryContent.fileName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
