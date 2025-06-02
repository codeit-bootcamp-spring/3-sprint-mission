package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    @Autowired
    public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String rootPath) {
        this.root = Paths.get(rootPath);
    }

    @PostConstruct
    public void init() {
        try {
            if (Files.exists(root)) {
                Files.createDirectories(root);
            }
        } catch (IOException e) {
            throw new IllegalStateException("로컬 바이너리 저장소 초기화 실패", e);
        }
    }

    /**
     * UUID에 해당하는 경로 반환
     *
     * @return UUID에 해당하는 경로
     */
    private Path resolvePath(UUID id) {
        return root.resolve(id.toString() + ".ser");
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        Path path = resolvePath(id);

        try {
            Files.createDirectories(path.getParent());
            Files.write(path, bytes);
            return id;
        } catch (IOException e) {
            throw new UncheckedIOException("파일 저장 실패: " + id, e);
        }
    }

    @Override
    public InputStream get(UUID id) {
        Path path = resolvePath(id);

        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new UncheckedIOException("파일 로드 실패: " + id, e);
        }
    }

    @Override
    public ResponseEntity<?> download(BinaryContentResponseDto dto) {
        Path path = resolvePath(dto.id());

        try {
            // MIME 타입
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            Resource resource = new FileSystemResource(path);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dto.fileName() + "\"")
                    .contentLength(dto.size())
                    .body(resource);
        } catch (IOException e) {
            throw new UncheckedIOException("다운로드 실패: " + dto.id(), e);
        }
    }
}
