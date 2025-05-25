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

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

@Repository
public class FileUserRepository implements UserRepository {

    private final Path dataDirectory;
    private final Object lock = new Object();

    public FileUserRepository(@Value("${discodeit.repository.file-directory}") String baseDir) {
        this.dataDirectory = Paths.get(System.getProperty("user.dir"), baseDir, "users");
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
        // 기존 코드에서는 FileOutputStream이 즉시 생성되고 ObjectOutputStream의 생성자에 전달됨.
        // 만약 ObjectOutputStream 생성 중 예외가 발생하면, FileOutputStream이 제대로 닫히지 않을 수 있음. 이러면
        // 메모리 누수 및 파일 핸드 고갈 가능성 있음.
        Path userPath = getUserPath(user.getUserId());
        try (FileOutputStream fos = new FileOutputStream(userPath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("사용자 저장 실패: " + user.getUserId(), e);
        }
    }

    private User loadUser(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)) {
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
    public Optional<User> findById(UUID userId) {
        synchronized (lock) {
            Path userPath = getUserPath(userId);
            if (Files.exists(userPath)) {
                return Optional.of(loadUser(userPath));
            }
            return Optional.empty();
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

    @Override
    public Optional<User> findByUsername(String username) {
        synchronized (lock) {
            return findAll().stream()
                    .filter(user -> user.getUsername().equals(username))
                    .findFirst();
        }
    }

    @Override
    public boolean existsById(UUID id) {
        synchronized (lock) {
            return Files.exists(getUserPath(id));
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        synchronized (lock) {
            return findAll().stream()
                    .anyMatch(user -> user.getEmail().equals(email));
        }
    }
}
