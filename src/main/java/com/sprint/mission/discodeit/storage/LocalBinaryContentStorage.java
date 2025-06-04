package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
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
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 경로 초기화 중 오류가 발생하였습니다.");
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

        Path path = resolvePath(id);

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
            throw new RuntimeException("파일을 읽는 중 오류가 발생하였습니다.");
        }
    }

    /**
     * 주어진 BinaryContentDto를 기반으로 파일 다운로드 응답 생성
     *
     * @param binaryContent 다운로드할 파일 정보
     * @return 파일이 포함된 HTTP 다운로드 응답
     */
    @Override
    public ResponseEntity<Resource> download(BinaryContentDto binaryContent) {

        InputStream stream = get(binaryContent.id());
        Resource resource = (Resource) new InputStreamResource(stream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition
                .builder("attachment")
                .filename(binaryContent.fileName())
                .build());

        headers.setContentType(MediaType.parseMediaType(binaryContent.contentType()));

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
