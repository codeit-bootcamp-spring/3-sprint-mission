package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileUserService implements UserService {
    private Path directory;

    private void initDirectory() {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException("디렉토리 생성 실패: " + e);
            }
        }
    }

    public FileUserService(Path directory) {
        this.directory = directory;
        initDirectory();
    }

    private Path resolvePath(UUID userId) {
        return directory.resolve(userId.toString().concat(".ser"));
    }

    // 저장 로직
    private void saveFile(User user) {
        try (
                FileOutputStream fos = new FileOutputStream(resolvePath(user.getId()).toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("유저 저장 실패: " + e);
        }
    }

    // 저장 로직
    private Optional<User> loadFile(UUID userId) {
        if (Files.exists(resolvePath(userId))) {
            try (
                    FileInputStream fis = new FileInputStream(resolvePath(userId).toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                return Optional.of((User) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    // 비즈니스 로직
    @Override
    public User createUser(User user) {
        saveFile(user);
        return user;
    }

    // 비즈니스 로직
    @Override
    public Optional<User> getUser(UUID userId) {
        return loadFile(userId);
    }

    // 비즈니스 로직
    @Override
    public List<User> getAllUsers() {
        if (Files.exists(directory)) {
            try {
                return Files.list(directory)
                        .filter(path -> path.toString().endsWith(".ser"))
                        .map(path -> {
                            try (
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis)
                            ) {
                                return (User) ois.readObject();
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException("유저 파일 로딩 실패: " + path, e);
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

    // 비즈니스 로직
    @Override
    public void updateUser(User user) {
        saveFile(user);
    }

    // 비즈니스 로직
    @Override
    public void deleteUser(UUID userId) {
        try {
            Files.deleteIfExists(resolvePath(userId));
        } catch (IOException e) {
            throw new RuntimeException("유저 삭제 실패: " + userId, e);
        }
    }
}
