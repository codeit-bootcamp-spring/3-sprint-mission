package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
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
public class FileReadStatusRepository implements ReadStatusRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileReadStatusRepository(@Value("${discodeit.repository.file-directory}") String directory) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), directory, ReadStatus.class.getSimpleName());
        try {
            Files.createDirectories(DIRECTORY);
        } catch (IOException e) {
            throw new RuntimeException("ReadStatus 디렉토리 생성 오류", e);
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(resolvePath(readStatus.getId()).toFile()))) {
            oos.writeObject(readStatus);
        } catch (IOException e) {
            throw new RuntimeException("ReadStatus 저장 오류", e);
        }
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                return Optional.of((ReadStatus) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("ReadStatus 읽기 오류", e);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return findAll().stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(r -> r.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    private List<ReadStatus> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(p -> p.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            return (ReadStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("ReadStatus 전체 읽기 오류", e);
                        }
                    }).toList();
        } catch (IOException e) {
            throw new RuntimeException("ReadStatus 디렉토리 탐색 오류", e);
        }
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
            throw new RuntimeException("ReadStatus 삭제 오류", e);
        }
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        findAllByChannelId(channelId).forEach(r -> deleteById(r.getId()));
    }
}