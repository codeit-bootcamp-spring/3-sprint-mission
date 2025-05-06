package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {
    private static final Path DIRECTORY = Paths.get(System.getProperty("user.dir"), "data", "userStatus");

    public FileUserStatusRepository() {
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

    public static UserStatus load(Path filePath) {
        if (!Files.exists(filePath)) {
            return null;
        }
        try (
                FileInputStream fis = new FileInputStream(filePath.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object data = ois.readObject();
            return (UserStatus) data;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("파일 로딩 실패", e);
        }
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        Path filePath = Paths.get(String.valueOf(DIRECTORY), userStatus.getId()+".ser");
        try(
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(userStatus);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return userStatus;
    }

    @Override
    public List<UserStatus> findAll() {
        if (!Files.exists(DIRECTORY)) {
            return new ArrayList<>();
        } else {
            List<UserStatus> data = new ArrayList<>();
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
    public UserStatus find(UUID id) {
        return load(Paths.get(String.valueOf(DIRECTORY), id+".ser"));
    }

    @Override
    public void delete(UUID id) throws IOException {
        Files.delete(Paths.get(String.valueOf(DIRECTORY), id+".ser"));
    }

    @Override
    public void deleteByUserId(UUID userId) throws IOException {
        UserStatus userStatus = findAll().stream()
                .filter(us -> us.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        Files.delete(Paths.get(String.valueOf(DIRECTORY), userStatus.getId()+".ser"));
    }
}
