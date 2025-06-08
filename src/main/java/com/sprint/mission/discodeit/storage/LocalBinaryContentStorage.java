package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    prefix = "discodeit.storage",
    name = "type",
    havingValue = "local"
)
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(
        @Value("${discodeit.storage.local.root-path}") String rootPath
    ) {
        this.root = Paths.get(rootPath);
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new IllegalStateException(
                "Could not initialize storage root at " + root, e);
        }
    }

    @Override
    public UUID put(UUID id, byte[] data) {
        Path file = resolvePath(id);
        try {
            Files.write(file, data);
            return id;
        } catch (IOException e) {
            throw new IllegalStateException(
                "Failed to store binary content with id " + id, e);
        }
    }

    @Override
    public InputStream get(UUID id) {
        Path file = resolvePath(id);
        try {
            return Files.newInputStream(file);
        } catch (IOException e) {
            throw new IllegalStateException(
                "Failed to read binary content with id " + id, e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto dto) {
        Path file = resolvePath(dto.id());
        Resource resource;
        try {
            resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalStateException(
                    "Could not read file for download: " + file);
            }
        } catch (MalformedURLException e) {
            throw new IllegalStateException(
                "Failed to create URL resource for file: " + file, e);
        }

        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + dto.fileName() + "\""
            )
            .contentType(MediaType.parseMediaType(dto.contentType()))
            .contentLength(dto.size())
            .body(resource);
    }

    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }
}
