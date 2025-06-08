package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
public class LocalBinaryContentStorage implements BinaryContentStorage{

    private final Path root;

    public LocalBinaryContentStorage(Path root) {
        this.root = root;
    }

    @PostConstruct
    public void init() {

        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not create directory: " + root, e);
        }
    }

    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }

    public UUID put(UUID id, byte[] bytes) {
        Path path = resolvePath(id);
        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public InputStream get(UUID id) {
        Path path = resolvePath(id);
        try {
            byte[] data = Files.readAllBytes(path);

            return BinaryContentDTO.builder()
                    .id(id)
                    .fileName(null)
                    .size((long) data.length)
                    .bytes(data)
                    .build();
                    new BinaryContentDTO(id, null, (long) data.length, data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }

    public ResponseEntity<Resource> download(BinaryContentDto dto) {
        ByteArrayResource resource = new ByteArrayResource(dto.getData());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dto.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    public UUID put(UUID id, byte[] bytes) {
        return null;
    }

    public InputStream get(UUID id) {
        return null;
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDTO dto) {
        return null;
    }
}
