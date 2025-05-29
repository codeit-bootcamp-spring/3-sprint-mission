package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(
            @Value("${discodeit.storage.local.root-path}") Path root) {
        this.root = root;
    }


    @PostConstruct
    public void init() throws IOException {
        if (Files.notExists(root)) {
            Files.createDirectories(root);
        }
    }

    @Override
    public UUID put(UUID id, byte[] data) throws IOException {
        Path file = resolvePath(id);
        Files.write(file, data,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
        return id;
    }

    @Override
    public InputStream get(UUID id) throws IOException {
        Path file = resolvePath(id);
        if (Files.notExists(file)) {
            throw new FileNotFoundException("File not found: " + id);
        }
        return Files.newInputStream(file, StandardOpenOption.READ);
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto dto) throws IOException {
        Resource resource = new InputStreamResource(get(dto.id()));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(dto.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + dto.fileName() + "\"")
                .body(resource);
    }

    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }
}