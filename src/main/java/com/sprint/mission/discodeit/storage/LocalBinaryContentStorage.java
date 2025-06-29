package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.FileUploadFailedException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;

  public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String rootPath) {
    this.root = Paths.get(rootPath);
  }

  @PostConstruct
  public void init() {
    try {
      if (!Files.exists(root)) {
        Files.createDirectories(root);
        log.info("로컬 저장소 디렉토리 생성: {}", root.toAbsolutePath());
      }
    } catch (IOException e) {
      throw new IllegalStateException("로컬 저장소 디렉토리 초기화 실패: " + root.toAbsolutePath(), e);
    }
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    try {
      Path filePath = resolvePath(id);
      Files.write(filePath, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      log.debug("파일 저장 완료: {} ({}bytes)", filePath.toAbsolutePath(), bytes.length);
      return id;
    } catch (IOException e) {
      throw FileUploadFailedException.withCause(e);
    }
  }

  @Override
  public InputStream get(UUID id) {
    try {
      Path filePath = resolvePath(id);
      if (!Files.exists(filePath)) {
        throw BinaryContentNotFoundException.withBinaryContentId(id);
      }
      return Files.newInputStream(filePath);
    } catch (IOException e) {
      throw BinaryContentNotFoundException.withBinaryContentId(id);
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
    try {
      InputStream inputStream = get(binaryContentDto.id());
      Resource resource = new InputStreamResource(inputStream);

      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + binaryContentDto.fileName() + "\"");
      headers.setContentType(MediaType.parseMediaType(binaryContentDto.contentType()));
      headers.setContentLength(binaryContentDto.size());

      return ResponseEntity.ok()
          .headers(headers)
          .body(resource);
    } catch (Exception e) {
      throw BinaryContentNotFoundException.of();
    }
  }

  /**
   * 파일의 실제 저장 위치에 대한 규칙을 정의
   * 파일 저장 위치 규칙: {root}/{UUID}
   */
  private Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }
}