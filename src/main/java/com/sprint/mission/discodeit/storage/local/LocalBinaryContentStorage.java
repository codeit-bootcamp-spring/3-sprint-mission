package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.exception.binarycontent.DuplicateFileException;
import com.sprint.mission.discodeit.exception.binarycontent.FileInitFailedException;
import com.sprint.mission.discodeit.exception.binarycontent.FileNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.FileReadFailedException;
import com.sprint.mission.discodeit.exception.binarycontent.FileSaveFailedException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
    public UUID put(UUID binaryContentId, byte[] bytes) {
        Path filePath = resolvePath(binaryContentId);
        if (Files.exists(filePath)) {
            throw new DuplicateFileException(binaryContentId, filePath.getFileName().toString());
        }
        try (OutputStream outputStream = Files.newOutputStream(filePath)) {
            outputStream.write(bytes);
        } catch (IOException e) {
            throw new FileSaveFailedException(filePath.toString(), e);
        }
        return binaryContentId;
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
}
