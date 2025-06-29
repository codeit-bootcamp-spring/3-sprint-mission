package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(value = "discodeit.storage.type", havingValue = "local", matchIfMissing = false)
@Component
public class LocalBinaryContentStorage implements BinaryContentStorage{

    private final Path root;

    public LocalBinaryContentStorage(
            @Value("${discodeit.storage.local.root-path:data}") Path root
    ) {
        this.root = root;
    }

    @PostConstruct
    public void init() {
        if (!Files.exists(root)) {
            try {
                Files.createDirectories(root);
            } catch (IOException e) {
                throw new RuntimeException("경로 생성 중 오류 발생 ", e);
            }
        }
    }

    private Path resolvePath(UUID key) {
        return root.resolve(key.toString());
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        Path path = resolvePath(id);

        if (Files.exists(path)) {
            throw new IllegalArgumentException(id + " 아이디를 가지는 파일이 이미 존재합니다.");
        }

        try(OutputStream outputStream = Files.newOutputStream(path)){
            outputStream.write(bytes);
        }catch (IOException e){
            throw new RuntimeException("바이트 파일 저장 중 오류 발생", e);
        }
        return id;
    }

    // 스트림을 try-with-resources로 닫으면 안됨 (닫힌 스트림을 반환하면 사용할 수 없음)
    @Override
    public InputStream get(UUID id) {
        Path path = resolvePath(id);

        if (Files.notExists(path)) {
            throw new NoSuchElementException(id + ", 파일을 찾을 수 없습니다.");
        }
        try {
            return Files.newInputStream(path);
        }catch (IOException e){
            throw new RuntimeException("바이트 파일 읽기 중 오류 발생", e);
        }
    }


    @Override
    public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
        InputStream inputStream = get(binaryContentDto.id()); // 예외를 상위로 던짐
        InputStreamResource resource = new InputStreamResource(inputStream);

        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attchment; filename=\"" + binaryContentDto.fileName() + "\"")
            .header(HttpHeaders.CONTENT_TYPE, binaryContentDto.contentType())
            .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(binaryContentDto.size()))
            .body(resource);
    }


    @Override
    public void delete(UUID id) {
        Path path = resolvePath(id);

        try {
            boolean deleted = Files.deleteIfExists(path);
            if (!deleted) {
                throw new FileNotFoundException("해당 바이트 파일이 존재하지 않습니다: " + path);
            }
        } catch (IOException e) {
            throw new RuntimeException(path + " 경로의 바이트 파일 삭제 중 오류 발생: ", e);
        }
    }
}
