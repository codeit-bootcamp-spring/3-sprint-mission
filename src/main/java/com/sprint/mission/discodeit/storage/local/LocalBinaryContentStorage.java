package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.exception.binarycontent.FileInitFailedException;
import com.sprint.mission.discodeit.exception.binarycontent.FileNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.FileReadFailedException;
import com.sprint.mission.discodeit.exception.binarycontent.FileSaveFailedException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.micrometer.core.annotation.Timed;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(
        @Value("${discodeit.storage.local.root-path}") Path root) {
        this.root = root;
    }

    @PostConstruct
    void checkProxy() {
        log.info("[AOP] LocalBinaryContentStorage proxied? {} ({})",
            org.springframework.aop.support.AopUtils.isAopProxy(this),
            this.getClass().getName());
    }

    @PostConstruct
    public void init() {
        if (!Files.exists(root)) {
            try {
                Files.createDirectories(root);
            } catch (IOException e) {
                throw new FileInitFailedException(root.toString(), e);
            }
        }
    }

    @Override
    @Timed(value = "storage.put.local", description = "로컬 저장소 파일 저장", histogram = true, percentiles = {
        0.5, 0.95, 0.99})
    public UUID put(UUID binaryContentId, byte[] bytes) {
        log.info("[LocalStorage] put(byte[]) start key={}, size={}, thread={}",
            binaryContentId, bytes.length, Thread.currentThread().getName());

        Path filePath = resolvePath(binaryContentId);
        try {
            Thread.sleep(3000); // 3초 지연
            Files.createDirectories(filePath.getParent());
            try (OutputStream os = Files.newOutputStream(filePath)) {
                os.write(bytes);
            }
            log.info("[LocalStorage] put(byte[]) end key={}", binaryContentId);
            return binaryContentId;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while simulating delay", ie);
        } catch (IOException ioe) {
            throw new FileSaveFailedException(filePath.toString(), ioe);
        }
    }

    @Override
    public void put(String objectKey, InputStream in, long contentLength, String contentType) {
        try (in) {
            byte[] bytes = in.readAllBytes();
            put(UUID.fromString(objectKey), bytes);
        } catch (IOException e) {
            throw new FileSaveFailedException(resolvePath(objectKey).toString(), e);
        }
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        Path filePath = resolvePath(binaryContentId);
        if (Files.notExists(filePath)) {
            throw new FileNotFoundException(binaryContentId);
        }
        try {
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            throw new FileReadFailedException(filePath.toString(), e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
        InputStream inputStream = get(binaryContentDto.id());
        Resource resource = new InputStreamResource(inputStream);

        // RFC 5987표준을 사용해 파일명 UTF-8로 인코딩
        String encodedFilename = URLEncoder.encode(binaryContentDto.fileName(),
            StandardCharsets.UTF_8);

        return ResponseEntity
            .ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename*=UTF-8''" + encodedFilename)
            .contentType(MediaType.parseMediaType(binaryContentDto.contentType()))
            .contentLength(binaryContentDto.size())
            .body(resource);
    }

    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }

    private Path resolvePath(String objectKey) {
        return root.resolve(objectKey);
    }
}
