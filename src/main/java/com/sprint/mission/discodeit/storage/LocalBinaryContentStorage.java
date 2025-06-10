package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(
        @Value("${discodeit.storage.local.root-path}") String rootDir) {
        this.root = Paths.get(rootDir);
        init();
    }

    @PostConstruct
    public void init() {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
            log.info("파일 생성 성공: " + root);
        } catch (IOException e) {
            log.error("파일 생성 실패: " + root, e);
            throw new RuntimeException("파일 생성 실패", e);
        }
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        Path path = resolvePath(id);
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, bytes);
            return id;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    @Override
    public InputStream get(UUID id) {
        Path path = resolvePath(id);
        try {
            if (!Files.exists(path)) {
                throw new RuntimeException("파일 찾을 수 없음.");
            }
            return new FileInputStream(path.toFile());
        } catch (IOException e) {
            throw new RuntimeException("파일 읽기 실패: " + id, e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
        InputStream inputStream = get(binaryContentDto.id());
        Resource resource = new InputStreamResource(inputStream);

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(binaryContentDto.contentType()))
            .contentLength(binaryContentDto.size())
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + (binaryContentDto.fileName()) + "\"")
            .body(resource);
    }

    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }
}
