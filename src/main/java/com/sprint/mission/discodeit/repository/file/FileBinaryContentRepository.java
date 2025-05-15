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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileBinaryContentRepository implements BinaryContentRepository {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileBinaryContentRepository(@Value("${discodeit.repository.file-directory}") String directory) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), directory, BinaryContent.class.getSimpleName());
        try {
            if (Files.notExists(DIRECTORY)) {
                Files.createDirectories(DIRECTORY);
            }
        } catch (IOException e) {
            throw new RuntimeException("BinaryContent 저장 디렉토리를 생성하는 중 오류가 발생했습니다.");
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
            throw new RuntimeException("BinaryContent 저장 중 오류가 발생하였습니다.");
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
            throw new RuntimeException("BinaryContent 로딩 중 오류가 발생하였습니다.");
        }
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(this::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(UUID id) {
        return Files.exists(resolvePath(id));
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.deleteIfExists(resolvePath(id));
        } catch (IOException e) {
            throw new RuntimeException("BinaryContent 삭제 중 오류가 발생하였습니다.");
        }
    }
}