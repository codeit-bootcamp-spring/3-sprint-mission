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

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {

    private final Path dataDirectory;

    public FileUserStatusRepository(@Value("${discodeit.repository.file-directory}") String baseDir) {
        this.dataDirectory = Paths.get(System.getProperty("user.dir"), baseDir, "userstatuses");
        if (!Files.exists(dataDirectory)) {
            try {
                Files.createDirectories(dataDirectory);
            } catch (IOException e) {
                throw new RuntimeException("사용자 상태 데이터 디렉토리 생성 실패", e);
            }
        }
    }

    private Path getUserStatusPath(UUID id) {
        return dataDirectory.resolve(id.toString() + ".ser");
    }

    private void saveUserStatus(UserStatus userStatus) {
        Path userStatusPath = getUserStatusPath(userStatus.getId());
        try (FileOutputStream fos = new FileOutputStream(userStatusPath.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(userStatus);
        } catch (IOException e) {
            throw new RuntimeException("사용자 상태 저장 실패: " + userStatus.getId(), e);
        }
    }

    private UserStatus loadUserStatus(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile());
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (UserStatus) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("사용자 상태 로드 실패: " + path, e);
        }
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        saveUserStatus(userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        Path userStatusPath = getUserStatusPath(id);
        if (Files.exists(userStatusPath)) {
            return Optional.of(loadUserStatus(userStatusPath));
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
         try (Stream<Path> pathStream = Files.list(dataDirectory)) {
            return pathStream
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(this::loadUserStatus)
                    .filter(status -> status.getUserId().equals(userId))
                    .findFirst();
        } catch (IOException e) {
            throw new RuntimeException("사용자 ID로 사용자 상태 로드 실패", e);
        }
    }

    @Override
    public List<UserStatus> findAll() {
        try (Stream<Path> pathStream = Files.list(dataDirectory)) {
            return pathStream
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(this::loadUserStatus)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("사용자 상태 목록 로드 실패", e);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return Files.exists(getUserStatusPath(id));
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.deleteIfExists(getUserStatusPath(id));
        } catch (IOException e) {
            throw new RuntimeException("사용자 상태 삭제 실패: " + id, e);
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
         try (Stream<Path> pathStream = Files.list(dataDirectory)) {
            pathStream
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(this::loadUserStatus)
                    .filter(status -> status.getUserId().equals(userId))
                    .findFirst()
                    .ifPresent(status -> {
                        try {
                            Files.deleteIfExists(getUserStatusPath(status.getId()));
                        } catch (IOException e) {
                            // Log or handle the exception for individual file deletion failure
                            System.err.println("Failed to delete user status file for user: " + userId + ", Error: " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("사용자 ID로 사용자 상태 삭제 실패", e);
        }
    }
} 