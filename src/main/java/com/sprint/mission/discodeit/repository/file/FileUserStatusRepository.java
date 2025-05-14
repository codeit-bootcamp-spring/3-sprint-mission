package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserStatusRepository implements UserStatusRepository {

    @Value("${discodeit.repository.file-directory}")
    private String FILE_DIRECTORY;
    private final String FILENAME = "userStatusRepo.ser";
    private final Map<UUID, UserStatus> data = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        data.putAll(loadFile());
    }

    private File getFile() {
        return new File(FILE_DIRECTORY, FILENAME);
    }

    @Override
    public void save(UserStatus userStatus) {
        data.put(userStatus.getId(), userStatus);

        saveFile();
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        Optional<UserStatus> foundUserStatus = data.entrySet().stream()
                .filter(entry -> entry.getKey().equals(id))
                .map(Map.Entry::getValue)
                .findFirst();

        return foundUserStatus;
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        Optional<UserStatus> foundUserStatus = data.values().stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .findFirst();

        return foundUserStatus;
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);

        saveFile();
    }

    @Override
    public void deleteByUserId(UUID userId) {
        data.entrySet().removeIf(entry -> entry.getValue().getUserId().equals(userId));

        saveFile();
    }

    private Map<UUID, UserStatus> loadFile() {
        Map<UUID, UserStatus> data = new HashMap<>();

        if (getFile().exists()) {
            try (FileInputStream fis = new FileInputStream(getFile());
                 ObjectInputStream in = new ObjectInputStream(fis)) {
                data = (Map<UUID, UserStatus>)in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return data;
    }

    private synchronized void saveFile() {
        try (FileOutputStream fos = new FileOutputStream(getFile());
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
