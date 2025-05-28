package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;

  public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") Path path) {
    this.root = path;
    init();
  }

  public void init() {
    if (!Files.exists(root)) {
      try {
        Files.createDirectories(root);
      } catch (Exception e) {
        throw new RuntimeException("Failed to create root directory for local storage", e);
      }
    }
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    try (
        FileOutputStream fileOutputStream = new FileOutputStream(resolvePath(id).toFile());
    ) {
      fileOutputStream.write(bytes);
    } catch (IOException e) {
      throw new RuntimeException("Failed to write binary content to local storage", e);
    }
    return id;
  }

  @Override
  public InputStream get(UUID id) {
    Path path = resolvePath(id);
    if (!Files.exists(path)) {
      throw new RuntimeException("Binary content with id " + id + " not found");
    }
    try {
      return new FileInputStream(resolvePath(id).toFile());
    } catch (IOException e) {
      throw new RuntimeException("Failed to read binary content from local storage", e);
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentResponse binaryContentResponse) {
    InputStream inputStream = get(binaryContentResponse.id());
    InputStreamResource resource = new InputStreamResource(inputStream);

    return ResponseEntity.status(HttpStatus.OK)
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\""
                + binaryContentResponse.fileName() + "\"")
        .contentType(MediaType.valueOf(binaryContentResponse.contentType()))
        .body(resource);
  }

  private Path resolvePath(UUID uuid) {
    return root.resolve(uuid.toString());
  }

  private String getExtension(String contentType) {
    if (contentType == null || !contentType.contains("/")) {
      return "";
    }
    return "." + contentType.substring(contentType.lastIndexOf("/") + 1);
  }

}
