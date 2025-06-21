package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDTO;
import jakarta.annotation.PostConstruct;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
@Component
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;

  public LocalBinaryContentStorage(
      @Value("${discodeit.storage.local.root-path}") Path root
  ) {
    this.root = root;
  }

  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      throw new RuntimeException("Could not create directory: " + root, e);
    }
  }

  private Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    Path path = resolvePath(id);
    if (Files.exists(path)) {
      throw new IllegalArgumentException("File with key " + id + " already exists");
    }
    try (OutputStream outputStream = Files.newOutputStream(path)) {
      outputStream.write(bytes);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return id;
  }

  @Override
  public InputStream get(UUID id) {
    Path path = resolvePath(id);
    if (Files.notExists(path)) {
      throw new NoSuchElementException("File with key " + id + " does not exist");
    }
    try {
      return Files.newInputStream(path);
    } catch (IOException e) {
      throw new RuntimeException("Failed to read file", e);
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDTO binaryContentDTO) {
    try {
      InputStream inputStream = get(binaryContentDTO.id());
      InputStreamResource resource = new InputStreamResource(inputStream);

      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=\"" + binaryContentDTO.fileName() + "\"")
          .header(HttpHeaders.CONTENT_TYPE, binaryContentDTO.contentType())
          .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(binaryContentDTO.size()))
          .body(resource);
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }
}
