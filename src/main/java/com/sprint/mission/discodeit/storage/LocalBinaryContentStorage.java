package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.serviceDto.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
// 스프링 빈으로 등록하기 위해 꼭 붙여야 함
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    // 생성자에서 yaml 프로퍼티로부터 루트 경로 주입
    public LocalBinaryContentStorage(
        @Value("${discodeit.storage.local.root-path}") String rootPath) {
        this.root = Path.of(rootPath);
    }

    @PostConstruct
    public void init() {
        System.out.println("ROOT PATH = " + root);
        File rootDir = root.toFile();
        if (!rootDir.exists()) {
            boolean created = rootDir.mkdirs();
            if (!created) {
                throw new RuntimeException(
                    "Failed to create root directory: " + root.toAbsolutePath());
            }
        }
    }

    @Override
    public UUID put(UUID id, byte[] data) throws IOException {
        Path filePath = resolvePath(id);
        Files.write(filePath, data);
        return id;
    }

    @Override
    public InputStream get(UUID binaryContentId) throws IOException {
        Path filePath = resolvePath(binaryContentId);
        if (!Files.exists(filePath)) {
            throw new IOException("File not found: " + filePath);
        }
        return Files.newInputStream(filePath);
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto dto) {
        try {
            InputStream is = this.get(dto.id());
            if (is == null) {
                return ResponseEntity.notFound().build();
            }
            InputStreamResource resource = new InputStreamResource(is);

            String encodedFileName = UriUtils.encode(dto.fileName(), StandardCharsets.UTF_8);
            String contentDisposition = "attachment; filename*=UTF-8''" + encodedFileName;

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body((Resource) resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // {root}/{UUID} 형태로 저장 경로 리턴
    public Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }
}

