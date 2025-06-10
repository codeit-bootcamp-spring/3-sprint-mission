package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root = Paths.get("uploads");

  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      throw new RuntimeException("Failed to initialize local storage directory", e);
    }
  }

  @Override
  public UUID put(UUID id, byte[] data) {
    try {
      Files.write(resolvePath(id), data);
      return id;
    } catch (IOException e) {
      throw new RuntimeException("Failed to write file for id: " + id, e);
    }
  }

  @Override
  public InputStream get(UUID id) {
    try {
      return Files.newInputStream(resolvePath(id));
    } catch (IOException e) {
      throw new RuntimeException("Failed to read file for id: " + id, e);
    }
  }


  @Override
  public ResponseEntity<Resource> download(BinaryContentDto dto) {
    try {
      File file = resolvePath(dto.id()).toFile();
      InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(dto.contentType()))
          .header(HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=\"" + dto.fileName() + "\"")
          .contentLength(dto.size())
          .body(resource);

    } catch (IOException e) {
      log.error("File download failed for id: {}", dto.id(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @Override
  public void delete(UUID id) {
    try {
      Files.deleteIfExists(resolvePath(id));
    } catch (IOException e) {
      throw new RuntimeException("Failed to delete file for id: " + id, e);
    }
  }

  private Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }
}
