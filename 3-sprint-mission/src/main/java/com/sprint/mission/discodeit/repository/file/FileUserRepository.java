package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FileUserRepository implements UserRepository {
    private static final Path DIRECTORY = Paths.get(System.getProperty("user.dir"), "data", "user");

    public FileUserRepository() {
        init();
    }

    // 저장할 경로의 파일 초기화
    public static Path init() {
        if (!Files.exists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return DIRECTORY;
    }

    public static User load(Path filePath) {
        if (!Files.exists(filePath)) {
            return null;
        }
        try (
                FileInputStream fis = new FileInputStream(filePath.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object data = ois.readObject();
            return (User) data;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("파일 로딩 실패", e);
        }
    }

    @Override
    public User save(User user) {
        Path filePath = Paths.get(String.valueOf(DIRECTORY), user.getId()+".ser");
        try(
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        if (!Files.exists(DIRECTORY)) {
            return new ArrayList<>();
        } else {
            List<User> data = new ArrayList<>();
            File[] files = DIRECTORY.toFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    data.add(load(file.toPath()));
                }
            }
            return data;
        }
    }

    @Override
    public User find(UUID id) {
        return load(Paths.get(String.valueOf(DIRECTORY), id+".ser"));
    }

    @Override
    public User findByUsername(String username) {
        return findAll().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public List<User> findByName(String name) {
        return findAll().stream()
                .filter(u -> u.getName().contains(name))
                .collect(Collectors.toList());
    }

    @Override
    public User findByEmail(String email) {
        return findAll().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public boolean existsId(UUID id) {
        return Files.exists(Paths.get(String.valueOf(DIRECTORY), id+".ser"));
    }

    @Override
    public boolean existsUsername(String username) {
        return findAll().stream().anyMatch(u -> u.getUsername().equals(username));
    }

    @Override
    public boolean existsEmail(String email) {
        return findAll().stream().anyMatch(u -> u.getEmail().equals(email));
    }

    @Override
    public boolean existsName(String name) {
        return findAll().stream().anyMatch(u -> u.getName().equals(name));
    }

    @Override
    public void delete(UUID id) throws IOException {
        Files.delete(Paths.get(String.valueOf(DIRECTORY), id+".ser"));
    }
}
