package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private Path root;

  public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path:data}") Path root) {
    this.root = root;
  }

  @PostConstruct
  public void init() {
    this.root = Paths.get(System.getProperty("user.dir"), root.toString(),
        BinaryContent.class.getSimpleName());
    if (Files.notExists(root)) {
      try {
        Files.createDirectories(root);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private Path resolvePath(UUID id) {
    return root.resolve(id + ".ser");
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    System.out.println("id = " + id);
    Path path = resolvePath(id);
    try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
      fos.write(bytes);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return id;
  }

  @Override
  public InputStream get(UUID id) {
    Path path = resolvePath(id);
    if (Files.notExists(path)) {
      throw new NoSuchElementException();
    }
    try {
      FileInputStream fis = new FileInputStream(path.toFile());
      return fis;

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
    byte[] bytes = binaryContentDto.getBytes();
    Resource resource = new ByteArrayResource(bytes);

    String filename = binaryContentDto.getFileName();
    String contentType = binaryContentDto.getContentType();

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
        .contentType(MediaType.parseMediaType(contentType))
        .contentLength(bytes.length)
        .body(resource);

  }

}
