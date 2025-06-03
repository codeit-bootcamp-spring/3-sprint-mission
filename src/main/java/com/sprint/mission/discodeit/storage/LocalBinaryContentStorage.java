package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;

  public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") Path rootPath) {
    this.root = rootPath;
    log.info("LocalBinaryContentStorage Created! - rootPath: {}", root.toAbsolutePath());
  }

  @PostConstruct
  public void init() {
    try {
      if (Files.notExists(root)) {
        Files.createDirectories(root);
        log.info("Storage Root Created: {}", root.toAbsolutePath());
      }
    } catch (IOException e) {
      throw new RuntimeException("Storage Root Creation Failed!!!", e);
    }
  }

  @Override
  public UUID put(UUID id, byte[] data) {
    Path path = resolvePath(id);
    try {
      Files.write(path, data);
      log.info("Binary Content Saved: {}", path.toAbsolutePath());
      return id;
    } catch (IOException e) {
      throw new RuntimeException("Binary Content Save Failed!!!", e);
    }
  }

  @Override
  public InputStream get(UUID id) {
    Path path = resolvePath(id);
    try {
      return Files.newInputStream(path);
    } catch (IOException e) {
      throw new RuntimeException("Binary Content Load Failed!!!", e);
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
    try {
      InputStream inputStream = get(binaryContentDto.id());
      InputStreamResource resource = new InputStreamResource(inputStream);

      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=\"" + binaryContentDto.fileName() + "\"")
          .contentType(MediaType.parseMediaType(binaryContentDto.contentType()))
          .contentLength(binaryContentDto.size())
          .body(resource);

    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }
}
