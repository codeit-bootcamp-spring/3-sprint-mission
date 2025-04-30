package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.entitiy.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "File")
public class FileUserStatusRepository implements UserStatusRepository {

    private static final Path FILE_PATH = Paths.get("src/main/java/com/sprint/mission/discodeit/repository/file/data/userstatuses.ser");

    //File*Repository에서만 사용, 파일을 읽어들여 리스트 반환
    public List<UserStatus> readFiles() {
        try {
            if (!Files.exists(FILE_PATH) || Files.size(FILE_PATH) == 0) {
                return new ArrayList<>();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<UserStatus> userStatuses = new ArrayList<>();
        try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(FILE_PATH.toFile()))) {
            while(true) {
                try {
                    userStatuses.add((UserStatus) reader.readObject());
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return userStatuses;
    }


    //File*Repository에서만 사용, 만들어 놓은 리스트를 인자로 받아 파일에 쓰기
    public void writeFiles(List<UserStatus> userStatuses) {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(FILE_PATH.toFile()))) {
                for (UserStatus readStatus : userStatuses) {
                    writer.writeObject(readStatus);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public UserStatus save(UserStatus userStatus) {
        List<UserStatus> userStatuses = readFiles();
        userStatuses.add(userStatus);
        writeFiles(userStatuses);
        return userStatus;
    }

    @Override
    public List<UserStatus> read() {
        List<UserStatus> userStatuses = readFiles();
        return userStatuses;
    }

    @Override
    public Optional<UserStatus> readById(UUID id) {
        List<UserStatus> userStatuses = readFiles();
        Optional<UserStatus> userStatus = userStatuses.stream()
                .filter((u)->u.getId().equals(id))
                .findAny();
        return userStatus;
    }

    @Override
    public Optional<UserStatus> readByUserId(UUID userId) {
        List<UserStatus> userStatuses = readFiles();
        Optional<UserStatus> userStatus = userStatuses.stream()
                .filter((u)->u.getUserId().equals(userId))
                .findAny();
        return userStatus;
    }

    @Override
    public void update(UUID id, UserStatus userStatus) {
        List<UserStatus> userStatuses = readFiles();
        userStatuses.stream()
                .filter((c)->c.getId().equals(id))
                .forEach((c)->{
                    c.setUpdatedAt(Instant.now());
                    c.setUserId(userStatus.getUserId());
                });
        writeFiles(userStatuses);
    }

    @Override
    public void delete(UUID userStatusId) {
        List<UserStatus> userStatuses = readFiles();
        userStatuses.removeIf(userStatus -> userStatus.getId().equals(userStatusId));
        writeFiles(userStatuses);
    }
}
