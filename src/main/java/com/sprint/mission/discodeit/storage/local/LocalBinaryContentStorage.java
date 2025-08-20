package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.exception.binaryContent.FileStorageErrorException;
import com.sprint.mission.discodeit.exception.binaryContent.ResourceUrlCreationErrorException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "local")
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
            log.info("파일 데이터 로컬에 저장 완료 - id: {}", id);
            return id;
        } catch (IOException e) {
            throw new FileStorageErrorException(id);
        }
    }

    @Override
    public InputStream get(UUID id) {
        Path file = resolvePath(id);
        try {
            return Files.newInputStream(file);
        } catch (IOException e) {
            throw new FileStorageErrorException(id);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto dto) {
        Path file = resolvePath(dto.id());
        Resource resource;
        try {
            resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new FileStorageErrorException(dto.id());
            }
        } catch (MalformedURLException e) {
            throw new ResourceUrlCreationErrorException(dto.id());
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
