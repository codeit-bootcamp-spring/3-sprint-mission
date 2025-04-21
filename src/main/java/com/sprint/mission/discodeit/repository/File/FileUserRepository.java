package com.sprint.mission.discodeit.repository.File;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileUserRepository implements UserRepository {

    private final Path userDir = Paths.get(System.getProperty("user.dir"), "data", "users");

    public FileUserRepository() {
        try {
            if (!Files.exists(userDir)) {
                Files.createDirectories(userDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("유저 디렉토리 생성 실패", e);
        }
    }

    @Override
    public void save(User user) {
        Path filePath = userDir.resolve(user.getId().toString().concat(".ser"));
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("유저 저장 실패", e);
        }
    }

    @Override
    public User findById(UUID id) {
        Path filePath = userDir.resolve(id.toString().concat(".ser"));
        if (!Files.exists(filePath)) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("유저 로딩 실패", e);
        }
    }

    @Override
    public List<User> findAll() {
        if (!Files.exists(userDir)) return new ArrayList<>();

        try {
            return Files.list(userDir)
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            return (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("유저 로딩 실패", e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("유저 목록 불러오기 실패", e);
        }
    }

    @Override
    public void deleteById(UUID id) {
        Path filePath = userDir.resolve(id.toString().concat(".ser"));
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("유저 삭제 실패", e);
        }
    }
}








