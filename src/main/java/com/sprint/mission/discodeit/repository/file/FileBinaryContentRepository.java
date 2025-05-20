package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {

  private final Path DIRECTORY;
  private final String EXTENSION = ".ser";

  public FileBinaryContentRepository(
      @Value("${discodeit.repository.file-directory:data}") String fileDirectory
  ) {
    this.DIRECTORY = Paths.get(System.getProperty("user.dir"), fileDirectory,
        BinaryContent.class.getSimpleName());
    if (Files.notExists(DIRECTORY)) {
      try {
        Files.createDirectories(DIRECTORY);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private Path resolvePath(UUID id) {
    return DIRECTORY.resolve(id + EXTENSION);
  }

  @Override
  public BinaryContent save(BinaryContent binaryContent) {
    Path path = resolvePath(binaryContent.getId());
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
      oos.writeObject(binaryContent);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return binaryContent;
  }

  @Override
  public Optional<BinaryContent> findById(UUID id) {
    Path path = resolvePath(id);
    if (!Files.exists(path)) {
      return Optional.empty();
    }
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
      return Optional.of((BinaryContent) ois.readObject());
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteById(UUID id) {
    Path path = resolvePath(id);
    try {
      Files.deleteIfExists(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
    return loadAll().stream()
        .filter(content -> ids.contains(content.getId()))
        .collect(Collectors.toList());
  }

  @Override
  public boolean existsById(UUID id) {
    Path path = resolvePath(id);
    return Files.exists(path);
  }

  private List<BinaryContent> loadAll() {
    try (Stream<Path> paths = Files.list(DIRECTORY)) {
      return paths
          .filter(path -> path.toString().endsWith(EXTENSION))
          .map(path -> {
            try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(path.toFile()))) {
              return (BinaryContent) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
              throw new RuntimeException(e);
            }
          })
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}