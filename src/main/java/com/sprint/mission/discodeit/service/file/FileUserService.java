package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileUserService implements UserService {

    private final Path userDirectory;

    public FileUserService() {
        this.userDirectory = Paths.get(System.getProperty("user.dir"), "data", "users");
        init();
    }

    private void init() {
        try {
            if (!Files.exists(userDirectory)) {
                Files.createDirectories(userDirectory);
            }
        } catch (IOException e) {
            throw new RuntimeException("유저 저장 디렉토리 생성 실패", e);
        }
    }

    @Override
    public User create(User user) {
        save(user);
        return user;
    }

    @Override
    public User getById(UUID id) {
        Path filePath = userDirectory.resolve(id.toString().concat(".ser"));
        if (!Files.exists(filePath)) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<User> getAll() {
        if (!Files.exists(userDirectory)) return new ArrayList<>();

        try {
            return Files.list(userDirectory)
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            return (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User update(User user) {
        return create(user); // 동일한 ID로 덮어쓰기
    }

    @Override
    public void delete(UUID id) {
        Path filePath = userDirectory.resolve(id.toString().concat(".ser"));
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void save(User user) {
        Path filePath = userDirectory.resolve(user.getId().toString().concat(".ser"));
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}













