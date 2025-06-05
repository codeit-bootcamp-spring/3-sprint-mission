package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.entity.BinaryContent;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  @Value(value = "${discodeit.storage.local.root-path}")
  Path root;

  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      throw new RuntimeException("스토리지 초기화 실패: ", e);
    }
  }

  @Override
  public UUID put(UUID id, byte[] content) {
    try {
      Files.createDirectories(root);
      Path filePath = root.resolve(id.toString());
      Files.write(filePath, content, StandardOpenOption.CREATE);
      return id;
    } catch (IOException e) {
      throw new UncheckedIOException("파일 저장 실패: ", e);
    }
  }

  @Override
  public InputStream get(UUID id) {
    try {
      Path filePath = root.resolve(id.toString());
      return Files.newInputStream(filePath);
    } catch (IOException e) {
      throw new UncheckedIOException("파일 조회 실패: ", e);
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContent dto) {
    try {
      Path filePath = root.resolve(dto.getId().toString());
      Resource resource = new FileSystemResource(filePath);
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=\"" + dto.getFileName() + "\"")
          .contentType(MediaType.parseMediaType(dto.getContentType()))
          .contentLength(dto.getSize())
          .body(resource);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
