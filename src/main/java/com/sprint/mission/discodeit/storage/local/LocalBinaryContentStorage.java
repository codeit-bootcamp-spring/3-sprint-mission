package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
@Component
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(
            @Value("${discodeit.storage.local.root-path}") String rootPath) {
        this.root = Paths.get(rootPath);
    }

    /**
     * root 경로가 존재하지 않으면 디렉토리 생성
     */
    @PostConstruct
    public void init() {
        if (!Files.exists(root)) {
            try {
                Files.createDirectories(root);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("파일 저장 경로 초기화 중 오류가 발생하였습니다.");
            }
        }
    }

    /**
     * 주어진 BinaryContent ID를 기준으로 바이트 데이터를 파일로 저장
     *
     * @param id    저장할 파일의 BinaryContent ID
     * @param bytes 저장할 byte[] 데이터
     * @return 저장된 파일의 ID
     */
    @Override
    public UUID put(UUID id, byte[] bytes) {
        log.info("파일 저장 요청: ID = {}", id);

        Path path = resolvePath(id);

        if (Files.exists(path)) {
            throw new IllegalArgumentException("이미 존재하는 파일입니다.");
        }

        try (OutputStream os = Files.newOutputStream(path)) {
            os.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException("파일을 저장하는 중 오류가 발생하였습니다.");
        }

        return id;
    }

    /**
     * 주어진 BinaryContent ID에 해당하는 파일을 읽어 InputStream으로 반환
     *
     * @param id 읽어올 파일의 BinaryContent ID
     * @return 해당 파일의 InputStream
     */
    @Override
    public InputStream get(UUID id) {

        Path path = resolvePath(id);

        if (!Files.exists(path)) {
            throw new RuntimeException("존재하지 않는 파일입니다.");
        }

        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("파일을 읽는 중 오류가 발생하였습니다.");
        }
    }

    /**
     * 주어진 BinaryContentDto를 기반으로 파일 다운로드 응답 생성
     *
     * @param metaData 다운로드할 파일 정보
     * @return 파일이 포함된 HTTP 다운로드 응답
     */
    @Override
    public ResponseEntity<Resource> download(BinaryContentDto metaData) {
        log.info("파일 다운로드 요청: ID = {}, 파일명 = {}, 형식 = {}", metaData.id(), metaData.fileName(),
                metaData.contentType());

        InputStream stream = get(metaData.id());
        Resource resource = new InputStreamResource(stream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition
                .builder("attachment")
                .filename(metaData.fileName(), StandardCharsets.UTF_8)  // Spring 5+ 지원
                .build()
        );
        headers.setContentType(MediaType.parseMediaType(metaData.contentType()));
        headers.setContentLength(metaData.size());

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(resource);
    }

    /**
     * 주어진 id를 기반으로 파일 저장 경로 반환
     *
     * @param id 저장할 파일의 BinaryContent ID
     * @return 파일 저장 경로
     */
    public Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }
}
