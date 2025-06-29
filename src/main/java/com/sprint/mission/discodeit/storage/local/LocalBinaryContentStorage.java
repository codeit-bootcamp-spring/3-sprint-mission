package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.NoSuchElementException;
import java.util.UUID;

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

    /**
     * 주어진 UUID를 기반으로 파일 경로를 생성하고, 해당 경로에 바이트 데이터를 파일로 저장합니다.
     * <p>
     * 파일이 이미 존재하는 경우 {@link RuntimeException}을 발생시켜 중복 저장을 방지합니다.
     * 파일은 새로 생성되며, OutputStream을 사용하여 데이터를 안전하게 씁니다.
     * </p>
     *
     * @param binaryContentId 파일을 식별할 UUID
     * @param bytes 저장할 데이터의 바이트 배열
     * @return 저장된 파일의 UUID (binaryContentId 그대로 반환)
     * @throws RuntimeException 파일이 이미 존재하거나, 파일 쓰기 중 IOException이 발생한 경우
     */
    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        Path filePath = resolvePath(binaryContentId);
        if (Files.exists(filePath)) {
            throw new RuntimeException("File already exists: " + filePath.toAbsolutePath());
        }
        try (OutputStream outputStream = Files.newOutputStream(filePath, StandardOpenOption.CREATE))
        {
            outputStream.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return binaryContentId;
    }

    /**
     * 파일 시스템에서 바이너리 콘텐츠를 읽어 InputStream으로 제공하는 서비스 메소드입니다.
     * <p>
     * 지정된 UUID를 기반으로 파일 경로를 생성하고,
     * 해당 파일이 존재하면 InputStream을 반환하여 호출자가 파일 데이터를 읽을 수 있도록 합니다.
     * 파일이 존재하지 않으면 NoSuchElementException을 발생시킵니다.
     * </p>
     *
     * <p><b>주의:</b> 반환된 InputStream은 호출자가 닫아야 합니다.
     * (try-with-resources 또는 finally에서 close 처리 권장)</p>
     *
     * @param binaryContentId 바이너리 콘텐츠 파일의 UUID
     * @return 파일 데이터에 접근할 수 있는 InputStream
     * @throws NoSuchElementException 파일이 존재하지 않을 경우 발생
     * @throws RuntimeException InputStream 생성 중 IOException이 발생한 경우
     */
    @Override
    public InputStream get(UUID binaryContentId) {
        Path filePath = resolvePath(binaryContentId);
        if (!Files.exists(filePath)) {
            throw new NoSuchElementException("File does not exist: " + filePath.toAbsolutePath());
        }
        try {
            // InputStream을 열어 주고 반환한 뒤, 실제 데이터 읽기/처리는 호출자에게 맡김
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 파일 시스템에서 주어진 {@link BinaryContentDto} 메타데이터를 기반으로 파일을 다운로드할 수 있는 ResponseEntity를 생성합니다.
     * <p>
     * - 파일명을 UTF-8로 인코딩하여 Content-Disposition 헤더에 추가함으로써 국제화 파일명(한글, 특수문자 등)도 안전하게 다운로드됩니다. <br>
     * - Content-Type은 메타데이터에서 제공된 실제 MIME 타입으로 설정됩니다. <br>
     * - Content-Length는 메타데이터의 파일 크기로 지정됩니다. <br>
     * - Content-Disposition은 attachment로 설정되어 브라우저가 파일을 다운로드하도록 유도합니다.
     * </p>
     *
     * <p><b>주의:</b> 반환된 ResponseEntity의 Resource(InputStreamResource)는 호출자가 close를 신경 쓸 필요가 없습니다.
     * 스프링 프레임워크에서 응답 후 자동으로 InputStream을 닫습니다.</p>
     *
     * @param metaData 다운로드할 파일의 메타데이터 (파일명, 타입, 크기, ID 포함)
     * @return 다운로드용 ResponseEntity (Content-Disposition, Content-Type, Content-Length 포함)
     * @throws NoSuchElementException 파일이 존재하지 않을 경우 (get 메소드 내부에서 발생)
     * @throws RuntimeException 파일 읽기 중 IOException 발생 시 (get 메소드 내부에서 발생)
     */
    @Override
    public ResponseEntity<Resource> download(BinaryContentDto metaData) {
        InputStream is = get(metaData.id());

        Resource resource = new InputStreamResource(is);

        String encodedFileName = UriUtils.encode(metaData.fileName(), StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename*=UTF-8''" + encodedFileName;

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
//                .contentType(MediaType.APPLICATION_OCTET_STREAM) // 사용자에게 파일을 강제 다운로드 시킴
                .header(HttpHeaders.CONTENT_TYPE, metaData.contentType())
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(metaData.size()))
                .body(resource);
    }

    /**
     * 주어진 UUID를 기반으로 파일 시스템 내의 파일 경로를 생성합니다.
     * <p>
     * 내부적으로 root 디렉토리를 기준으로 UUID를 문자열로 변환한 파일명을 결합하여 절대 경로를 만듭니다.
     * 주로 파일 저장, 조회, 삭제 등의 작업 시 파일 위치를 식별하는 데 사용됩니다.
     * </p>
     *
     * @param id 파일을 식별할 UUID
     * @return UUID를 기반으로 생성된 파일의 경로 (root 디렉토리 하위 경로)
     */
    public Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }
}

