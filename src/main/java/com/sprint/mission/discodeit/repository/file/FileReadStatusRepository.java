package com.sprint.mission.discodeit.repository.file;

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
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {

    private final Path dataDirectory;

    public FileReadStatusRepository(@Value("${discodeit.repository.file-directory}") String baseDir) {
        this.dataDirectory = Paths.get(System.getProperty("user.dir"), baseDir, "readstatuses");
        if (!Files.exists(dataDirectory)) {
            try {
                Files.createDirectories(dataDirectory);
            } catch (IOException e) {
                throw new RuntimeException("읽기 상태 데이터 디렉토리 생성 실패", e);
            }
        }
    }

    private Path getReadStatusPath(UUID id) {
        return dataDirectory.resolve(id.toString() + ".ser");
    }

    private void saveReadStatus(ReadStatus readStatus) {
        Path readStatusPath = getReadStatusPath(readStatus.getId());
        try (FileOutputStream fos = new FileOutputStream(readStatusPath.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(readStatus);
        } catch (IOException e) {
            throw new RuntimeException("읽기 상태 저장 실패: " + readStatus.getId(), e);
        }
    }

    private ReadStatus loadReadStatus(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile());
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (ReadStatus) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("읽기 상태 로드 실패: " + path, e);
        }
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        saveReadStatus(readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        Path readStatusPath = getReadStatusPath(id);
        if (Files.exists(readStatusPath)) {
            return Optional.of(loadReadStatus(readStatusPath));
        }
        return Optional.empty();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
         try (Stream<Path> pathStream = Files.list(dataDirectory)) {
            return pathStream
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(this::loadReadStatus)
                    .filter(status -> status.getUserId().equals(userId))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("사용자별 읽기 상태 목록 로드 실패", e);
        }
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        try (Stream<Path> pathStream = Files.list(dataDirectory)) {
            return pathStream
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(this::loadReadStatus)
                    .filter(status -> status.getChannelId().equals(channelId))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("채널별 읽기 상태 목록 로드 실패", e);
        }
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        try (Stream<Path> pathStream = Files.list(dataDirectory)) {
            return pathStream
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(this::loadReadStatus)
                    .filter(status -> status.getUserId().equals(userId) && status.getChannelId().equals(channelId))
                    .findFirst();
        } catch (IOException e) {
            throw new RuntimeException("사용자 및 채널별 읽기 상태 로드 실패", e);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return Files.exists(getReadStatusPath(id));
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.deleteIfExists(getReadStatusPath(id));
        } catch (IOException e) {
            throw new RuntimeException("읽기 상태 삭제 실패: " + id, e);
        }
    }
    
    @Override
    public void deleteAllByChannelId(UUID channelId) {
        try (Stream<Path> pathStream = Files.list(dataDirectory)) {
            pathStream
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(this::loadReadStatus)
                    .filter(status -> status.getChannelId().equals(channelId))
                    .forEach(status -> {
                        try {
                            Files.deleteIfExists(getReadStatusPath(status.getId()));
                        } catch (IOException e) {
                            // Log or handle the exception for individual file deletion failure
                             System.err.println("Failed to delete read status file: " + getReadStatusPath(status.getId()) + ", Error: " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("채널별 읽기 상태 전체 삭제 실패", e);
        }
    }
} 