package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
@Getter
@Setter
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;


  public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") Path root) {
    System.out.println(root);
    System.out.println(" root.getClass()  = " + root.getClass());
    this.root = root;
  }

  @PostConstruct
  public void init() {
    System.out.println("calling PostConsturct : " + this.root);
    if (!Files.exists(this.root)) {
      try {
        Files.createDirectories(this.root);
      } catch (IOException e) {
        // TODO : 다른 에러로 변경
        throw new RuntimeException(e);
      }
    }
  }

  private Path resolvePath(UUID id) {
    return this.root.resolve(String.valueOf(id) + ".ser");
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    System.out.println("calling `LocalBinaryContentStorage` put : uuid( " + id + " ) ");

    // 객체를 저장할 파일 path 생성
    Path filePath = this.resolvePath(id);

    try (
        // 파일과 연결되는 스트림 생성
        FileOutputStream fos = new FileOutputStream(filePath.toFile());
    ) {
      fos.write(bytes);
      return id;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public InputStream get(UUID id) {
    System.out.println("calling `LocalBinaryContentStorage` get : uuid( " + id + " ) ");

    Path filePath = this.resolvePath(id);

    try (
        // 파일과 연결되는 스트림 생성
        FileInputStream fis = new FileInputStream(String.valueOf(filePath));
    ) {
      return fis;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
    return null;
  }
}
