package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.util.FileSaveManager;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserStatusRepository implements UserStatusRepository {

    @Value("${discodeit.repository.file-directory}")
    private String FILE_DIRECTORY;
    private final String FILENAME = "userStatusRepo.ser";
    private final Map<UUID, UserStatus> data = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        Map<UUID, UserStatus> loaded = FileSaveManager.loadFromFile(getFile());
        if (loaded != null) {
            data.putAll(loaded);
        }
    }

    private File getFile() {
        return new File(FILE_DIRECTORY, FILENAME);
    }

    @Override
    public void save(UserStatus userStatus) {
        data.put(userStatus.getId(), userStatus);

        FileSaveManager.saveToFile(getFile(), data);
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

        FileSaveManager.saveToFile(getFile(), data);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        data.entrySet().removeIf(entry -> entry.getValue().getUserId().equals(userId));

        FileSaveManager.saveToFile(getFile(), data);
    }
}
