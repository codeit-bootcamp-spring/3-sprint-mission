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
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

public class FileUserRepository implements UserRepository {

    private final Path dataDirectory;
    private final Object lock = new Object();

    public FileUserRepository() {
        this.dataDirectory = Paths.get(System.getProperty("user.dir"), "data", "users");
        if (!Files.exists(dataDirectory)) {
            try {
                Files.createDirectories(dataDirectory);
            } catch (IOException e) {
                throw new RuntimeException("사용자 데이터 디렉토리 생성 실패", e);
            }
        }
    }

    private Path getUserPath(UUID userId) {
        return dataDirectory.resolve(userId.toString() + ".ser");
    }

    private void saveUser(User user) {
        Path userPath = getUserPath(user.getUserId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(userPath.toFile()))) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("사용자 저장 실패: " + user.getUserId(), e);
        }
    }

    private User loadUser(Path path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("사용자 로드 실패: " + path, e);
        }
    }

    @Override
    public User save(User user) {
        synchronized (lock) {
            saveUser(user);
            return user;
        }
    }

    @Override
    public User findById(UUID userId) {
        synchronized (lock) {
            Path userPath = getUserPath(userId);
            if (Files.exists(userPath)) {
                return loadUser(userPath);
            }
            return null;
        }
    }

    @Override
    public List<User> findAll() {
        try (Stream<Path> pathStream = Files.list(dataDirectory)) {
            return pathStream
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(this::loadUser)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("사용자 목록 로드 실패", e);
        }
    }

    @Override
    public void deleteById(UUID userId) {
        synchronized (lock) {
            try {
                Files.deleteIfExists(getUserPath(userId));
            } catch (IOException e) {
                throw new RuntimeException("사용자 삭제 실패: " + userId, e);
            }
        }
    }
}
