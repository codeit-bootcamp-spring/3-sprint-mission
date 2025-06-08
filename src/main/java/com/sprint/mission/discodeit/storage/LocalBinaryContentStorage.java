package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    // 로컬 디스크의 루트 경로 discodeit.storage.local.root-path 설정값을 정의하고, 이 값을 통해 주입
    @Value("${discodeit.storage.local.root-path}")
    private Path root;

    // 루트 디렉토리를 초기화, Bean이 생성되면 자동으로 호출되도록 함
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create storage root directory", e);
        }
    }

    // 파일의 실제 저장 위치에 대한 규칙을 정의 / 파일 저장 위치 규칙 예시: {root}/{UUID}
    // put, get 메소드에서 호출해 일관된 파일 경로 규칙을 유지합니다.
    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }

    @Override
    public UUID put(UUID id, byte[] data) {
        Path path = resolvePath(id);
        try {
            Files.write(path, data, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write data to storage", e);
        }
        return id;
    }

    @Override
    public InputStream get(UUID id) {
        Path path = resolvePath(id);
        try {
            if (!Files.exists(path)) {
                throw new NoSuchElementException("No such file or directory: " + path);
            }
            return Files.newInputStream(path, StandardOpenOption.READ);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read data from storage", e);
        }
    }


    // get 메소드를 통해 파일의 바이너리 데이터를 조회합니다.
    // BinaryContentDto와 바이너리 데이터를 활용해 ResponseEntity<Resource> 응답을 생성 후 반환합니다.
    @Override
    public ResponseEntity<Resource> download(BinaryContentDto dto) {
        try (InputStream inputStream = get(dto.id())) {
            byte[] bytes = inputStream.readAllBytes();
            Resource resource = new ByteArrayResource(bytes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(dto.contentType()));
            headers.setContentLength(dto.size());
            headers.setContentDispositionFormData("attachment", dto.fileName());

            return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
