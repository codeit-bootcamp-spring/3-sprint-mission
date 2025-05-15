package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserRepository implements UserRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserRepository(@Value("${discodeit.repository.file-directory}") String directory) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), directory, User.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException("User 저장 디렉토리 생성 중 오류 발생", e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public User save(User user) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(resolvePath(user.getId()).toFile()))) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("User 저장 중 오류 발생", e);
        }
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                return Optional.of((User) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("User 로드 중 오류 발생", e);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(p -> p.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            return (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("User 전체 조회 중 오류 발생", e);
                        }
                    }).toList();
        } catch (IOException e) {
            throw new RuntimeException("디렉토리 로드 오류", e);
        }
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        try {
            return Files.list(DIRECTORY)
                    .filter(p -> p.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            return (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("User 전체 조회 중 오류 발생", e);
                        }
                    })
                    .filter(u -> u.getUsername().equals(userName))
                    .findFirst();
        } catch (IOException e) {
            throw new RuntimeException("디렉토리 로드 오류", e);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return Files.exists(resolvePath(id));
    }

    @Override
    public boolean existsByUsername(String username) {
        return findAll().stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }

    @Override
    public boolean existsByEmail(String email) {
        return findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.deleteIfExists(resolvePath(id));
        } catch (IOException e) {
            throw new RuntimeException("User 삭제 중 오류 발생", e);
        }
    }
}