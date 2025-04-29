package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class FileUserRepository implements UserRepository {
    private final Path directory;

    public FileUserRepository(Path directory) {
        this.directory = directory;
        initDirectory();
    }

    private void initDirectory() {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException("디렉토리 생성 실패: " + e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return directory.resolve(id.toString().concat(".ser"));
    }

    private void saveFile(User user) {
        try (FileOutputStream fos = new FileOutputStream(resolvePath(user.getId()).toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("유저 저장 실패: " + e);
        }
    }

    private Optional<User> loadFile(UUID id) {
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (FileInputStream fis = new FileInputStream(path.toFile());
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                return Optional.of((User) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    public User save(User user) {
        saveFile(user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return loadFile(id);
    }

    @Override
    public List<User> findAll() {
        if (Files.exists(directory)) {
            try {
                return Files.list(directory)
                        .filter(path -> path.toString().endsWith(".ser"))
                        .map(path -> {
                            try (FileInputStream fis = new FileInputStream(path.toFile());
                                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                                return (User) ois.readObject();
                            } catch (IOException | ClassNotFoundException e) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .toList();
            } catch (IOException e) {
                throw new RuntimeException("유저 전체 조회 실패", e);
            }
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.deleteIfExists(resolvePath(id));
        } catch (IOException e) {
            throw new RuntimeException("유저 삭제 실패: " + id, e);
        }
    }
}
